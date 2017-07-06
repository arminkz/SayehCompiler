import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by Armin on 5/31/2017.
 */
public class Parser {

    private AsmGenerator asm = new AsmGenerator();

    public String getASM(){
        return asm.getASM();
    }

    public Parser(){

    }

    public void parseCode(ArrayList<Token> tokens){
        if (tokens.size() == 0) return;

        Iterator<Token> I = tokens.iterator();
        while(I.hasNext()){
            Token frist = I.next();

            if(frist.getValue().equals("if")){
                if(I.next().getValue().equals("(")){
                    int pc = 1;
                    //PARSE CONDITION
                    ArrayList<Token> condition = new ArrayList<>();
                    Chunk cond_ans = null;
                    while(I.hasNext()) {
                        Token ti = I.next();
                        if(ti.getValue().equals("(")){
                            pc++;
                        }
                        if(ti.getValue().equals(")")){
                            if (pc == 1){
                                ArrayList<Token> simt = decideSIM(condition);
                                cond_ans = parseBoolExp(simt);
                                break;
                            }
                            pc--;
                        }
                        condition.add(ti);
                    }

                    String if_lbl = asm.getLabel();
                    asm.generateNOT(cond_ans,cond_ans);
                    asm.generateSHR(cond_ans,cond_ans);
                    asm.generateBRC(if_lbl);

                    //PARSE INNER
                    Parser innerParser = new Parser();

                    if(I.next().getValue().equals("{")){
                        int bc = 1;
                        ArrayList<Token> statements = new ArrayList<>();
                        int j;
                        while(I.hasNext()) {
                            Token ti = I.next();
                            if(ti.getValue().equals("{")){
                                pc++;
                            }
                            if(ti.getValue().equals("}")){
                                if (pc == 1){
                                    innerParser.parseCode(statements);
                                    break;
                                }
                                pc--;
                            }
                            statements.add(ti);
                        }
                    }else{
                        System.out.println("Invalid IF (Code :2)");
                    }

                    asm.put(innerParser.getASM());
                    asm.putLabel(if_lbl);

                }else{
                    System.out.println("Invalid IF (Code :1)");
                }
            }else if(frist.getValue().equals("while")){

                if(I.next().getValue().equals("(")){
                    int pc = 1;
                    //PARSE CONDITION
                    ArrayList<Token> condition = new ArrayList<>();
                    Chunk cond_ans = null;
                    while(I.hasNext()) {
                        Token ti = I.next();
                        if(ti.getValue().equals("(")){
                            pc++;
                        }
                        if(ti.getValue().equals(")")){
                            if (pc == 1){
                                ArrayList<Token> simt = decideSIM(condition);
                                cond_ans = parseBoolExp(simt);
                                break;
                            }
                            pc--;
                        }
                        condition.add(ti);
                    }

                    String while_lbl = asm.getLabel();
                    asm.generateNOT(cond_ans,cond_ans);
                    asm.generateSHR(cond_ans,cond_ans);
                    asm.generateBRC(if_lbl);

                    //PARSE INNER
                    Parser innerParser = new Parser();

                    if(I.next().getValue().equals("{")){
                        int bc = 1;
                        ArrayList<Token> statements = new ArrayList<>();
                        int j;
                        while(I.hasNext()) {
                            Token ti = I.next();
                            if(ti.getValue().equals("{")){
                                pc++;
                            }
                            if(ti.getValue().equals("}")){
                                if (pc == 1){
                                    innerParser.parseCode(statements);
                                    break;
                                }
                                pc--;
                            }
                            statements.add(ti);
                        }
                    }else{
                        System.out.println("Invalid IF (Code :2)");
                    }

                    asm.put(innerParser.getASM());
                    asm.putLabel(if_lbl);

                }else{
                    System.out.println("Invalid IF (Code :1)");
                }
            }
        }



    }

