import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class PeerConnection {

    private Socket connectionSocket;
    private int connectedPeerId;
    private Peer connectedPeer;

    public PeerConnection(int id) {
        connectedPeerId = id;
        connectedPeer = peerProcess.getPeerDictionary().get(id);
    }

    public void connect() {
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
