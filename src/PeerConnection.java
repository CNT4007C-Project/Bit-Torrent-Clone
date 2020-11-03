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

public class PeerConnection implements Runnable {

    private Socket connectionSocket;
    private int connectedPeerId;
    private Peer connectedPeer;
    BufferedInputStream inputStream;
    BufferedOutputStream outputStream;

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

        sendHandshake();

    }

    public void sendHandshake() {
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

}
