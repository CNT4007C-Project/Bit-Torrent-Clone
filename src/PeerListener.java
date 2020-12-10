import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class PeerListener implements Runnable {
    private Socket connectionSocket;
    private int connectedPeerId;
    private Peer connectedPeer;
    private Socket sock;
    private volatile boolean running;

    public PeerListener(int id) {
        connectedPeerId = id;
        connectedPeer = peerProcess.getPeerDictionary().get(id);
        running = true;
    }

    public void terminate() {
        running = false;
        try {
            if (sock != null) {
                sock.close();
            }
        } catch (IOException e) {
            //Socket forced to close
        }
    }

    public void sendStartChoke() {
        //Choke all peers that connect to this peer
    }

    // @Override
    public void run() {
        Peer self = peerProcess.getPeerDictionary().get(peerProcess.getPeerId());
        sock = null;

        //
        /*
        * while (true) { try { System.out.println("LISTENING"); // servsock = new
        * ServerSocket(self.getPortNumber()); // there shouldnt be a new // server each
        * time sock = servsock.accept(); // whenever this happens it should create a
        * new PeerConnection object and add // it to the connectionManager
        * System.out.println("Peer has connected! : " + sock);
        * 
        * } catch (Exception e) { // TODO: handle exception System.out.println(e); }
        * 
        * }
        */

        try {
            ServerSocket servsock = new ServerSocket(self.getPortNumber());

            while (running) {
                sock = servsock.accept();
                // System.out.println("Peer has connected! : " + sock);
                // Logger.write("Peer " + peerProcess.getPeerId() + " is connected from Peer " +
                // connectedPeer.getPeerId() + ".");

                PeerConnection peer = new PeerConnection(sock); // somehow needs to get into connectionManager once id
                                                                // is found
                Thread s = new Thread(() -> peer.run());
                //peer.sendChoke(); //idk if this can run threadsafe here
                s.start();
            }

        } catch (IOException e) {
            //Socket forced to close
        } catch (Exception e) {
            // TODO: handle excpetion
            e.printStackTrace();
        }
        
    }
}
