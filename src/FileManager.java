import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class FileManager {

    private static int fileSize = peerProcess.getFileSize(); 
    private static int pieceSize = peerProcess.getPieceSize();
    private static String fileName = peerProcess.getFileName();
    private static int numPieces = peerProcess.getPieces();
    private static byte[] fullFile = new byte [fileSize]; 
    private static HashMap<Integer, Boolean> pieceCheck;
    private static boolean hasAllPieces = false;
    
    public FileManager(){

    }

    public byte[] fileToPieces(int i){
        FileInputStream fileReader;
        byte[] fileBytes;
        byte[] pieceBytes;
        BufferedInputStream fileBuffer; 

        try {
            fileReader = new FileInputStream(new File(System.getProperty("user.dir") + "/" + fileName));
            fileBytes = new byte[fileSize];
            pieceBytes = new byte[pieceSize];
            fileBuffer = new BufferedInputStream(fileReader);
            fileBuffer.read(fileBytes,0,fileBytes.length); 
            System.arraycopy(fileBytes, i*pieceSize, pieceBytes, 0, Math.min(pieceSize,fileSize-i*pieceSize));
            return pieceBytes; 
        } catch (Exception e) {
            
        }
        return new byte[0]; 
    }

    public void piecesToFile(int i, byte[] piece){ // i is indexed at 0
        FileOutputStream fileWriter; 
        BufferedOutputStream fileBuffer; 

        try {
            pieceCheck.put(i,true);
            fileWriter = new FileOutputStream(System.getProperty("user.dir") + "/" + fileName, true);
            fileBuffer = new BufferedOutputStream(fileWriter);

            System.arraycopy(piece, 0, fullFile, pieceSize*i, piece.length); 

            if(hasAllPieces()){
                fileBuffer.write(fullFile, 0 , fullFile.length);
                fileBuffer.flush();
            }

        } catch (Exception e) {
            
        }
        //return new File(System.getProperty("user.dir") + "/" + fileName);
        
    }

    public boolean hasAllPieces(){ 
        for(int i = 0; i < numPieces; i++){
            if(pieceCheck.get(i) == false){
                return false;
            }
        }
        return true; 
    }

    public boolean hasPiece(int i){
        return pieceCheck.get(i);
    }

    // private ArrayList<Piece> fileToPieces(){
    //     FileInputStream fileReader;
    //     byte[] pieceBytes = new byte[0];
    // }
    
}
