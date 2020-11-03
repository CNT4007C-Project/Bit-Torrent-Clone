import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class FileMgr {

    private static ArrayList<Piece> chunks = new ArrayList<Piece>();
    private static int fileSize = peerProcess.getFileSize(); 
    private static int pieceSize = peerProcess.getPieceSize();
    private static byte[] pieceBytesInput;
    private static byte[] pieceBytesOutput;
    private static BufferedInputStream fileBufferInput;
    private static BufferedOutputStream fileBufferOutput;
    private static HashMap<Integer, Peer> peerDictionary = peerProcess.getPeerDictionary();
    private static int portno = peerDictionary.get(peerProcess.getPeerId()).getPortNumber();
    private static String fileName = peerProcess.getFileName();
    private static int numPieces = peerProcess.getPieces();
    public FileMgr(){

        HashMap<Integer, Peer> peerDictionary = peerProcess.getPeerDictionary();
        if(peerDictionary.get(peerProcess.getPeerId()).getHasFile() == true){
            //chunks = fileToPieces();
        }
    }

    private static byte[] fileToPieces(int i){
        FileInputStream fileReader;

        try {
            fileReader = new FileInputStream(new File(System.getProperty("user.dir") + "/" + fileName));
            pieceBytesInput = new byte[fileSize];
            fileBufferInput = new BufferedInputStream(fileReader);
            fileBufferInput.read(pieceBytesInput, i*pieceSize, 1+i+pieceSize);
            //fileBufferInput.read(pieceBytes, 0, fileSize);
            return pieceBytesInput;
        } catch (Exception e) {
            
        }
        return new byte[0];
    }

    private static File piecesToFile(int i){
        FileOutputStream fileWriter; 

        try {
            //fileWriter = new FileOutputStream(System.getProperty("user.dir") + "/" + fileName);
            fileWriter = new FileOutputStream("/Users/mustafamohamed/Desktop/3d5.jpg");
            pieceBytesOutput = new byte[fileSize]; 
            fileBufferOutput = new BufferedOutputStream(fileWriter);
            fileBufferOutput.write(pieceBytesOutput, i*pieceSize, 1+i+pieceSize); 
            fileBufferOutput.flush();
        } catch (Exception e) {
            
        }
        //return new File(System.getProperty("user.dir") + "/" + fileName);
        return new File("/Users/mustafamohamed/Desktop/3d5.jpg"); 
        
    }

    // private ArrayList<Piece> fileToPieces(){
    //     FileInputStream fileReader;
    //     byte[] pieceBytes = new byte[0];
    // }
    
}