    public Chunk parseMathExp(ArrayList<Token> tokens){

        //Convert to PostFix
        Stack<Token> OpStack = new Stack<>();
        Stack<Token> postfix = new Stack<>();

        for(Token t : tokens){

            if(t.getType() == TokenType.number || t.getType() == TokenType.identifier){
                //Number
                postfix.push(t);

            }else if(t.getType() == TokenType.operator){
                //Operator
                while(!OpStack.isEmpty() && (getOpPiority(OpStack.peek()) > getOpPiority(t))){
                    postfix.push(OpStack.pop());
                }
                OpStack.push(t);
            }else if((t.getType() == TokenType.punctuation && t.getValue().equals("(")) || (t.getType() == TokenType.punctuation && t.getValue().equals(")")) ){
                //Left Paranthesis
                if(t.getValue().equals("(")){
                    OpStack.push(t);
                }
                //Right Paranthesis
                if(t.getValue().equals(")")){
                    while (!"(".equals(OpStack.peek().getValue())) {
                        postfix.push(OpStack.pop());
                    }
                    OpStack.pop();
                }
            }else{
                System.err.println("Invalid Token in Math Expression");
            }
        }



        //Append OpStack to Postfix
        while (!OpStack.isEmpty()){
            postfix.push(OpStack.pop());
        }

        //Reverse Stack for Evaluating
        Stack<Token> RPF = new Stack<>();
        while(!postfix.isEmpty()){
            RPF.push(postfix.pop());
        }

        //Evaluate
        Stack<Chunk> EvalStack = new Stack<>();
        while (!RPF.isEmpty()){
            Token token = RPF.pop();
            if(token.getType() == TokenType.operator) {
                Chunk tmp;
                Chunk tmp2;
                Chunk c1;
                Chunk c2;
                switch(token.getValue()){
                    case "+":
                        c2 = EvalStack.pop();
                        c1 = EvalStack.pop();
                        if(c1.getType()==ChunkType.reg_integer && c2.getType()==ChunkType.reg_integer){
                            tmp = asm.getTempIntegerChunk();
                            asm.generateADD(tmp,c1);
                            asm.generateADD(tmp,c2);
                            EvalStack.push(tmp);
                        }else if(c1.getType()==ChunkType.reg_integer && c2.getType()==ChunkType.immediate){
                            tmp = asm.getTempIntegerChunk();
                            asm.generateMIL(tmp, Integer.parseInt(c2.Name));
                            asm.generateADD(tmp,c1);
                            EvalStack.push(tmp);
                        }else if(c1.getType()==ChunkType.immediate && c2.getType()==ChunkType.reg_integer){
                            tmp = asm.getTempIntegerChunk();
                            asm.generateMIL(tmp,Integer.parseInt(c1.Name));
                            asm.generateADD(tmp,c2);
                            EvalStack.push(tmp);
                        }else if(c1.getType()==ChunkType.immediate && c2.getType()==ChunkType.immediate){
                            //Both imd
                            int c1v = Integer.parseInt(c1.Name);
                            int c2v = Integer.parseInt(c2.Name);
                            EvalStack.push(new Chunk(String.valueOf(c1v + c2v),ChunkType.immediate));
                        }else {
                            System.err.println("Invalid Chunks In Math Eval Stack !");
                        }
                        break;
                    case "-":
                        c2 = EvalStack.pop();
                        c1 = EvalStack.pop();
                        if(c1.getType()==ChunkType.reg_integer && c2.getType()==ChunkType.reg_integer){
                            tmp = asm.getTempIntegerChunk();
                            asm.generateADD(tmp,c1);
                            asm.generateSUB(tmp,c2);
                            EvalStack.push(tmp);
                        }else if(c1.getType()==ChunkType.reg_integer && c2.getType()==ChunkType.immediate){
                            tmp = asm.getTempIntegerChunk();
                            asm.generateMIL(tmp,-Integer.parseInt(c2.Name));
                            asm.generateADD(tmp,c1);
                            EvalStack.push(tmp);
                        }else if(c1.getType()==ChunkType.immediate && c2.getType()==ChunkType.reg_integer){
                            tmp = asm.getTempIntegerChunk();
                            asm.generateMIL(tmp,Integer.parseInt(c1.Name));
                            asm.generateSUB(tmp,c2);
                            EvalStack.push(tmp);
                        }else if(c1.getType()==ChunkType.immediate && c2.getType()==ChunkType.immediate){
                            //Both imd
                            int c1v = Integer.parseInt(c1.Name);
                            int c2v = Integer.parseInt(c2.Name);
                            EvalStack.push(new Chunk(String.valueOf(c1v - c2v),ChunkType.immediate));
                        }else {
                            System.err.println("Invalid Chunks In Math Eval Stack !");
                        }
                        break;
                    case "*":
                        c2 = EvalStack.pop();
                        c1 = EvalStack.pop();
                        if(c1.getType()==ChunkType.reg_integer && c2.getType()==ChunkType.reg_integer){
                            tmp = asm.getTempIntegerChunk();
                            asm.generateADD(tmp,c1);
                            asm.generateMUL(tmp,c2);
                            EvalStack.push(tmp);
                        }else if(c1.getType()==ChunkType.reg_integer && c2.getType()==ChunkType.immediate){
                            tmp = asm.getTempIntegerChunk();
                            tmp2 = asm.getTempIntegerChunk();
                            asm.generateMIL(tmp2, Integer.parseInt(c2.Name));
                            asm.generateADD(tmp,c1);
                            asm.generateMUL(tmp,tmp2);
                            EvalStack.push(tmp);
                        }else if(c1.getType()==ChunkType.immediate && c2.getType()==ChunkType.reg_integer){
                            tmp = asm.getTempIntegerChunk();
                            asm.generateMIL(tmp,Integer.parseInt(c1.Name));
                            asm.generateMUL(tmp,c2);
                            EvalStack.push(tmp);
                        }else if(c1.getType()==ChunkType.immediate && c2.getType()==ChunkType.immediate){
                            //Both imd
                            int c1v = Integer.parseInt(c1.Name);
                            int c2v = Integer.parseInt(c2.Name);
                            EvalStack.push(new Chunk(String.valueOf(c1v * c2v),ChunkType.immediate));
                        }else {
                            System.err.println("Invalid Chunks In Math Eval Stack !");
                        }
                        break;
                    case "/":
                        c2 = EvalStack.pop();
                        c1 = EvalStack.pop();
                        if(c1.getType()==ChunkType.reg_integer && c2.getType()==ChunkType.reg_integer){
                            tmp = asm.getTempIntegerChunk();
                            asm.generateADD(tmp,c1);
                            asm.generateDIV(tmp,c2);
                            EvalStack.push(tmp);
                        }else if(c1.getType()==ChunkType.reg_integer && c2.getType()==ChunkType.immediate){
                            tmp = asm.getTempIntegerChunk();
                            tmp2 = asm.getTempIntegerChunk();
                            asm.generateMIL(tmp2, Integer.parseInt(c2.Name));
                            asm.generateADD(tmp,c1);
                            asm.generateDIV(tmp,tmp2);
                            EvalStack.push(tmp);
                        }else if(c1.getType()==ChunkType.immediate && c2.getType()==ChunkType.reg_integer){
                            tmp = asm.getTempIntegerChunk();
                            asm.generateMIL(tmp,Integer.parseInt(c1.Name));
                            asm.generateDIV(tmp,c2);
                            EvalStack.push(tmp);
                        }else if(c1.getType()==ChunkType.immediate && c2.getType()==ChunkType.immediate){
                            //Both imd
                            int c1v = Integer.parseInt(c1.Name);
                            int c2v = Integer.parseInt(c2.Name);
                            EvalStack.push(new Chunk(String.valueOf((int)(c1v / c2v)),ChunkType.immediate));
                        }else {
                            System.err.println("Invalid Chunks In Math Eval Stack !");
                        }
                        break;
                    case "++":
                        c1 = EvalStack.pop();
                        asm.generateINC(c1);
                        EvalStack.push(c1);
                        break;
                    case "--":
                        c1 = EvalStack.pop();
                        asm.generateDEC(c1);
                        EvalStack.push(c1);
                        break;
                }
            }else if(token.getType() == TokenType.identifier) {
                EvalStack.push(new Chunk(token.getValue(),ChunkType.reg_integer));
            }else if(token.getType() == TokenType.number) {
                EvalStack.push(new Chunk(token.getValue(),ChunkType.immediate));
            }
        }

        if(EvalStack.size() == 1){
            //System.out.println(asm.getASM());
            Chunk ans = EvalStack.pop();
            //System.out.println("ANSWER IS IN : " + ans.Name);
            return ans;
        }else{
            System.err.println("Bad Syntax : More than one Chunk in stack !");
            return null;
        }
    }

