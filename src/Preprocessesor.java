/**
 * Created by Armin on 5/30/2017.
 */
public class Preprocessesor {

    public static String removeCRLF(String s){
        String ret = s.replaceAll("\\r|\\n"," ");
        return ret;
    }

    public static String removeComments(String s){
        String ret = s.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)","");
        return ret;
    }

    public static String removeBlankLines(String s){
        String ret = s.replaceAll("(?m)^[ \t]*\r?\n", "");
        return ret;
    }

}
