import java.net.ServerSocket;
import java.net.Socket;

public class PeerListener implements Runnable {
    private Socket connectionSocket;
    private int connectedPeerId;
    private Peer connectedPeer;

    public PeerListener(int id) {
        connectedPeerId = id;
        connectedPeer = peerProcess.getPeerDictionary().get(id);
    }

    //@Override
    public void run(){
        ServerSocket servsock = null;
        Socket sock = null;
        Peer self = peerProcess.getPeerDictionary().get(peerProcess.getPeerId()); 
        //
        while(true){
            try {
                servsock = new ServerSocket(self.getPortNumber());
                sock = servsock.accept();
                System.out.println("Peer has connected! : " + sock);

            } catch (Exception e) {
                //TODO: handle exception
            }
            
        }
    }
}
