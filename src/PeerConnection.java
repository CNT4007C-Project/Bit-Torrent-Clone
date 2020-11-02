import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map; 

public class PeerConnection extends Thread {

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

    public void accept(){
        ServerSocket servsock = null;
        Socket sock = null;
        Peer self = peerProcess.getPeerDictionary().get(peerProcess.getPeerId()); 
        //
        while(true){
            try {
                servsock = new ServerSocket(self.getPortNumber());
                sock = servsock.accept();

            } catch (Exception e) {
                //TODO: handle exception
            }
            
        }
    }

}
