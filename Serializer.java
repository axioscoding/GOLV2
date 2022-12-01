import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer {
    


    public static final Pattern loadFromFile(String name){
        Pattern p = null;
        
        try{
            FileInputStream fi = new FileInputStream(name);
            ObjectInputStream oi = new ObjectInputStream(fi);

            p = (Pattern) oi.readObject();

            oi.close();
            fi.close();
        }catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        } 

        return p;
    }


    public static final void saveToFile(Pattern p){
        
        try {

            File file = new File("patterns/" + p.getName() + ".gol");
            if(!(file.exists() && !file.isDirectory())) { 
                FileOutputStream f = new FileOutputStream(file);
                ObjectOutputStream o = new ObjectOutputStream(f);
                o.writeObject(p);
                o.close();
                f.close();
            }

            
            
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } 
        
    }


}
