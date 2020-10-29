import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionProcess {

    private ArrayList<Piece> chunks = new ArrayList<Piece>();
    private int fileSize = peerProcess.getFileSize();
    private int pieceSize = peerProcess.getPieceSize();
    private byte[] pieceBytes;
    private FileInputStream fileReader;
    private BufferedInputStream fileBuffer;
    private HashMap<Integer, Peer> peerDictionary = peerProcess.getPeerDictionary();

    public ConnectionProcess(){

        if(peerDictionary.get(peerProcess.getPeerId()).getHasFile() == true){
            //chunks = fileToPieces();
            fileToPieces();
        }
    }

    // private ArrayList<Piece> fileToPieces(){
    private void fileToPieces(){
        try {
            fileReader = new FileInputStream(new File(peerProcess.getFileName()));
            pieceBytes = new byte[pieceSize];
            fileBuffer = new BufferedInputStream(fileReader);
            fileBuffer.read(pieceBytes, 0, pieceSize);
        } catch (Exception e) {
            //TODO: handle exception
        }
        
    }

    private void startConnection(){
        ServerSocket servsock;
        Socket sock;
        OutputStream connectionOutput;
        int portno = getPortNo();

        try {
            sock = servsock.accept();
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    private int getPortNo(){   
        return peerDictionary.get(peerProcess.getPeerId()).getPortNumber();
    }
    
}
