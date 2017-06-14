import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by chait on 07/06/2017.
 */
public class FileUtil {

    public String readFromFile(String path){
        String everything=null;
        try {
        BufferedReader br = new BufferedReader(new FileReader(path));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            everything = sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return everything;
    }

    public void writeInTOFile(String Path){

    }
}
