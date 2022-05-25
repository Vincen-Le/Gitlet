package gitlet;

import gitlet.Utils.*;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {

    String SHA;
    String ShortSHA;
    String name;
    File dir;
    byte[] contents;

    public Blob(String fileName, File location) {
        this.name = fileName;
        this.dir = Utils.join(location, fileName);
        if (!dir.exists()) {
            contents = null;
        } else {
            contents = Utils.readContents(dir);
        }
    }

    public Blob(Blob input) {
        this.name = input.name;
        this.dir = input.dir;
    }

    public void setSHA() {
        SHA = Utils.sha1(Utils.readContents(dir));
        ShortSHA = SHA.substring(0, 6);
    }

}