    public Chunk parseBoolExp(ArrayList<Token> tokens){

        //Convert to PostFix
        Stack<Token> OpStack = new Stack<>();
        Stack<Token> postfix = new Stack<>();

        for(Token t : tokens){

            if(t.getValue().equals("true") || t.getValue().equals("false") || t.getType() == TokenType.identifier){
                //Number
                postfix.push(t);

            }else if(t.getType() == TokenType.operator){
                //Operator
                while(!OpStack.isEmpty() && (getBoolOpPiority(OpStack.peek()) > getBoolOpPiority(t))){
                    postfix.push(OpStack.pop());
                }
                OpStack.push(t);
            }else if((t.getType() == TokenType.punctuation && t.getValue().equals("(")) || (t.getType() == TokenType.punctuation && t.getValue().equals(")")) ){
                //Left Paranthesis
                if(t.getValue().equals("(")){
                    OpStack.push(t);
                }
                //Right Paranthesis
                if(t.getValue().equals(")")){
                    while (!"(".equals(OpStack.peek().getValue())) {
                        postfix.push(OpStack.pop());
                    }
                    OpStack.pop();
                }
            }else{
                System.err.println("Invalid Token in Math Expression");
            }
        }



        //Append OpStack to Postfix
        while (!OpStack.isEmpty()){
            postfix.push(OpStack.pop());
        }

        //Reverse Stack for Evaluating
        Stack<Token> RPF = new Stack<>();
        while(!postfix.isEmpty()){
            RPF.push(postfix.pop());
        }

        //Evaluate
        Stack<Chunk> EvalStack = new Stack<>();
        while (!RPF.isEmpty()){
            Token token = RPF.pop();
            if(token.getType() == TokenType.operator) {
                Chunk tmp;
                //Chunk tmp2;
                Chunk c1;
                Chunk c2;
                switch(token.getValue()){
                    case "&&":
                        c2 = EvalStack.pop();
                        c1 = EvalStack.pop();
                        if(c1.getType()==ChunkType.reg_bool && c2.getType()==ChunkType.reg_bool){
                            tmp = asm.getTempBoolChunk();
                            asm.generateAND(tmp,c1);
                            asm.generateAND(tmp,c2);
                            EvalStack.push(tmp);
                        }else if(c1.getType()==ChunkType.reg_bool && c2.getType()==ChunkType.immediate){
                            tmp = asm.getTempBoolChunk();
                            asm.generateMIL(tmp, Boolean.parseBoolean(c2.Name));
                            asm.generateAND(tmp,c1);
                            EvalStack.push(tmp);
                        }else if(c1.getType()==ChunkType.immediate && c2.getType()==ChunkType.reg_bool){
                            tmp = asm.getTempBoolChunk();
                            asm.generateMIL(tmp,Boolean.parseBoolean(c1.Name));
                            asm.generateAND(tmp,c2);
                            EvalStack.push(tmp);
                        }else if(c1.getType()==ChunkType.immediate && c2.getType()==ChunkType.immediate){
                            //Both imd
                            boolean c1v = Boolean.parseBoolean(c1.Name);
                            boolean c2v = Boolean.parseBoolean(c2.Name);
                            EvalStack.push(new Chunk(String.valueOf(c1v && c2v),ChunkType.immediate));
                        }else {
                            System.err.println("Invalid Chunks In Bool Eval Stack !");
                        }
                        break;
                    case "||":
                        c2 = EvalStack.pop();
                        c1 = EvalStack.pop();
                        if(c1.getType()==ChunkType.reg_bool && c2.getType()==ChunkType.reg_bool){
                            tmp = asm.getTempIntegerChunk();
                            asm.generateAND(tmp,c1);
                            asm.generateOR(tmp,c2);
                            EvalStack.push(tmp);
                        }else if(c1.getType()==ChunkType.reg_bool && c2.getType()==ChunkType.immediate){
                            tmp = asm.getTempIntegerChunk();
                            asm.generateMIL(tmp,Boolean.parseBoolean(c2.Name));
                            asm.generateOR(tmp,c1);
                            EvalStack.push(tmp);
                        }else if(c1.getType()==ChunkType.immediate && c2.getType()==ChunkType.reg_bool){
                            tmp = asm.getTempIntegerChunk();
                            asm.generateMIL(tmp,Boolean.parseBoolean(c1.Name));
                            asm.generateOR(tmp,c2);
                            EvalStack.push(tmp);
                        }else if(c1.getType()==ChunkType.immediate && c2.getType()==ChunkType.immediate){
                            //Both imd
                            boolean c1v = Boolean.parseBoolean(c1.Name);
                            boolean c2v = Boolean.parseBoolean(c2.Name);
                            EvalStack.push(new Chunk(String.valueOf(c1v || c2v),ChunkType.immediate));
                        }else {
                            System.err.println("Invalid Chunks In Bool Eval Stack !");
                        }
                        break;
                    case "!":
                        c1 = EvalStack.pop();
                        asm.generateNOT(c1,c1);
                        EvalStack.push(c1);
                        break;
                }
            }else if(token.getType() == TokenType.identifier) {
                EvalStack.push(new Chunk(token.getValue(),ChunkType.reg_bool));
            }else if(token.getValue().equals("true") || token.getValue().equals("false")) {
                EvalStack.push(new Chunk(token.getValue(),ChunkType.immediate));
            }
        }

        if(EvalStack.size() == 1){
            //System.out.println(asm.getASM());
            Chunk ans = EvalStack.pop();
            //System.out.println("ANSWER IS IN : " + ans.Name);
            return ans;
        }else{
            System.err.println("Bad Syntax : More than one Chunk in stack !");
            return null;
        }
    }

