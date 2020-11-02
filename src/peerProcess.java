package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;

// this process should take in the peerID as an argument and run the peer process

// TODO figure out if subdirectories should be created at runtime or manually beforehand
// FAQ says rthey can be created manually beforehand

// TODO one socket for every neighboring peer, two threads per socket (sending and receiving)

class peerProcess {
    private static int peerId; // peer ID for THIS peer

    // Common.cfg variables
    private static int numberOfPreferredNeighbors;
    private static int unchokingInterval;
    private static int optimisticUnchokingInterval;
    private static String fileName;
    private static int fileSize;
    private static int pieceSize;

    // PeerInfo.cfg variables
    private static HashMap<Integer, Peer> peerDictionary = new HashMap<>(); // includes info of every peer including
                                                                            // THIS one

    private static BitSet bitField;

    public static void main(String[] args) {
        peerId = Integer.parseInt(args[0]);
        initialize();
    }

    public static int getPeerId(){
        return peerId;
    }

    public static int getPieceSize(){
        return pieceSize;
    }

    public static int getFileSize(){
        return fileSize;
    }

    public static String getFileName(){
        return fileName;
    }

    public static int getNumPieces(){
        return (int) Math.ceil(fileSize / (double) pieceSize);
    }

    public static HashMap<Integer, Peer> getPeerDictionary(){
        return peerDictionary;
    }

    /* used ot read both .cfg files */
    private static void initialize() {

        BufferedReader commonBufferedReader = null;
        BufferedReader peerInfoBufferedReader = null;

        try {

            /* Read Common.cfg */
            commonBufferedReader = new BufferedReader(new FileReader("./Common.cfg"));

            // TODO this could probably be a loop that automatically names the variables in
            // a HashMap;\

            String currentLine = commonBufferedReader.readLine();
            String numberOfPreferredNeighborsString = currentLine.split(" ")[1];
            numberOfPreferredNeighbors = Integer.parseInt(numberOfPreferredNeighborsString);

            currentLine = commonBufferedReader.readLine();
            String unchokingIntervalString = currentLine.split(" ")[1];
            unchokingInterval = Integer.parseInt(unchokingIntervalString);

            currentLine = commonBufferedReader.readLine();
            String optimisticUnchokingIntervalString = currentLine.split(" ")[1];
            optimisticUnchokingInterval = Integer.parseInt(optimisticUnchokingIntervalString);

            currentLine = commonBufferedReader.readLine();
            fileName = currentLine.split(" ")[1];

            currentLine = commonBufferedReader.readLine();
            String fileSizeString = currentLine.split(" ")[1];
            fileSize = Integer.parseInt(fileSizeString);

            currentLine = commonBufferedReader.readLine();
            String pieceSizeString = currentLine.split(" ")[1];
            pieceSize = Integer.parseInt(pieceSizeString);

            /* Read PeerInfo.cfg */
            peerInfoBufferedReader = new BufferedReader(new FileReader("./PeerInfo.cfg"));
            String[] strings = null;

            currentLine = peerInfoBufferedReader.readLine();
            while (currentLine != null) {
                strings = currentLine.split(" ");
                Peer peer = new Peer(Integer.parseInt(strings[0]), strings[1], Integer.parseInt(strings[2]),
                        Integer.parseInt(strings[3]));
                peerDictionary.put(Integer.parseInt(strings[0]), peer);
                currentLine = peerInfoBufferedReader.readLine();
                
            }

            int pieces = (int) Math.ceil(fileSize / (double) pieceSize);
            
            bitField = new BitSet(pieces);

            // set bit field to all ones if this peer has the file
            if (peerDictionary.get(peerId).getHasFile()) {
                bitField.set(0, pieces);
            }

            ConnectionProcess.startConnection();
            

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            // close reader
            try {
                if (commonBufferedReader != null)
                    commonBufferedReader.close();
                if (peerInfoBufferedReader != null)
                    peerInfoBufferedReader.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

}