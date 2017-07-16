import javax.lang.model.type.ArrayType;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Armin on 5/30/2017.
 */
public class Main {

    public static void main(String[] args) {
        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle("Select C Source Code");
        int ret = jfc.showOpenDialog(null);
        if(ret == 0){
            String source = null;

            //Read Source Code
            try {
                BufferedReader br = new BufferedReader(new FileReader(jfc.getSelectedFile()));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                source = sb.toString();
                br.close();
            }catch(IOException e){
                System.out.println("Error Reading Source File !");
                System.exit(-1);
            }


            //System.out.println("--------- Original Source Code ---------------");
            //System.out.println(source);

            //System.out.println("--------- Source Code Pre---------------");
            source = Preprocessesor.removeComments(source);
            source = Preprocessesor.removeBlankLines(source);
            source = Preprocessesor.removeCRLF(source);

            ArrayList<Token> allTokens = Tokenizer.tokenize(source);
            System.out.println("ALL TOKENS :");
            for(Token t  : allTokens){
                System.out.print(t.getValue() + " ");
            }
            System.out.println("\n");

            Parser p = new Parser();

            p.parseCode(allTokens);
            System.out.println("Sayeh Assembly :\n");
            System.out.println(p.getFinalASM());
        }
    }

}
