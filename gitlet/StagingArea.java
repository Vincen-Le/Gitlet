package gitlet;

import java.io.Serializable;
import java.util.HashMap;

public class StagingArea implements Serializable {

    private HashMap<String, Blob> addStaging;
    private HashMap<String, Blob> deleteStaging;

    public StagingArea() {
        addStaging = new HashMap<>();
        deleteStaging = new HashMap<>();
    }

    public void reset() {
        addStaging = new HashMap<>();
        deleteStaging = new HashMap<>();
    }

    public void addFile(Blob input) {
        addStaging.put(input.SHA, input);
    }

    public void addStaging() {


    }

}