    public Chunk parseComparison(ArrayList<Token> tokens){

        //Handle Single Boolean
        if(tokens.size()==1){
            return new Chunk(tokens.get(0).getValue(),ChunkType.reg_bool);
        }

        //Handle Math Comparison
        ArrayList<Token> left = new ArrayList<>();
        ArrayList<Token> right = new ArrayList<>();
        Token mid = null;

        Iterator<Token> iter = tokens.iterator();
        boolean secondHalf = false;
        while(iter.hasNext()){
            Token t = iter.next();
            if(t.getType() == TokenType.operator && t.getValue().matches(">|<|>=|<=|==|!=")){
                if(!secondHalf){
                    mid = t;
                    secondHalf = true;
                }else{
                    System.err.println("Invalid Comparison Operator !");
                }
            }else{
                if(!secondHalf){
                    left.add(t);
                }else{
                    right.add(t);
                }
            }
        }

        if(mid != null) {
            Chunk lchunk = parseMathExp(left);
            Chunk rchunk = parseMathExp(right);
            asm.generateCMP(lchunk, rchunk);
            Chunk boolAns = asm.getTempBoolChunk();
            switch(mid.getValue()){
                case "<":
                    asm.generateSHL(boolAns,boolAns);
                    break;
                case ">":
                    asm.generateSHL(boolAns,boolAns);
                    asm.generateNOT(boolAns,boolAns);
                    asm.generateBRZ(1);
                    asm.generateNOT(boolAns,boolAns);
                    break;
                case "==":
                    asm.generateMVR(boolAns,AsmGenerator.TRU);
                    asm.generateBRZ(1);
                    asm.generateNOT(boolAns,boolAns);
                    break;
                case "!=":
                    asm.generateMVR(boolAns,AsmGenerator.FLS);
                    asm.generateBRZ(1);
                    asm.generateNOT(boolAns,boolAns);
                    break;
                case "<=":
                    asm.generateSHL(boolAns,boolAns);
                    asm.generateBRZ(1);
                    asm.generateNOT(boolAns,boolAns);
                    break;
                case ">=":
                    asm.generateSHL(boolAns,boolAns);
                    asm.generateNOT(boolAns,boolAns);
                    break;
            }
            return boolAns;
        }else{
            System.err.println("Invalid Comparison Statement !");
            return null;
        }
    }

