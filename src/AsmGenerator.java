import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Armin on 5/31/2017.
 */
public class AsmGenerator {

    StringBuilder SB = new StringBuilder();

    private static HashMap<String,Chunk> declaredVars = new HashMap<>();
    public void declareVAR(String name , Chunk c){
        declaredVars.put(name,c);
    }
    public Chunk getVAR(String name) {
        return declaredVars.get(name);
    }

    /*public Chunk findChunkByName(String name){
        for(Chunk c : declaredVars){
            if(c.Name.equals(name)) return c;
        }
        return null;
    }

    public static String finalVarString(){
        StringBuilder FVSB = new StringBuilder();
        for(Chunk c: declaredVars){
            FVSB.append(c.Name + ", ___\n");
        }
        return FVSB.toString();
    }*/

    public static Chunk ZER = new Chunk("ZER",ChunkType.reg_integer);
    public static Chunk TRU = new Chunk("ZER",ChunkType.reg_integer);
    public static Chunk FLS = new Chunk("ZER",ChunkType.reg_integer);
    public static Chunk NUL = new Chunk("ZER",ChunkType.reg_integer);

    int tmp_chunk_count = 0;
    public Chunk getTempIntegerChunk(){
        tmp_chunk_count++;
        return new Chunk("$" + tmp_chunk_count,ChunkType.reg_integer);
    }

    public Chunk getTempBoolChunk(){
        tmp_chunk_count++;
        return new Chunk("$" + tmp_chunk_count,ChunkType.reg_bool);
    }

    static int lblc = 0;
    public String getLabel(){
        lblc++;
        return "LBL" + lblc;
    }

    public void reduceLabel(){
        lblc--;
    }


    public void generateADD(Chunk dest,Chunk src){
        SB.append("add " + dest.Name + " " + src.Name + "\n");
    }

    public void generateSUB(Chunk dest,Chunk src){
        SB.append("sub " + dest.Name + " " + src.Name + "\n");
    }

    public void generateMUL(Chunk dest,Chunk src){
        SB.append("mul " + dest.Name + " " + src.Name + "\n");
    }

    public void generateDIV(Chunk dest,Chunk src){
        SB.append("div " + dest.Name + " " + src.Name + "\n");
    }

    public void generateINC(Chunk reg){
        SB.append("inc " + reg.Name + "\n");
    }

    public void generateDEC(Chunk reg){
        SB.append("dec " + reg.Name + "\n");
    }

    public void generateMIL(Chunk dest,int imd){
        SB.append("mil " + dest.Name + " " + imd + "\n");
    }

    public void generateMIL(Chunk dest,boolean imd){
        if(imd) {
            SB.append("mil " + dest.Name + " " + "TRU" + "\n");
        }else{
            SB.append("mil " + dest.Name + " " + "FLS" + "\n");
        }
    }

    public void generateCMP(Chunk r1 , Chunk r2) {
        SB.append("cmp " + r1.Name + " " + r2.Name + "\n");
    }

    public void generateSHL(Chunk dest,Chunk src) {
        SB.append("shl " + dest.Name + " " + src.Name + "\n");
    }

    public void generateSHR(Chunk dest,Chunk src) {
        SB.append("shr " + dest.Name + " " + src.Name + "\n");
    }

    public void generateNOT(Chunk dest,Chunk src) {
        SB.append("not " + dest.Name + " " + src.Name + "\n");
    }

    public void generateMVR(Chunk dest,Chunk src) {
        SB.append("mvr " + dest.Name + " " + src.Name + "\n");
    }

    public void generateBRZ(int imd) {
        SB.append("brz " + imd + "\n");
    }

    public void generateBRC(int imd) {
        SB.append("brc " + imd + "\n");
    }

    public void generateBRC(String lbl) {
        SB.append("brc " + lbl + "\n");
    }

    public void generateJMP(String lbl)  {
        SB.append("jmp " + lbl + "\n");
    }

    public void generateAND(Chunk dest, Chunk src){
        SB.append("and " + dest.Name + " " + src.Name + "\n");
    }

    public void generateOR(Chunk dest, Chunk src){
        SB.append("orr " + dest.Name + " " + src.Name + "\n");
    }

    public void generateNOP() {
        SB.append("nop " + "\n");
    }


    public void put(String code){
        SB.append(code);
    }

    public void putLabel(String lbl){
        SB.append(lbl + ",");
    }

    public String getASM() {
        return SB.toString();
    }

    //public String getFinalASM() {
    //return SB.toString() + "\n" + finalVarString();
    //}
    public String getFinalASM() {
        return SB.toString() + "\n";
    }
}
