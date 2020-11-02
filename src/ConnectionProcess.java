package src;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream; 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.InputStream; 
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map; 

public class ConnectionProcess {

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

    public static void startConnection(){
        ServerSocket servsock = null;
        Socket sock = null;
        OutputStream connectionOutput = null;
        if(peerDictionary.get(peerProcess.getPeerId()).getHasFile() == true){
            System.out.println("Sending File Mode");
            while(true){
                try {
                    servsock = new ServerSocket(portno); 
                    sock = servsock.accept();
                    connectionOutput = sock.getOutputStream();
                    for (int i = 0; i < numPieces; i++){
                        connectionOutput.write(fileToPieces(i),0, pieceSize);
                    }
                    connectionOutput.flush();
                    
                } catch (Exception e) {
                    //TODO: handle exception
                }
            }
        }
        else{
            System.out.println("Receiving File Mode");
            int bytesRead;
            int current = 0;
            Peer peerWithFile = null;
            // while(peerDictionary.get(peerProcess.getPeerId()).getHasFile() != true){
            //     int hasFilePort = 6008; // TODO: change this to get port from config file
            //     int bytesRead;
            //     int current = 0;
                

            // }
            for(Map.Entry<Integer,Peer> entry : peerDictionary.entrySet()){
                if(entry.getValue().getHasFile()){
                    peerWithFile = entry.getValue();
                }
            }
            if (peerWithFile == null){
                System.out.println("No one has file");
                return;
            }
            // System.out.println(peerWithFile.getPeerId() + " " + peerWithFile.getPortNumber());
            try {
                sock = new Socket(peerWithFile.getHostName(), peerWithFile.getPortNumber());
                System.out.println("Connecting..."); 


            } catch (Exception e) {
                //TODO: handle exception
            }
        }
   
    }    
}

/* How to iterate through hashmap:

for(Map.Entry<Integer,Peer> entry : peerDictionary.entrySet()){
    System.out.println("key = " + entry.getKey() + ", Value = " + entry.getValue().getHasFile());

} */

/* Reference/Citation:

Transfer file via Socket: https://www.rgagnon.com/javadetails/java-0542.html
Iterate Hashmap: https://www.geeksforgeeks.org/iterate-map-java/

*/