    //public Chunk parseBoolExp(){}

    public ArrayList<Token> simplizeBoolExp(ArrayList<Token> tokens){
        //System.out.println("MUST SIMPELIZE : ");
        //Token.PrintTokenSequence(tokens);

        ArrayList<Token> ret = new ArrayList<>();
        boolean addP = false;
        Token PS = null;
        Token PE = null;

        //Trim Start & End Paratethis
        int li = tokens.size()-1;
        if(tokens.get(0).getValue().equals("(") && tokens.get(li).getValue().equals(")")){
            PE = tokens.remove(li);
            PS = tokens.remove(0);
            addP = true;
        }

        Iterator<Token> iter = tokens.iterator();
        ArrayList<Token> lvl1exp = new ArrayList<>();
        //simplize level 11

        int pc = 0;
        while(iter.hasNext()){
            Token t = iter.next();
            if(t.getValue().equals("(")){
                pc++;
            }
            if(t.getValue().equals(")")){
                pc--;
            }
            //System.out.println("READ TOK : " + t);
            //System.out.println("PC = " + pc);
            if(t.getValue().matches("&&|\\|\\||!") && pc==0){
                ArrayList<Token> simplified = decideSIM(lvl1exp);
                for(Token st : simplified) ret.add(st);
                ret.add(t);
                lvl1exp = new ArrayList<>();
            }else{
                lvl1exp.add(t);
            }
            if(!iter.hasNext()){
                ArrayList<Token> simplified = decideSIM(lvl1exp);
                for(Token st : simplified) ret.add(st);
            }
        }
        if(addP){
            ret.add(0,PS);
            ret.add(PE);
        }
        return ret;
    }

