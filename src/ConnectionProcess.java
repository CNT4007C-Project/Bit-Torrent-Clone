import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionProcess {

    private static ArrayList<Piece> chunks = new ArrayList<Piece>();
    private static int fileSize = peerProcess.getFileSize();
    private static int pieceSize = peerProcess.getPieceSize();
    private static byte[] pieceBytes;
    
    private static BufferedInputStream fileBuffer;
    private static HashMap<Integer, Peer> peerDictionary = peerProcess.getPeerDictionary();
    private static int portno = peerDictionary.get(peerProcess.getPeerId()).getPortNumber();
    private static String fileName = peerProcess.getFileName();
    private static int numPieces = peerProcess.getNumPieces();

    public ConnectionProcess(){

        if(peerDictionary.get(peerProcess.getPeerId()).getHasFile() == true){
            //chunks = fileToPieces();
            startConnection();
        }
    }

    // private ArrayList<Piece> fileToPieces(){
    private static byte[] fileToPieces(int i){
        FileInputStream fileReader;

        try {
            System.out.println(System.getProperty("user.dir")+"/"+fileName);
            fileReader = new FileInputStream(new File(System.getProperty("user.dir")+"/"+fileName));
            pieceBytes = new byte[fileSize];
            fileBuffer = new BufferedInputStream(fileReader);
            //fileBuffer.read(pieceBytes, i*pieceSize, 1+i+pieceSize);
            fileBuffer.read(pieceBytes, 0, fileSize);
            return pieceBytes;
        } catch (Exception e) {
            //TODO: handle exception
        }
        return new byte[0];
    }

    public static void startConnection(){
        ServerSocket servsock;
        Socket sock;
        OutputStream connectionOutput;
        while(true){
            try {
                servsock = new ServerSocket(portno);
                sock = servsock.accept();
                connectionOutput = sock.getOutputStream();
                // for (int i = 0; i < numPieces; i++){
                    connectionOutput.write(fileToPieces(0),0,104346);
                // }
                connectionOutput.flush();
                
            } catch (Exception e) {
                //TODO: handle exception
            }
        }   
    }    
}
