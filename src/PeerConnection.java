import java.net.Socket;

public class PeerConnection {

    private Socket connectionSocket;
    private int connectedPeerId;

    public PeerConnection(Socket s, int id) {
        connectionSocket = s;
        connectedPeerId = id;
    }

}
