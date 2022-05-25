package gitlet;

import java.io.Serializable;

public class Branches implements Serializable {
    //For serialization
    static final long serialVersionUID = 0;
    //    ArrayList<Commit> arr;
    String shaSplit;
    String name;
    String shaLast; //sha of commit

    //outdated
    public Branches(String n, String spl) {
        name = n;
        shaSplit = spl;
        shaLast = spl;
    }

}
