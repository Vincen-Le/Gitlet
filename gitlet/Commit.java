package gitlet;
import java.io.Serializable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

//import static gitlet.Utils.serialize;

/** Represents a gitlet commit object.
 *  It will contain the message, timestamp, and blobs associated with the commit.
 *
 *
 *  @author Vincent Le
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;

    /** Timestamp for the Commit. */
    private Date timestamp;
    private String date;
    private SimpleDateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z");

    /** SHA-1 Codes */
    String SHA;
    String ShortSHA;

    String parent;
    String parent2;

    //private HashSet<File> currentCommit = new HashSet<>();

    /** HashMap of fileName keys and SHA values */
    private HashMap<String, String> blobs;

    public Commit(String message, String parent, HashMap<String, String> blobs,
                  String parent2) {
        this.message = message;
        this.parent = parent;
        this.blobs = blobs;
        this.parent2 = parent2;
        if (message.equals("initial commit")) {
            this.timestamp = new Date(0);
            date = formatter.format(timestamp);
        } else {
            this.timestamp = new Date(System.currentTimeMillis());
            date = formatter.format(timestamp);
        }
    }

    /* public void getSHA() {
        if (this.parent == null) {
            SHA = Utils.sha1(message + timestamp);
        } else {
            SHA = Utils.sha1(message + timestamp);
        }
    } */

    public void setSHA() {
        String s = "";
        for (String i : this.blobs.values()) {
            s += i;
        }
        SHA = Utils.sha1(message + timestamp + s);
        ShortSHA = SHA.substring(0, 6);
    }

    public HashMap getBlobs() {
        return blobs;
    }

    public String getParent2() {
        return parent2;
    }

    public void addBlob(String name, String blob) {
        this.blobs.put(name, blob);
    }

    public void removeBlob(String name) {
        this.blobs.remove(name);
    }

    public String getParentSHA() {
        return parent;
    }

    public String getSHA() {
        return SHA;
    }

    public String getTimestamp() {
        return date;
    }

    public String getMessage() {
        return message;
    }

}
