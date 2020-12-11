import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.Random;

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
    private static FileManager fileManager;

    // PeerInfo.cfg variables
    private static HashMap<Integer, Peer> peerDictionary = new HashMap<>(); // includes info of every peer including
                                                                            // THIS one

    private static byte[] bitField;
    private static HashMap<Integer, PeerConnection> connectionManager = new HashMap<>();
    private static PeerListener listenerObj;

    public static void main(String[] args) {
        // Do not log before running the next line
        peerId = Integer.parseInt(args[0]);
        System.out.println("Initializing");
        initialize();
        fileManager = new FileManager();
        System.out.println("Starting Receiving Connections");
        acceptConnections();
        System.out.println("Starting Sending Connections");
        requestConnections();

        boolean allPeersHaveFile = false;
        long previousUnchoke = System.currentTimeMillis();
        long previousOptimUnchoke = System.currentTimeMillis();

        /*
         * Peer peer = peerDictionary.get(peerId); for (HashMap.Entry<Integer,
         * PeerConnection> entry : connectionManager.entrySet()) { // Clients start by
         * choked and not interested entry.getValue().sendChoke(); } // Listener should
         * choke new connections automatically // Should be fine not to choke anything
         * at beginning
         */

        boolean first = true;
        // ASSUMING THAT connectionManager has all peers

        Integer[] preferredNeighbors = new Integer[numberOfPreferredNeighbors];
        HashMap<Integer, Integer> downloadRate = new HashMap<>();
        Random random = new Random();
        ArrayList<Integer> unchoked = new ArrayList<>();
        ArrayList<Integer> choked = new ArrayList<>();
        while (!allPeersHaveFile) {

            ArrayList<Integer> interested = new ArrayList<>();
            HashMap<Integer, PeerConnection> copy = new HashMap<Integer, PeerConnection>(connectionManager);
            for (HashMap.Entry<Integer, PeerConnection> entry : copy.entrySet()) {
                if (entry.getValue().isInterested()) {
                    interested.add(entry.getKey());
                }
            }

            long currentTime = System.currentTimeMillis();
            int iter = 0;
            if (currentTime - previousUnchoke > unchokingInterval && connectionManager.size() != 0) {
                if (first) {
                    // Object[] peers = peerDictionary.keySet().toArray();
                    Object[] peers = connectionManager.keySet().toArray();
                    for (int i = 0; i < numberOfPreferredNeighbors; i++) {
                        int choice;
                        do {
                            System.out.println("hmmm");
                            int ind = random.nextInt(peers.length);
                            // System.out.println(Integer.toString(ind));
                            // choice = (Integer) peers[random.nextInt(peers.length)];
                            choice = (Integer) peers[ind];
                        } while (choice == peerId);
                        System.out.println("choice: " + choice);
                        preferredNeighbors[iter] = choice;
                        iter++;
                    }
                    System.out.println(preferredNeighbors.length);
                    for (int i = 0; i < numberOfPreferredNeighbors; i++) {
                        connectionManager.get(preferredNeighbors[i]).sendUnchoke();
                    }
                } else {
                    ArrayList<Integer> sorted = new ArrayList<>();
                    for (HashMap.Entry<Integer, PeerConnection> entry : connectionManager.entrySet()) {
                        downloadRate.put(entry.getKey(), entry.getValue().piecesReceived());
                        boolean inserted = false;
                        for (int i = 0; i < sorted.size(); i++) {
                            if (sorted.get(i) > entry.getValue().piecesReceived()) {
                                sorted.add(i, entry.getKey());
                                inserted = true;
                            }
                        }
                        // Collections.sort(sorted);
                        // Collections.reverse(list);
                        if (!inserted) {
                            sorted.add(entry.getKey());
                        }
                        if (entry.getValue().isInterested()) {
                            if (!interested.contains(entry.getKey())) {
                                interested.add(entry.getKey());
                            }
                        } else {
                            if (interested.contains(entry.getKey())) {
                                interested.remove(entry.getKey());
                            }
                        }
                    }

                    String preferred = "";
                    int modifier = 0;
                    for (int i = 0; i < sorted.size(); i++) {
                        if (i < numberOfPreferredNeighbors + modifier) {
                            if (!interested.contains(sorted.get(i))) {
                                modifier++;
                            } else {
                                preferred += sorted.get(i) + ", ";
                            }
                        } else {
                            break;
                        }
                    }

                    connectionManager.get(peerId).updatePreferred(preferred.substring(0, preferred.length() - 2));

                    modifier = 0;
                    for (int i = 0; i < sorted.size(); i++) {
                        if (i < numberOfPreferredNeighbors + modifier) {
                            if (!interested.contains(sorted.get(i))) {
                                modifier++;
                            } else if (!unchoked.contains(sorted.get(i))) {
                                unchoked.add(sorted.get(i));
                                choked.remove(sorted.get(i));
                                connectionManager.get(sorted.get(i)).sendUnchoke();
                            }
                        } else {
                            if (!choked.contains(sorted.get(i))) {
                                unchoked.remove(sorted.get(i));
                                choked.add(sorted.get(i));
                                connectionManager.get(sorted.get(i)).sendChoke();
                            }
                        }
                    }
                }
                previousUnchoke = System.currentTimeMillis();
            }

            if (currentTime - previousOptimUnchoke > optimisticUnchokingInterval && connectionManager.size() > 1
                    && choked.size() != 0) {
                int choice;
                System.out.println("Choked size: " + choked.size());
                do {
                    choice = choked.get(random.nextInt(choked.size()));
                } while (!interested.contains(choice));
                connectionManager.get(choice).sendOptimUnchoke();
                choked.remove(choice);
                unchoked.add(choice);
                previousOptimUnchoke = System.currentTimeMillis();
            }

            allPeersHaveFile = true;
            for (HashMap.Entry<Integer, Peer> entry : peerDictionary.entrySet()) {
                if (!entry.getValue().getHasFile()) {
                    allPeersHaveFile = false;
                    break;
                }
            }
        }
        terminateThreads();
    }

    public static int getPeerId() {
        return peerId;
    }

    public static FileManager getFileManager() {
        return fileManager;
    }

    public static void startNewConnection(Socket s) {
        // connectionManager.put(i, new PeerConnection(i, toExisting));
    }

    public static int getPieceSize() {
        return pieceSize;
    }

    public static int getFileSize() {
        return fileSize;
    }

    public static String getFileName() {
        return fileName;
    }

    public static HashMap<Integer, Peer> getPeerDictionary() {
        return peerDictionary;
    }

    public static HashMap<Integer, PeerConnection> getConnectionManager() {
        return connectionManager;
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
            commonBufferedReader = new BufferedReader(new FileReader("./Common.cfg"));

            // TODO this could probably be a loop that automatically names the variables in
            // a HashMap;\

            String currentLine = commonBufferedReader.readLine();
            // System.out.println(currentLine);
            String numberOfPreferredNeighborsString = currentLine.split(" ")[1];
            numberOfPreferredNeighbors = Integer.parseInt(numberOfPreferredNeighborsString);

            currentLine = commonBufferedReader.readLine();
            String unchokingIntervalString = currentLine.split(" ")[1];
            unchokingInterval = Integer.parseInt(unchokingIntervalString) * 1000;

            currentLine = commonBufferedReader.readLine();
            String optimisticUnchokingIntervalString = currentLine.split(" ")[1];
            optimisticUnchokingInterval = Integer.parseInt(optimisticUnchokingIntervalString) * 1000;

            currentLine = commonBufferedReader.readLine();
            fileName = currentLine.split(" ")[1];

            currentLine = commonBufferedReader.readLine();
            String fileSizeString = currentLine.split(" ")[1];
            fileSize = Integer.parseInt(fileSizeString);

            currentLine = commonBufferedReader.readLine();
            String pieceSizeString = currentLine.split(" ")[1];
            pieceSize = Integer.parseInt(pieceSizeString);

            /* Read PeerInfo.cfg */
            peerInfoBufferedReader = new BufferedReader(new FileReader("./LocalPeerInfo.cfg"));
            /*
             * TODO "Local" version is just for testing locally, should be changed for
             * submission
             */

            String[] strings = null;

            currentLine = peerInfoBufferedReader.readLine();
            // System.out.println(currentLine);
            while (currentLine != null) {
                strings = currentLine.split(" ");
                Peer peer = new Peer(Integer.parseInt(strings[0]), strings[1], Integer.parseInt(strings[2]),
                        Integer.parseInt(strings[3]));
                peerDictionary.put(Integer.parseInt(strings[0]), peer);
                currentLine = peerInfoBufferedReader.readLine();

            }

            int pieces = (int) Math.ceil(fileSize / (double) pieceSize);
            // System.out.println(pieces);
            bitField = new byte[(int) Math.ceil((double) pieces / 8.0)];

            // System.out.println((int) Math.ceil((double) pieces / 8.0));
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

                Logger.write("Peer " + peerId + " makes a connection to Peer " + i + ".");
                System.err.println("Connected to peer " + i + " at port " + existingPeer.getPortNumber() + "!");
            } catch (UnknownHostException e) {
                System.err.println("The host listed for peer " + i + " is not recognized");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connectionManager.put(i, new PeerConnection(i, toExisting));
        }

        connectionManager.forEach((id, peer) -> {
            Thread s = new Thread(() -> peer.run());
            s.start();
        });
    }

    // from peers that come after
    public static void acceptConnections() {
        PeerListener accept = new PeerListener(peerId);
        Thread t = new Thread(() -> accept.run());
        listenerObj = accept;
        t.start();
    }

    private static void terminateThreads() {
        // Log here
        System.out.println("Terminating ...");
        for (HashMap.Entry<Integer, PeerConnection> entry : connectionManager.entrySet()) {
            entry.getValue().terminate();
        }
        listenerObj.terminate();
    }

}