/**
 * Created by Armin on 5/31/2017.
 */
public class Chunk {

    public String Name;
    public boolean isTemp;

    private ChunkType Type;
    public ChunkType getType(){
        return Type;
    }

    public Chunk(String Name,ChunkType Type){
        this.Name = Name;
        this.Type = Type;
    }

}
