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
import java.util.Random;
import java.util.zip.InflaterOutputStream;
import java.util.Vector;
import java.util.ArrayList;

import javax.management.ServiceNotFoundException;

import java.nio.*;

public class PeerConnection implements Runnable {

    private Socket connectionSocket;
    private int connectedPeerId;
    private Peer connectedPeer;
    BufferedInputStream inputStream;
    BufferedOutputStream outputStream;
    private boolean handshakeReceived = false;
    private boolean isChoked = true;
    private boolean isInterested = false;
    private volatile boolean running;
    private int piecesReceived = 0;

    public PeerConnection(int id, Socket s) { // for requested connections where destination is known
        connectedPeerId = id;
        connectedPeer = peerProcess.getPeerDictionary().get(id);
        connectionSocket = s;
        running = true;
    }

    public PeerConnection(Socket s) { // for listened connections when peer ID is unknown
        connectedPeerId = 0;
        connectedPeer = null;
        connectionSocket = s;
        running = true;
    }

    public void terminate() {
        // System.out.println("Terminating thread to peer " + connectedPeerId);
        running = false;
    }

    @Override
    public void run() {
        // this part isnt necessary anymore since the socket would already exist from
        // outside
        /*
         * try {
         * 
         * connectionSocket = new Socket(connectedPeer.getHostName(),
         * connectedPeer.getPortNumber()); System.err.println( "Connected to peer " +
         * connectedPeerId + " at port " + connectedPeer.getPortNumber() + "!");
         * 
         * } catch (UnknownHostException e) {
         * System.err.println("The host listed for peer " + connectedPeerId +
         * " is not recognized"); e.printStackTrace(); } catch (IOException e) { // TODO
         * Auto-generated catch block e.printStackTrace(); }
         */

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
            // listen is on while loop until this thread is called to terminate
        }

    }

    public synchronized void sendHandshake() {
        String handshakeHeader = "P2PFILESHARINGPROJ";
        byte[] handshakeMessage = new byte[32];
        System.out.println("SENDING HANDSHAKE");
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

        ByteBuffer idBuf = ByteBuffer.allocate(4);
        idBuf.putInt(peerProcess.getPeerId());
        byte[] id = idBuf.array();

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
            handshakeHeader = new String((Arrays.copyOfRange(incomingHandshake, 0, 18)), "UTF-8");

            if (handshakeHeader.equals("P2PFILESHARINGPROJ")) {
                // TODO some logic to see if peerID is the "expected" peer as per the spec
                byte[] id = (Arrays.copyOfRange(incomingHandshake, 28, 32));
                ByteBuffer wrapper = ByteBuffer.wrap(id);
                int temp = wrapper.getInt();

                if (connectedPeerId == 0) {
                    connectedPeerId = temp;
                    Logger.write(
                            "Peer " + peerProcess.getPeerId() + " is connected from Peer " + connectedPeerId + ".");
                } else if (connectedPeerId != temp) {
                    System.out.println("unexpected peer id");
                }

                connectedPeer = peerProcess.getPeerDictionary().get(connectedPeerId);

                peerProcess.getConnectionManager().put(connectedPeerId, this);

                handshakeReceived = true; // does there need to be some sort of ack as well? Or does that happen
                                          // automatically?
                System.out.println("Handshake received from " + connectedPeerId);
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void sendChoke() {
        byte[] chokeMessage = new byte[5];

        ByteBuffer lengthBuf = ByteBuffer.allocate(4);
        lengthBuf.putInt(0);
        byte[] messageLength = lengthBuf.array();

        System.arraycopy(messageLength, 0, chokeMessage, 0, 4);

        ByteBuffer typeBuf = ByteBuffer.allocate(1);
        typeBuf.put((byte) 0);
        byte[] messageType = typeBuf.array();

        System.arraycopy(messageType, 0, chokeMessage, 4, 1);

        try {
            outputStream.write(chokeMessage);
            outputStream.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void sendOptimUnchoke() {
        Logger.write("Peer " + peerProcess.getPeerId() + " has the optimistically unchoked neighbor " + connectedPeerId
                + ".");
        sendUnchoke();
    }

    public void sendUnchoke() {
        byte[] unchokeMessage = new byte[5];

        ByteBuffer lengthBuf = ByteBuffer.allocate(4);
        lengthBuf.putInt(0);
        byte[] messageLength = lengthBuf.array();

        System.arraycopy(messageLength, 0, unchokeMessage, 0, 4);

        ByteBuffer typeBuf = ByteBuffer.allocate(1);
        typeBuf.put((byte) 1);
        byte[] messageType = typeBuf.array();

        System.arraycopy(messageType, 0, unchokeMessage, 4, 1);

        try {
            outputStream.write(unchokeMessage);
            outputStream.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendInterested() {
        byte[] interestedMessage = new byte[5];

        ByteBuffer lengthBuf = ByteBuffer.allocate(4);
        lengthBuf.putInt(0);
        byte[] messageLength = lengthBuf.array();

        System.arraycopy(messageLength, 0, interestedMessage, 0, 4);

        ByteBuffer typeBuf = ByteBuffer.allocate(1);
        typeBuf.put((byte) 2);
        byte[] messageType = typeBuf.array();

        System.arraycopy(messageType, 0, interestedMessage, 4, 1);

        try {
            outputStream.write(interestedMessage);
            outputStream.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendUninterested() {
        byte[] uninterestedMessage = new byte[5];

        ByteBuffer lengthBuf = ByteBuffer.allocate(4);
        lengthBuf.putInt(0);
        byte[] messageLength = lengthBuf.array();

        System.arraycopy(messageLength, 0, uninterestedMessage, 0, 4);

        ByteBuffer typeBuf = ByteBuffer.allocate(1);
        typeBuf.put((byte) 3);
        byte[] messageType = typeBuf.array();

        System.arraycopy(messageType, 0, uninterestedMessage, 4, 1);

        try {
            outputStream.write(uninterestedMessage);
            outputStream.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendRequest(byte[] pieceIndex) {
        byte[] requestMessage = new byte[9];

        ByteBuffer lengthBuf = ByteBuffer.allocate(4);
        lengthBuf.putInt(4);
        byte[] messageLength = lengthBuf.array();

        System.arraycopy(messageLength, 0, requestMessage, 0, 4);

        ByteBuffer typeBuf = ByteBuffer.allocate(1);
        typeBuf.put((byte) 6);
        byte[] messageType = typeBuf.array();

        System.arraycopy(messageType, 0, requestMessage, 4, 1);
        System.arraycopy(pieceIndex, 0, requestMessage, 5, 4);

        // System.out.println("sending request to " + connectedPeerId);

        try {
            outputStream.write(requestMessage);
            outputStream.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendHave(byte[] pieceIndex) {
        byte[] haveMessage = new byte[9];

        ByteBuffer lengthBuf = ByteBuffer.allocate(4);
        lengthBuf.putInt(4);
        byte[] messageLength = lengthBuf.array();

        System.arraycopy(messageLength, 0, haveMessage, 0, 4);

        ByteBuffer typeBuf = ByteBuffer.allocate(1);
        typeBuf.put((byte) 4);
        byte[] messageType = typeBuf.array();

        System.arraycopy(messageType, 0, haveMessage, 4, 1);
        System.arraycopy(pieceIndex, 0, haveMessage, 5, 4);

        try {
            outputStream.write(haveMessage);
            outputStream.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void handleBitfield(byte[] peerBitfield) {
        if (BitfieldUtility.hasNeededPiece(peerProcess.getBitfield(), peerBitfield)) {
            sendInterested();
        } else {
            sendUninterested();
        }
        // updates bitfield
        connectedPeer.setBitfield(peerBitfield);
    }

    public void handlePieceIndex(byte[] p) {
        int pieceIndex = ByteBuffer.wrap(p).getInt();
        if (!BitfieldUtility.getBit(peerProcess.getBitfield(), pieceIndex)) { // is this piece one we are missing?
            sendInterested();
        } else {
            sendUninterested();
        }
        // updates bitfield
        connectedPeer.updateBitfield(p);

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

    public byte[] pickPieceIndex() {
        // need to determine piece to ask for
        byte[] unique = BitfieldUtility.xor(peerProcess.getBitfield(), connectedPeer.getBitfield());

        // show which pieces ONLY the peer has
        byte[] uniqueToPeer = BitfieldUtility.and(unique, connectedPeer.getBitfield());
        // System.out.print("uniquePeer: ");
        // BitfieldUtility.printBitfield(uniqueToPeer);

        Vector<Integer> neededPieces = new Vector<Integer>();

        // populates the vector with the indices of pieces only peer has
        for (int i = 0; i < uniqueToPeer.length * 8; i++) {
            if (BitfieldUtility.getBit(uniqueToPeer, i)) {
                neededPieces.add(i); // adds index for needed piece
            }
        }

        Random random = new Random();
        // System.out.println("needed pieces: " + neededPieces.size());
        if (neededPieces.size() != 0) {
            int index = random.nextInt(neededPieces.size());
            int pieceIndexInt = neededPieces.get(index); // choose a random piece to request

            ByteBuffer indexBuf = ByteBuffer.allocate(4);
            indexBuf.putInt(pieceIndexInt);
            byte[] pIndex = indexBuf.array();
            return pIndex;
        } else {
            return null;
        }

    }

    public void sendPiece(byte[] pieceIndex) {
        byte[] filePiece = peerProcess.getFileManager().fileToPieces(ByteBuffer.wrap(pieceIndex).getInt());
        byte[] pieceMessage = new byte[4 + 1 + 4 + filePiece.length];

        ByteBuffer lengthBuf = ByteBuffer.allocate(4);
        lengthBuf.putInt(4 + filePiece.length);
        byte[] messageLength = lengthBuf.array();

        System.arraycopy(messageLength, 0, pieceMessage, 0, 4);

        ByteBuffer typeBuf = ByteBuffer.allocate(1);
        typeBuf.put((byte) 7);
        byte[] messageType = typeBuf.array();

        System.arraycopy(messageType, 0, pieceMessage, 4, 1);
        System.arraycopy(pieceIndex, 0, pieceMessage, 5, 4);

        // get the file piece
        // System.out.println("LENGTH: " + filePiece.length);
        System.arraycopy(filePiece, 0, pieceMessage, 9, filePiece.length);

        try {
            outputStream.write(pieceMessage);
            outputStream.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void downloadPiece(int index, byte[] piece) {
        // download the piece
        peerProcess.getFileManager().piecesToFile(index, piece);
    }

    public void listenForMessages() {
        while (running) { // once again, this should probably be a thread
            processMessage();
        }
    }

    public int piecesReceived() {
        return piecesReceived;
    }

    public boolean isInterested() {
        return isInterested;
    }

    public void updatePreferred(String preferred) {
        Logger.write("Peer " + peerProcess.getPeerId() + " has the preferred neighbors " + preferred + ".");
    }

    public synchronized void processMessage() { // TODO confirm what "synchronized does"

        byte[] messageLength = new byte[4];
        byte[] messageType = new byte[1];

        try {
            inputStream.read(messageLength); // TODO confirm if length of bytes needs to be specified
            inputStream.read(messageType);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Connection closed by peer");
            // e.printStackTrace();
        }

        int type = messageType[0];
        int length = ByteBuffer.wrap(messageLength).getInt();

        switch (type) {
            case 0: // choke
                Logger.write("Peer " + peerProcess.getPeerId() + " is choked by " + connectedPeerId + ".");
                break;
            case 1: // unchoke
                piecesReceived = 0;
                Logger.write("Peer " + peerProcess.getPeerId() + " is unchoked by " + connectedPeerId + ".");
                if (!peerProcess.getPeerDictionary().get(peerProcess.getPeerId()).getHasFile()) {
                    System.out.println("Got unchoked, sending request");
                    byte[] piece = pickPieceIndex();
                    if (piece != null) {
                        sendRequest(piece);
                    } else {
                        sendUninterested();
                    }
                }
                break;
            case 2: // interested
                isInterested = true;
                connectedPeer.becomeInterested();
                Logger.write("Peer " + peerProcess.getPeerId() + " received the ‘interested’ message from "
                        + connectedPeerId + ".");
                break;
            case 3: // not interested
                isInterested = false;
                connectedPeer.becomeUninterested();
                Logger.write("Peer " + peerProcess.getPeerId() + " received the ‘not interested’ message from "
                        + connectedPeerId + ".");
                break;
            case 4: // have
                try {
                    byte[] pieceIndex = new byte[4];
                    inputStream.read(pieceIndex);
                    handlePieceIndex(pieceIndex);
                    Logger.write("Peer " + peerProcess.getPeerId() + " received the ‘have’ message from "
                            + connectedPeerId + " for the piece " + ByteBuffer.wrap(pieceIndex).getInt() + ".");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case 5: // bitfield
                try {
                    byte[] payload = new byte[length];
                    inputStream.read(payload);
                    handleBitfield(payload);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case 6: // request
                try {
                    byte[] pieceIndex = new byte[4];
                    inputStream.read(pieceIndex);

                    // TODO: send piece
                    sendPiece(pieceIndex);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case 7: // piece
                try {
                    byte[] pieceIndex = new byte[4];
                    inputStream.read(pieceIndex);
                    int index = ByteBuffer.wrap(pieceIndex).getInt();

                    // first download piece
                    int pieceSize = ByteBuffer.wrap(messageLength).getInt() - 4;
                    byte[] filePiece = new byte[pieceSize];
                    inputStream.read(filePiece);
                    downloadPiece(index, filePiece);
                    // updating THIS bitfield
                    BitfieldUtility.setBit(peerProcess.getBitfield(), index, true);

                    // count how many pieces
                    int numPieces = 0;
                    for (int i = 0; i < peerProcess.getBitfield().length * 8; i++) {
                        if (BitfieldUtility.getBit(peerProcess.getBitfield(), i)) {
                            numPieces++;
                        }
                    }

                    Logger.write("Peer " + peerProcess.getPeerId() + " has downloaded the piece " + index + " from "
                            + connectedPeerId + ". Now the number of pieces it has is " + numPieces + ".");

                    // check neighbors to see if it should still interested

                    peerProcess.getConnectionManager().forEach((id, peer) -> {
                        if (!BitfieldUtility.hasNeededPiece(peerProcess.getBitfield(), connectedPeer.getBitfield())) {
                            // if a neighbor is no longer interesting, tell them so on that connection
                            peer.sendUninterested();
                        }
                        peer.sendHave(pieceIndex);
                    });

                    if (peerProcess.getFileManager().hasAllPieces()) {
                        // has whole file
                        peerProcess.getPeerDictionary().get(peerProcess.getPeerId()).setHasFile(1);
                    } else {
                        // after download, request another
                        byte[] p = pickPieceIndex();
                        // System.out.println(
                        // "Got piece " + index + ", sending request for piece " +
                        // ByteBuffer.wrap(p).getInt());
                        if (p != null) {
                            sendRequest(p);
                        } else {
                            sendUninterested();
                        }
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            default:
                Logger.write("MESSAGE TYPE ERROR");
                System.err.println("Message type error!");
                System.out.println("REC TYPE: " + type);

        }

    }

}