    public ArrayList<Token> decideSIM(ArrayList<Token> tokens){
        //System.out.println("MUST DECIDE : ");
        //Token.PrintTokenSequence(tokens);
        boolean isVisitedBoolOp = false;
        boolean isVisitedCompOp = false;
        for(Token t : tokens){
            if(t.getValue().matches("&&|\\|\\||!")) isVisitedBoolOp = true;
            if(t.getValue().matches(">|<|>=|<=|==|!=")) isVisitedCompOp = true;
        }
        if (isVisitedBoolOp) return simplizeBoolExp(tokens);
        if(isVisitedCompOp){
            ArrayList<Token> singleToken = new ArrayList<>();
            //System.out.println("MUST PARSE COMP EXP : " );
            //Token.PrintTokenSequence(tokens);
            singleToken.add(Token.fromChunk(parseComparison(tokens)));
            return singleToken;
        }
        if(tokens.size()==1 && (tokens.get(0).getType()==TokenType.identifier || tokens.get(0).getType()==TokenType.keyword) ){
            return tokens;
        }
        System.err.println("Invalid Exp Inside Bool Exp (decideSIM)");
        return null;
    }

    private static int getOpPiority(Token opt){
        if(opt.getType() == TokenType.operator || opt.getType() == TokenType.punctuation){
            switch (opt.getValue()){
                case "++":
                    return 6;
                case "--":
                    return 5;
                case "*":
                    return 4;
                case "/":
                    return 3;
                case "+":
                    return 2;
                case "-":
                    return 1;
            }
        }else{
            System.err.println("Token Type must be operator !" + opt.getType());
        }
        return -1;
    }

    private static int getBoolOpPiority(Token opt){
        if(opt.getType() == TokenType.operator || opt.getType() == TokenType.punctuation){
            switch (opt.getValue()){
                case "!":
                    return 3;
                case "&&":
                    return 2;
                case "||":
                    return 1;
            }
        }else{
            System.err.println("Token Type must be operator !" + opt.getType());
        }
        return -1;
    }

}
