import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.InflaterOutputStream;

public class PeerConnection implements Runnable {

    private Socket connectionSocket;
    private int connectedPeerId;
    private Peer connectedPeer;
    BufferedInputStream inputStream;
    BufferedOutputStream outputStream;
    private boolean handshakeReceived = false;

    public PeerConnection(int id, Socket s) { // for requested connections where destination is known
        connectedPeerId = id;
        connectedPeer = peerProcess.getPeerDictionary().get(id);
        connectionSocket = s;
    }

    public PeerConnection(Socket s) { // for listened connections when peer ID is unknown
        connectedPeerId = 0;
        connectedPeer = null;
        connectionSocket = s;
    }

    @Override
    public void run() {
        // this part isnt necessary anymore since the socket would already exist from
        // outside
        try {

            connectionSocket = new Socket(connectedPeer.getHostName(), connectedPeer.getPortNumber());
            System.err.println(
                    "Connected to peer " + connectedPeerId + " at port " + connectedPeer.getPortNumber() + "!");

        } catch (UnknownHostException e) {
            System.err.println("The host listed for peer " + connectedPeerId + " is not recognized");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            inputStream = new BufferedInputStream(connectionSocket.getInputStream());
            outputStream = new BufferedOutputStream(connectionSocket.getOutputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // System.out.println(peerProcess.getBitfield().length + " " +
        // peerProcess.getPieces());
        sendHandshake();
        receiveHandshake();
        if (handshakeReceived) {
            // start regular message process
            sendBitfieldMessage();
            listenForMessages(); // TODO this needs to be a thread
        }

    }

    public synchronized void sendHandshake() {
        String handshakeHeader = "P2PFILESHARINGPROJ";
        byte[] handshakeMessage = new byte[32];

        // string to byte array
        try {
            byte[] header = handshakeHeader.getBytes("UTF-8");
            System.arraycopy(header, 0, handshakeMessage, 0, header.length);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        byte[] padding = new byte[10];
        System.arraycopy(padding, 0, handshakeMessage, 18, 10);

        // integer to byte array
        byte[] id = new byte[4];
        id[0] = (byte) (peerProcess.getPeerId() >>> 24);
        id[1] = (byte) (peerProcess.getPeerId() >>> 16);
        id[2] = (byte) (peerProcess.getPeerId() >>> 8);
        id[3] = (byte) (peerProcess.getPeerId() >>> 0);

        System.arraycopy(id, 0, handshakeMessage, 28, 4);

        try {
            outputStream.write(handshakeMessage);
            outputStream.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void receiveHandshake() {
        byte[] incomingHandshake = new byte[32];
        try {
            inputStream.read(incomingHandshake);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String handshakeHeader;
        try {
            handshakeHeader = new String((Arrays.copyOfRange(incomingHandshake, 0, 18)), "UTF_8");

            if (handshakeHeader.equals("P2PFILESHARINGPROJ")) {
                // TODO some logic to see if peerID is the "expected" peer as per the spec
                byte[] id = (Arrays.copyOfRange(incomingHandshake, 28, 32));
                int temp = (int) (id[0] << 24);
                temp += (int) (id[1] << 16);
                temp += (int) (id[2] << 8);
                temp += (int) (id[3] << 0);

                connectedPeerId = temp;
                connectedPeer = peerProcess.getPeerDictionary().get(connectedPeerId);
                handshakeReceived = true; // does there need to be some sort of ack as well? Or does that happen
                                          // automatically?
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public synchronized void sendBitfieldMessage() {

        int bitfieldLength = peerProcess.getBitfield().length;
        byte[] message = new byte[4 + 1 + bitfieldLength];

        byte[] messageLength = new byte[4];
        messageLength[0] = (byte) (bitfieldLength >>> 24);
        messageLength[1] = (byte) (bitfieldLength >>> 16);
        messageLength[2] = (byte) (bitfieldLength >>> 8);
        messageLength[3] = (byte) (bitfieldLength >>> 0);

        System.arraycopy(messageLength, 0, message, 0, 4);

        byte[] messageType = { (byte) 5 };

        System.arraycopy(messageType, 0, message, 4, 1);

        System.arraycopy(peerProcess.getBitfield(), 0, message, 5, bitfieldLength);

        try {
            outputStream.write(message);
            outputStream.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void listenForMessages() {

        while (true) { // once again, this should probably be a thread
            processMessage();
        }

    }

    public synchronized void processMessage() { // TODO confirm what "synchronized does"

        byte[] messageLength = new byte[4];
        byte[] messageType = new byte[1];

        try {
            inputStream.read(messageLength); // TODO confirm if length of bytes needs to be specified
            inputStream.read(messageType);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int type = messageType[0];

        switch (type) {
            case 0: // choke
                break;
            case 1: // unchoke
                break;
            case 2: // interested
                break;
            case 3: // not interested
                break;
            case 4: // have
                byte[] pieceIndex = new byte[4];
                try {
                    inputStream.read(pieceIndex);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            default:
                System.err.println("Message type error!");

        }

    }

}
