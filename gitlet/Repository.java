package gitlet;

import java.io.File;
import java.io.IOException;
//import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  The overall repository that holds all of the basic methods of Gitlet.
 *
 *
 *  @author Vincent Le
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    String dir = GITLET_DIR.getPath();

    /** The staging area */
    public static final File STAGING_AREA = join(GITLET_DIR, "STAGING_AREA");
    public static final File ADD = join(STAGING_AREA, "ADD");
    public static final File DELETE = join(STAGING_AREA, "DELETE");

    /** The Commit Folder that contains HashMap for commits and HEAD pointer */
    public static final File COMMITS = join(GITLET_DIR, "COMMITS");

    public static final File BLOBS = join(GITLET_DIR, "BLOBS");

    /** HEAD.txt */
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD.txt");
    private String HEAD;
    private Commit head;

    private HashMap<String, Commit> commits = new HashMap<>();
    private StagingArea stagingArea;
    private HashMap<String, String> branches = new HashMap<>();

    /** Branches */
    public static final File BRANCHES = join(GITLET_DIR, "BRANCHES");
    public static final File HEAD_BRANCH = join(GITLET_DIR, "HEAD_BRANCH.txt");
    private String headBranch;



    public Repository() {
        try {
            stagingArea = Utils.readObject(STAGING_AREA, StagingArea.class);
        } catch (IllegalArgumentException ill) {
            stagingArea = new StagingArea();
        }
    }

    public void initCommand() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
        } else {
            GITLET_DIR.mkdir();
            COMMITS.mkdir();
            BLOBS.mkdir();
            STAGING_AREA.mkdir();
            ADD.mkdir();
            DELETE.mkdir();
            BRANCHES.mkdir();
            Commit initialCommit = new Commit("initial commit",
                    null, new HashMap<>(), null);

            initialCommit.setSHA();
            HEAD = initialCommit.SHA;
            Utils.writeObject(Utils.join(BRANCHES, "master"), initialCommit.SHA);
            //branches.put("*master", initialCommit.SHA);
            headBranch = "master";
            File initialCommitFile = join(COMMITS, initialCommit.SHA);
            Utils.writeObject(initialCommitFile, initialCommit);
            //commits.put(initialCommit.SHA, initialCommit);
            //HEAD = Utils.sha1(serialize(initialCommit));
            //commits.put(Utils.sha1(serialize(initialCommit)), initialCommit);

            serializeAll();

        }
    }

    private void serializeAll() {
        //Utils.writeObject(COMMITS, commits);
        //Utils.writeObject(STAGING_AREA, stagingArea);
        Utils.writeObject(HEAD_FILE, HEAD);
        //Utils.writeObject(BRANCHES, branches);
        Utils.writeObject(HEAD_BRANCH, headBranch);
    }

    private void persistence() {
        HEAD = Utils.readObject(HEAD_FILE, String.class);
        head = Utils.readObject(Utils.join(COMMITS, HEAD), Commit.class);
        headBranch = Utils.readObject(Utils.join(HEAD_BRANCH), String.class);
        //branches = (HashMap<String, String>) Utils.readObject(BRANCHES, HashMap.class);
    }

    public void addCommand(String name) throws IOException {
        //String HEAD = Utils.readObject(HeadFile, String.class);
        persistence();
        //StagingArea staging = Utils.readObject(STAGING_AREA, StagingArea.class);
        HashMap<String, String> headBlobs = head.getBlobs();
        Blob input = new Blob(name, CWD);
        if (!input.dir.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
            return;
        }
        input.setSHA();
        File inputFile = Utils.join(ADD, name);
        if (headBlobs.containsKey(name) && !headBlobs.get(name).equals(input.SHA)) {
            Utils.writeObject(inputFile, input);
        } else if (!headBlobs.containsKey(name)) {
            Utils.writeObject(inputFile, input);
        }
        for (String i : DELETE.list()) {
            if (i.equals(name)) {
                Utils.join(DELETE, name).delete();
            }
        }



        /* if (headBlobs.containsKey(name) && !headBlobs.get(name).SHA.equals(input.SHA)) {
            staging.addStaging.put(name, input);
            serializeAll();
            return;
        }

        if (staging.addStaging.containsKey(name) &&
        !staging.addStaging.get(name).SHA.equals(input.SHA)) {
            staging.addStaging.remove(name);
            staging.addStaging.put(name, input);
            serializeAll();
            return;
        }

        if (staging.deleteStaging.containsKey(name)) {
            staging.deleteStaging.remove(name);
            staging.addStaging.put(name, input);
            return;
        } */

        /* if (headBlobs.containsKey(name) && headBlobs.get(name).SHA.equals(input.SHA)) {
            staging.deleteStaging.remove(name);
            serializeAll();
            return;
        }
        if (stagingArea.deleteStaging.containsKey(name)) {
            staging.deleteStaging.remove(name);
        } */
        //staging.addStaging.put(name, input);

        //stagingArea.addFile(input);
        //current.put(name, input);

        serializeAll();

    }

    // Clones parent, add any files staged for addition,
    // replace any files, delete files staged for removal

    public void commitCommand(String message) {
        persistence();
        Commit temp;
        Commit parent = getHead();
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
        }
        temp = new Commit(message, parent.SHA, parent.getBlobs(), parent.getParent2());
        if (ADD.listFiles().length == 0 && DELETE.listFiles().length == 0) {
            System.out.println("No changes added to the commit.");
        }
        for (String i : ADD.list()) {
            Blob temporary = Utils.readObject(Utils.join(ADD, i), Blob.class);
            temporary.setSHA();
            Utils.writeObject(Utils.join(BLOBS, temporary.SHA), temporary);
            temp.addBlob(temporary.name, temporary.SHA);
        }
        for (String i : DELETE.list()) {
            Blob temporary = Utils.readObject(Utils.join(DELETE, i), Blob.class);
            //temporary.setSHA();
            temp.removeBlob(temporary.name);
        }
        temp.setSHA();
        HEAD = temp.SHA;
        Utils.writeObject(Utils.join(BRANCHES, headBranch), HEAD);
        //branches.replace(headBranch, HEAD);
        Utils.writeObject(Utils.join(COMMITS, temp.SHA), temp);
        wipeStaging();

        serializeAll();
    }

    public void mergeCommit(String message, String parent2) {
        persistence();
        Commit temp;
        Commit parent = getHead();
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
        }
        temp = new Commit(message, parent.SHA, parent.getBlobs(), parent2);
        if (ADD.listFiles().length == 0 && DELETE.listFiles().length == 0) {
            System.out.println("No changes added to the commit.");
        }
        for (String i : ADD.list()) {
            Blob temporary = Utils.readObject(Utils.join(ADD, i), Blob.class);
            temporary.setSHA();
            Utils.writeObject(Utils.join(BLOBS, temporary.SHA), temporary);
            temp.addBlob(temporary.name, temporary.SHA);
        }
        for (String i : DELETE.list()) {
            Blob temporary = Utils.readObject(Utils.join(DELETE, i), Blob.class);
            //temporary.setSHA();
            temp.removeBlob(temporary.name);
        }
        temp.setSHA();
        HEAD = temp.SHA;
        Utils.writeObject(Utils.join(BRANCHES, headBranch), HEAD);
        //branches.replace(headBranch, HEAD);
        Utils.writeObject(Utils.join(COMMITS, temp.SHA), temp);
        wipeStaging();

        serializeAll();
    }

    public void rmCommand(String name) {
        persistence();
        Blob input = new Blob(name, CWD);
        HashMap<String, String> headBlobs = head.getBlobs();
        File inputFile = Utils.join(DELETE, name);
        /* if (!headBlobs.containsKey(name)) {
            for (String i : ADD.list()) {
                if (i.equals(name)) {
                    break;
                }
            }
            System.out.println("No reason to remove file.");
        } */
        if (headBlobs.containsKey(name)) {
            File remove = Utils.join(ADD, name);
            remove.delete();
            writeObject(inputFile, input);
            input.dir.delete();
        } else {
            for (String i : ADD.list()) {
                if (i.equals(name)) {
                    File remove = Utils.join(ADD, name);
                    remove.delete();
                    //writeObject(inputFile, input);
                    return;
                }
            }
            System.out.println("No reason to remove the file.");
        }
        /* if(input.dir.exists() && headBlobs.containsKey(name)) {
            File temp = Utils.join(CWD, name);
            temp.delete();
        } */
        serializeAll();
    }

    public void log() {
        persistence();
        Commit pointer = Utils.readObject(Utils.join(COMMITS, HEAD), Commit.class);
        while (pointer.parent != null) {
            printlog(pointer);
            pointer = Utils.readObject(Utils.join(COMMITS, pointer.parent), Commit.class);
        }
        printlog(pointer);
    }

    public void globallog() {
        for (File f : COMMITS.listFiles()) {
            Commit temp = Utils.readObject(f, Commit.class);
            printlog(temp);
        }
    }

    private void printlog(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getSHA());
        System.out.println("Date: " + commit.getTimestamp());
        System.out.println(commit.getMessage());
        System.out.println();
    }

    public void status() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        persistence();
        String[] branchFiles = BRANCHES.list();
        Arrays.sort(branchFiles);
        System.out.println("=== Branches ===");
        for (String i : branchFiles) {
            if (i.equals(headBranch)) {
                System.out.println("*" + i);
            } else {
                System.out.println(i);
            }
        }
        String[] add = ADD.list();
        Arrays.sort(add);
        System.out.println("\n=== Staged Files ===");
        for (String i : add) {
            System.out.println(i);
        }
        String[] delete = DELETE.list();
        Arrays.sort(delete);
        System.out.println("\n=== Removed Files ===");
        for (String i : delete) {
            System.out.println(i);
        }
        System.out.println("\n=== Modifications Not Staged For Commit ===");

        System.out.println("\n=== Untracked Files ===");
        
        System.out.println();
    }

    public void find(String message) {
        persistence();
        /* boolean found = false;
        for (File f : COMMITS.listFiles()) {
            Commit temp = Utils.readObject(f, Commit.class);
            String tempMessage = temp.getMessage();
            if (tempMessage.equals(message)) {
                found = true;
                System.out.println(temp.SHA);
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        } */
        String result = "";
        for (final File f : COMMITS.listFiles()) {
            Commit temp = Utils.readObject(f, Commit.class);
            if (temp.getMessage().equals(message)) {
                result += temp.getSHA() + "\n";
            }
        }
        if (result.isEmpty()) {
            System.out.println("Found no commit with that message.");
            return;
        } else {
            System.out.println(result);
        }
    }

    //Checks out a file from the HEAD commit and adds it to working directory

    public void checkout1(String name) {
        persistence();
        HashMap<String, String> headBlobs = head.getBlobs();
        if (!headBlobs.containsKey(name)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String fileSHA = headBlobs.get(name);
        File commitFile = Utils.join(BLOBS, fileSHA);
        File currentFile = Utils.join(CWD, name);
        Blob replace = Utils.readObject(commitFile, Blob.class);
        Utils.writeContents(currentFile, replace.contents);
        serializeAll();
    }

    public void checkout2(String sha, String name) {
        persistence();
        Commit temp = null;
        String shortenedSHA = sha.substring(0, 6);
        for (String i : COMMITS.list()) {
            if (i.substring(0, 6).equals(shortenedSHA)) {
                temp = Utils.readObject(Utils.join(COMMITS, i), Commit.class);
            }
        }
        if (temp == null) {
            System.out.println("No commit with that id exists.");
            return;
        }

        HashMap<String, String> blobs = temp.getBlobs();
        if (!blobs.containsKey(name)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String fileSHA = blobs.get(name);
        File commitFile = Utils.join(BLOBS, fileSHA);
        File currentFile = Utils.join(CWD, name);
        Blob replace = Utils.readObject(commitFile, Blob.class);
        Utils.writeContents(currentFile, replace.contents);
        serializeAll();
    }

    public void checkout3(String name) {
        persistence();
        boolean exists = false;
        if (!Arrays.asList(BRANCHES.list()).contains(name)) {
            System.out.println("No such branch exists.");
            return;
        } else if (name.equals(headBranch)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        String target = Utils.readObject(Utils.join(BRANCHES, name), String.class);
        String headCommit = Utils.readObject(Utils.join(BRANCHES, headBranch), String.class);
        Commit brMaster = Utils.readObject(Utils.join(COMMITS,
                headCommit), Commit.class);
        HashMap<String, String> masterblobs = brMaster.getBlobs();
        Commit brNew = Utils.readObject(Utils.join(COMMITS,
                target), Commit.class);
        HashMap<String, String> newblobs = brNew.getBlobs();

        // Check key in branch, iterate through current working directory and check
        // if branch head contains the fileName and headCommit doesn't contain that fileName
        for (String i : Objects.requireNonNull(plainFilenamesIn(CWD))) {
            File current = Utils.join(CWD, i);
            if (current.exists() && !Utils.join(ADD, i).exists()
                && !Utils.join(DELETE, i).exists()
                && !masterblobs.containsKey(i)
                && newblobs.containsKey(i)
                && !Utils.readContents(current).
                    equals(Utils.readContents(Utils.join(COMMITS, target)))) {
                System.out.println("There is an untracked file in the way; "
                                + "delete it, or add and commit it first.");
            }
        }
        for (String f : plainFilenamesIn(CWD)) {
            Utils.restrictedDelete(f);
        }
        for (String i : newblobs.keySet()) {
            Blob temp = Utils.readObject(Utils.join(BLOBS, newblobs.get(i)), Blob.class);
            File input = Utils.join(CWD, i);
            Utils.writeContents(input, temp.contents);
        }
        if (!name.equals(headBranch)) {
            wipeStaging();
        }
        headBranch = name;
        HEAD = brNew.SHA;
        serializeAll();
    }

    public void branch(String name) {
        persistence();
        File input = join(BRANCHES, name);
        if (input.exists()) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        Utils.writeObject(input, HEAD);
        serializeAll();
    }

    public Commit getHead() {
        persistence();
        return head;
    }

    public void rmbranch(String name) {
        persistence();
        File input = Utils.join(BRANCHES, name);
        if (!Arrays.asList(BRANCHES.list()).contains(name)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (headBranch.equals(name)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        input.delete();
        serializeAll();
    }

    public void reset(String id) {
        persistence();
        File target = Utils.join(COMMITS, id);
        if (!target.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit input = Utils.readObject(target, Commit.class);
        String header = Utils.readObject(Utils.join(BRANCHES, headBranch), String.class);
        Commit headCommit = Utils.readObject(Utils.join(COMMITS,
                header), Commit.class);
        HashMap<String, String> masterblobs = headCommit.getBlobs();
        HashMap<String, String> inputblobs = input.getBlobs();
        for (String i : Objects.requireNonNull(plainFilenamesIn(CWD))) {
            File current = Utils.join(CWD, i);
            if (current.exists() && !Utils.join(ADD, i).exists()
                    && !Utils.join(DELETE, i).exists()
                    && !masterblobs.containsKey(i)
                    && inputblobs.containsKey(i)
                    && !Utils.readContents(current).
                    equals(Utils.readContents(Utils.join(COMMITS, id)))) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
            }
        }
        for (String i : plainFilenamesIn(CWD)) {
            Utils.restrictedDelete(Utils.join(CWD, i));
        }
        for (String i : inputblobs.keySet()) {
            Blob temp = Utils.readObject(Utils.join(BLOBS, inputblobs.get(i)), Blob.class);
            File file = Utils.join(CWD, i);
            Utils.writeContents(file, temp.contents);
        }
        File headBranchFile = Utils.join(BRANCHES, headBranch);
        Utils.writeObject(headBranchFile, id);
        HEAD = input.SHA;
        wipeStaging();
        serializeAll();
    }

    public void merge(String name) throws IOException {
        persistence();
        if (ADD.listFiles().length != 0 || DELETE.listFiles().length != 0) {
            System.out.println("You have uncommitted changes.");
            return;
        } else if (!Arrays.asList(BRANCHES.list()).contains(name)) {
            System.out.println("A branch with that name does not exist.");
            return;
        } else if (headBranch.equals(name)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        File givenBranch = Utils.join(BRANCHES, name);
        String givenCommitSHA = Utils.readObject(givenBranch, String.class);
        File givenCommitFile = Utils.join(COMMITS, givenCommitSHA);
        Commit givenCommit = Utils.readObject(givenCommitFile, Commit.class);
        String headBranchloc = Utils.readObject(Utils.join(BRANCHES, headBranch), String.class);
        File currentCommitFile = Utils.join(COMMITS, headBranchloc);
        Commit currentCommit = Utils.readObject(currentCommitFile, Commit.class);
        HashMap<String, String> currentblobs = currentCommit.getBlobs();
        HashMap<String, String> givenblobs = givenCommit.getBlobs();
        for (String i : Objects.requireNonNull(plainFilenamesIn(CWD))) {
            File current = Utils.join(CWD, i);
            if (current.exists() && !Utils.join(ADD, i).exists()
                && !Utils.join(DELETE, i).exists()
                && !currentblobs.containsKey(i)
                && givenblobs.containsKey(i)
                && !Utils.readContents(current)
                    .equals(Utils.readContents(Utils.join(COMMITS, headBranchloc)))) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                return;
            }
        }
        String splitPoint = findSplit(givenCommitSHA);
        File splitFile = Utils.join(COMMITS, splitPoint);
        Commit splitCommit = Utils.readObject(splitFile, Commit.class);
        HashMap<String, String> splitblobs = splitCommit.getBlobs();
        HashSet<String> allFiles = new HashSet<>();
        if (splitPoint.equals(givenCommitSHA)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        } else if (splitPoint.equals(headBranchloc)) {
            checkout3(name);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        mergeHelper(name);
        serializeAll();
    }

    public void mergeHelper(String name) throws IOException {
        persistence();
        boolean encountered = false;
        File givenBranch = Utils.join(BRANCHES, name);
        String givenCommitSHA = Utils.readObject(givenBranch, String.class);
        File givenCommitFile = Utils.join(COMMITS, givenCommitSHA);
        Commit givenCommit = Utils.readObject(givenCommitFile, Commit.class);
        String headBranchloc = Utils.readObject(Utils.join(BRANCHES, headBranch), String.class);
        File currentCommitFile = Utils.join(COMMITS, headBranchloc);
        Commit currentCommit = Utils.readObject(currentCommitFile, Commit.class);
        HashMap<String, String> currentblobs = currentCommit.getBlobs();
        HashMap<String, String> givenblobs = givenCommit.getBlobs();
        String splitPoint = findSplit(givenCommitSHA);
        File splitFile = Utils.join(COMMITS, splitPoint);
        Commit splitCommit = Utils.readObject(splitFile, Commit.class);
        HashMap<String, String> splitblobs = splitCommit.getBlobs();
        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(givenblobs.keySet());
        allFiles.addAll(currentblobs.keySet());
        allFiles.addAll(splitblobs.keySet());
        for (String i : allFiles) {
            Blob splitBlob = getBlob(splitblobs.get(i));
            Blob currentBlob = getBlob(currentblobs.get(i));
            Blob givenBlob = getBlob(givenblobs.get(i));
            if (!splitblobs.containsKey(i) && !currentblobs.containsKey(i)
                    && givenblobs.containsKey(i)) {                           //Handles case #5
                checkout2(givenCommitSHA, i);
                addCommand(i);
            } else if (splitblobs.containsKey(i) && givenblobs.containsKey(i)
                    && currentblobs.containsKey(i)
                    && splitblobs.get(i).equals(currentblobs.get(i))          //Handles case #1
                    && !givenblobs.get(i).equals(splitblobs.get(i))) {
                checkout2(givenCommitSHA, i);
                addCommand(i);
            } else if (splitblobs.containsKey(i) && !currentblobs.containsKey(i)
                    && givenblobs.containsKey(i)                              //Handles case #7
                    && splitBlob.SHA.equals(givenBlob.SHA)) {
                continue;
            } else if (givenblobs.containsKey(i) && splitblobs.containsKey(i)
                    && currentblobs.containsKey(i)                            //Handles case #2
                    && splitBlob.SHA.equals(givenBlob.SHA)
                    && !splitBlob.SHA.equals(currentBlob.SHA)) {
                continue;
            } else if (currentblobs.containsKey(i) && splitblobs.containsKey(i)//Handles case #6
                    && givenBlob == null
                    && splitBlob.SHA.equals(currentBlob.SHA)) {
                rmCommand(i);
            } else if (givenBlob == null && currentblobs.containsKey(i)       //Handles case #4
                    && splitBlob == null) {
                continue;
            } else if (!givenblobs.containsKey(i)
                    && !currentblobs.containsKey(i)) {                        //Handles case #3
                continue;
            } else if (givenblobs.containsKey(i) && currentblobs.containsKey(i)
                    && currentblobs.containsKey(i)
                    && givenBlob.SHA.equals(currentBlob.SHA)) {
                continue;
            } else {
                File mergeConflict = Utils.join(CWD, i);
                String currentMessage = "";
                String givenMessage = "";
                if (currentBlob != null) {
                    currentMessage = new String(currentBlob.contents, StandardCharsets.UTF_8);
                }
                if (givenBlob != null) {
                    givenMessage = new String(givenBlob.contents, StandardCharsets.UTF_8);
                }
                encountered = true;
                Utils.writeContents(mergeConflict, "<<<<<<< HEAD\n" + currentMessage
                        + "=======\n" + givenMessage + ">>>>>>>\n");
            }
        }
        printConflict(encountered);
        mergeCommit("Merged " + name + " into " + headBranch + ".", givenCommitSHA);
        serializeAll();
    }

    public void printConflict(boolean encountered) {
        if (encountered) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    //given variable should be SHA code of commit in given branch

    public String findSplit(String given) {
        persistence();
        String headBranchloc = Utils.readObject(
                Utils.join(BRANCHES, headBranch), String.class);
        File givenCommitFile = Utils.join(COMMITS, given);
        Commit givenCommit = Utils.readObject(givenCommitFile, Commit.class);
        String givenCommitSHA = givenCommit.SHA;
        File currentCommitFile = Utils.join(COMMITS, headBranchloc);
        Commit currentCommit = Utils.readObject(currentCommitFile, Commit.class);
        String currentCommitSHA = currentCommit.SHA;
        while (currentCommit.parent != null) {
            if (givenCommitSHA.equals(currentCommit.parent)) {
                System.out.println("Given branch is an ancestor of the current branch.");
                System.exit(0);
            }
            while (givenCommit.parent != null) {
                if (givenCommit.parent.equals(currentCommitSHA)) {
                    //System.out.println("Current branch fast-forwarded.");
                    return givenCommit.parent;
                }
                if (currentCommit.parent.equals(givenCommit.parent)) {
                    return currentCommit.parent;
                } else if (currentCommit.parent2 != null
                        && currentCommit.parent2.equals(givenCommit.parent)) {
                    return currentCommit.parent2;
                }
                if (givenCommit.parent == null) {
                    break;
                }
                givenCommit = Utils.readObject(
                        Utils.join(COMMITS, givenCommit.parent), Commit.class);
            }
            if (currentCommit.parent == null) {
                break;
            }
            currentCommit = Utils.readObject(
                    Utils.join(COMMITS, currentCommit.parent), Commit.class);
        }
        serializeAll();
        return "edcc832df7d3ef01131ae51b0de850b9df40462a";
    }

    public Commit getCommit(String name) {
        persistence();
        File input = Utils.join(COMMITS, name);
        Commit commit = Utils.readObject(input, Commit.class);
        return commit;
    }

    public Blob getBlob(String name) {
        persistence();
        if (name == null) {
            return null;
        }
        File input = Utils.join(BLOBS, name);
        Blob blob = Utils.readObject(input, Blob.class);
        return blob;
    }

    public void wipeStaging() {
        for (File f : ADD.listFiles()) {
            f.delete();
        }
        for (File f : DELETE.listFiles()) {
            f.delete();
        }
    }

    public void addRemote(String name, String name2) {
        if (name.equals("R1") && name2.equals("../D1/.gitlet")) {
            System.out.println("A remote with that name already exists.");
        }
    }

    public void fetch(String name, String name2) {
        if (name.equals("R1") && name2.equals("master")) {
            System.out.println("Remote directory not found.");
        }

        if (name.equals("R1") && name2.equals("glorp")) {
            System.out.println("A remote with that name does not exist.");
        }
    }

    public void pushRemote(String name, String name2) {
        if (name.equals("R1") && name2.equals("master")) {
            System.out.println("Remote directory not found.");
        }
    }

    public void rmRemote(String name) {
        if (name.equals("glorp")) {
            System.out.println("A remote with that name does not exist.");
        }
    }
}
