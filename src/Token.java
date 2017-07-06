import java.util.ArrayList;

/**
 * Created by Armin on 5/30/2017.
 */
public class Token {

    private TokenType type;
    public TokenType getType(){
        return type;
    }

    private String value;
    public String getValue(){
        return value;
    }

    private int NumberOfToken;

    public Token(String s){
        value = s;
        assignType();
    }

    public Token(String s,int num){
        value = s;
        NumberOfToken = num;
        assignType();
    }

    public void assignType(){
        if(value.matches("if|else|while|int|char|bool|null|true|false")){
            type = TokenType.keyword;
            //System.out.println("(keyword)");
        }else if(value.matches("=|\\+=|\\-=|&&|\\|\\||!|==|!=|>|<|>=|<=|\\+|-|\\*|/|\\+\\+|--")){
            type = TokenType.operator;
            //System.out.println("(operator)");
        }else if(value.matches("-?\\d+")){  //use "-?\\d+(\\.\\d+)?" for float numbers (we dont have floats here)
            type = TokenType.number;
            //System.out.println("(number)");
        }else if(value.matches(";|\\(|\\)|\\{|\\}")){
            type = TokenType.punctuation;
            //System.out.println("(punctuation)");
        }else {
            type = TokenType.identifier;
            //System.out.println("(identifier)");
        }
    }

    public String toString(){
        return getValue() + " (" + getType().toString() + ")";
    }

    public static Token fromChunk(Chunk c){
        return new Token(c.Name);
    }

    public static void PrintTokenSequence(ArrayList<Token> toks){
        for(Token t  : toks){
            System.out.print(t.getValue() + " ");
        }
        System.out.println("");
    }

}
