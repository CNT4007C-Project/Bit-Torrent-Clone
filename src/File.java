import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class File {

    private ArrayList<Piece> chunks = new ArrayList<Piece>();
    //private int 

    public File(){

        HashMap<Integer, Peer> peerDictionary = peerProcess.getPeerDictionary();
        if(peerDictionary.get(peerProcess.getPeerId()).getHasFile() == true){
            chunks = fileToPieces();
            chunks = null;
        }
    }

    
    private ArrayList<Piece> fileToPieces(){
        FileInputStream fileReader;
        return new ArrayList<>();
    }
    
}
