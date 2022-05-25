package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        String firstArg = args[0];
        Repository gitlet = new Repository();
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                checkArgs(args, 1);
                gitlet.initCommand();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                checkArgs(args, 2);
                gitlet.addCommand(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "status":
                checkArgs(args, 1);
                gitlet.status();
                break;
            case "commit":
                if (args.length < 2 || args[1].isEmpty()) {
                    System.out.println("Please enter a commit message.");
                    return;
                }
                checkArgs(args, 2);
                gitlet.commitCommand(args[1]);
                break;
            case "rm":
                checkArgs(args, 2);
                gitlet.rmCommand(args[1]);
                break;
            case "log":
                checkArgs(args, 1);
                gitlet.log();
                break;
            case "global-log":
                checkArgs(args, 1);
                gitlet.globallog();
                break;
            case "find":
                checkArgs(args, 2);
                gitlet.find(args[1]);
                break;
            case "checkout":
                if (args.length == 3 && args[1].equals("--")) {
                    gitlet.checkout1(args[2]);
                    break;
                } else if (args.length == 4 && args[2].equals("--")) {
                    gitlet.checkout2(args[1], args[3]);
                    break;
                } else if (args.length == 2) {
                    gitlet.checkout3(args[1]);
                    break;
                } else {
                    System.out.println("Incorrect operands.");
                    break;
                }
            case "branch":
                checkArgs(args, 2);
                gitlet.branch(args[1]);
                break;
            case "rm-branch":
                checkArgs(args, 2);
                gitlet.rmbranch(args[1]);
                break;
            case "reset":
                checkArgs(args, 2);
                gitlet.reset(args[1]);
                break;
            case "merge":
                checkArgs(args, 2);
                gitlet.merge(args[1]);
                break;
            /* case "add-remote":
                checkArgs(args, 3);
                gitlet.addRemote(args[1], args[2]);
            case "fetch":
                checkArgs(args, 3);
                gitlet.fetch(args[1], args[2]);
            case "push":
                checkArgs(args, 3);
                gitlet.pushRemote(args[1], args[2]);
            case "rm-remote":
                checkArgs(args, 2);
                gitlet.rmRemote(args[1]); */
            default:
                System.out.println("No command with that name exists.");
        }
    }
    public static void checkArgs(String[] args, int num) {
        if (args.length != num) {
            throw new GitletException("Incorrect operands");
        }
    }
}
