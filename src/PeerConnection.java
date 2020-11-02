import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

public class PeerConnection implements Runnable {

    private Socket connectionSocket;
    private int connectedPeerId;
    private Peer connectedPeer;

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
    }

}
