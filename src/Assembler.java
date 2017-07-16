import java.util.HashMap;

/**
 * Created by Armin on 7/11/2017.
 */
public class Assembler {

    HashMap<String,Integer> label_loc = new HashMap<>();
    StringBuilder SB = new StringBuilder();

    private void pass1(String s){
        String[] lines = s.split("\n");
        int line_counter = 0;
        for(String l : lines){
            line_counter++;
            if(l.contains(",")){
                String lbl_name = l.split(",")[0];
                label_loc.put(lbl_name,line_counter);
            }
        }
    }

    private void pass2(String s){
        String[] lines = s.split("\n");
        int line_counter = 0;
        for(String l : lines){
            line_counter++;
            String li = l;
            if(l.contains(",")){
                li = l.split(",")[1];
            }
            String[] seg = li.split(" ");

            if(seg[0].equals("nop")){
                SB.append("0000000000000000\n");
            }else if(seg[0].equals("hlt")){
                SB.append("0000000100000000\n");
            }else if(seg[0].equals("szf")){

            }else if(seg[0].equals("czf")){

            }else if(seg[0].equals("scf")){

            }else if(seg[0].equals("ccf")){

            }else if(seg[0].equals("cwp")){

            }else if(seg[0].equals("mvr")){

            }else if(seg[0].equals("lda")){

            }else if(seg[0].equals("sta")){

            }else if(seg[0].equals("and")){

            }else if(seg[0].equals("orr")){

            }else if(seg[0].equals("not")){

            }else if(seg[0].equals("shl")){

            }else if(seg[0].equals("shr")){

            }else if(seg[0].equals("add")){

            }else if(seg[0].equals("sub")){

            }else if(seg[0].equals("mul")){

            }else if(seg[0].equals("cmp")){

            }else if(seg[0].equals("mil")){

            }else if(seg[0].equals("mih")){

            }else if(seg[0].equals("spc")){

            }else if(seg[0].equals("jpa")){

            }else if(seg[0].equals("jpr")){

            }else if(seg[0].equals("brz")){

            }else if(seg[0].equals("brc")){

            }else if(seg[0].equals("awp")){

            }
        }
    }

}
