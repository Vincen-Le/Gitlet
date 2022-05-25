package gitlet;


import java.io.Serializable;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;

public class SerializableHelper implements Serializable {
    /**
     * Still have to convert to proper type, used to read a certain file and convert
     */
    static Object readObject(File f) {
        byte[] arr = Utils.readContents(f);
        ByteArrayInputStream i = new ByteArrayInputStream(arr);
        try {
            ObjectInputStream s = new ObjectInputStream(i);
            Object o = s.readObject();
            s.close();
            i.close();
            return o;
        } catch (IOException | ClassNotFoundException c) {
            System.out.println("IOException");
            return null;
        }
    }


    /**
     * Object must be serializable, filename should be sha
     *
     * @param o serialized
     */
    static void writeObject(Object o, File f) {
        Utils.writeContents(f, helper(o));
//        Utils.writeContents(helperOut(o, f.getName()), helper(o));
    }

    private static byte[] helper(Object obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(obj);
            objectStream.close();
//            return stream.toByteArray();
            ByteArrayOutputStream arr = stream;
            stream.close();
            return arr.toByteArray();
        } catch (IOException excp) {
            System.out.println("BAD ERROR");
            return null;
        }

    }

    private static File helperOut(Object o, String name) {
        File outFile = new File(name);
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(o);
            out.close();
            return outFile;
        } catch (IOException excp) {
            return null;
        }
    }
}
