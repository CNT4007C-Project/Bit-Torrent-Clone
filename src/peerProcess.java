import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.BitSet;
import java.util.HashMap;

// this process should take in the peerID as an argument and run the peer process

// TODO figure out if subdirectories should be created at runtime or manually beforehand
// FAQ says rthey can be created manually beforehand

// TODO one socket for every neighboring peer, two threads per socket (sending and receiving)

// can we assum epeer IDs will be in the correct order?

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

    private static byte[] bitField;
    private static HashMap<Integer, PeerConnection> connectionManager = new HashMap<>();

    public static void main(String[] args) {
        peerId = Integer.parseInt(args[0]);
        System.out.println("Initializing");
        initialize();
        System.out.println("Starting Receiving Connections");
        acceptConnections();
        System.out.println("Starting Sending Conenctions");
        requestConnections();
    }

    public static int getPeerId() {
        return peerId;
    }

    public static int getPieceSize() {
        return pieceSize;
    }

    public static HashMap<Integer, Peer> getPeerDictionary() {
        return peerDictionary;
    }

    public static byte[] getBitfield() {
        return bitField;
    }

    public static int getPieces() {
        return (int) Math.ceil(fileSize / (double) pieceSize);
    }

    /* used ot read both .cfg files */
    private static void initialize() {

        BufferedReader commonBufferedReader = null;
        BufferedReader peerInfoBufferedReader = null;

        try {

            /* Read Common.cfg */
            commonBufferedReader = new BufferedReader(new FileReader("../Common.cfg"));

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
            peerInfoBufferedReader = new BufferedReader(new FileReader("../LocalPeerInfo.cfg"));
            /*
             * TODO "Local" version is just for testing locally, should be changed for
             * submission
             */

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
            bitField = new byte[(int) Math.ceil((double) pieces / 8.0)];

            // BitfieldUtility.printBitfield(bitField);

            // set bit field to all ones if this peer has the file
            if (peerDictionary.get(peerId).getHasFile()) {
                for (int i = 0; i < pieces; i++) {
                    BitfieldUtility.setBit(bitField, i, true);
                }
            }

            // BitfieldUtility.printBitfield(bitField);

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

    // reaches out to all peers that came before it to initiate connections
    public static void requestConnections() {

        Socket toExisting = null;
        for (int i = 1001; i < peerId; i++) {
            Peer existingPeer = peerDictionary.get(i);
            try {

                toExisting = new Socket(existingPeer.getHostName(), existingPeer.getPortNumber());

                System.err.println("Connected to peer " + i + " at port " + existingPeer.getPortNumber() + "!");
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                System.err.println("The host listed for peer " + i + " is not recognized");
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            connectionManager.put(i, new PeerConnection(i, toExisting));
        }

        connectionManager.forEach((id, peer) -> {
            Thread s = new Thread(() -> peer.run());
            s.start();
        });
    }

    public static void acceptConnections() {
        PeerListener accept = new PeerListener(peerId);
        Thread t = new Thread(() -> accept.run());
        t.start();
    }
}