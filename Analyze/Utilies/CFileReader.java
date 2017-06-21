package src.Analyze.Utilies;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by AliReza on 18/06/2017.
 */
public class CFileReader {
    public String fileContent = "";
    public CFileReader(String fileAdr){
        BufferedReader br = null;
        FileReader fr = null;

        try {

            fr = new FileReader(fileAdr);
            br = new BufferedReader(fr);

            String sCurrentLine;

            br = new BufferedReader(new FileReader(fileAdr));

            while ((sCurrentLine = br.readLine()) != null) {
                fileContent += sCurrentLine + '\n';
//                System.out.println(sCurrentLine);
            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
    }


}
