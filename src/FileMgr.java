import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class FileMgr {

    private ArrayList<Piece> chunks = new ArrayList<Piece>();

    public FileMgr(){

        HashMap<Integer, Peer> peerDictionary = peerProcess.getPeerDictionary();
        if(peerDictionary.get(peerProcess.getPeerId()).getHasFile() == true){
            //chunks = fileToPieces();
        }
    }

    // private ArrayList<Piece> fileToPieces(){
    //     FileInputStream fileReader;
    //     byte[] pieceBytes = new byte[0];
    // }
    
}
