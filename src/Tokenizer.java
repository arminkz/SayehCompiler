import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Tokenizer {

    public static ArrayList<String> tokenizeOld(String text){
        ArrayList<String> tokens = new ArrayList<>();
        String pattern = "([A-Za-z0-9]+)|&|\\(|\\)|!|\\|";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        while (m.find()){
            tokens.add(m.group());
        }
        return tokens;
    }

    public static ArrayList<Token> tokenize(String s){
        ArrayList<Token> tokens = new ArrayList<>();
        String[] segments = s.split("[ \t\n]");
        int i=0;
        for(String seg : segments){
            //ignore trash tokens
            //System.out.println("S) " + seg);
            if("".equals(seg)) continue;
            tokens.add(new Token(seg,i));
            i++;
        }
        return tokens;
    }

}