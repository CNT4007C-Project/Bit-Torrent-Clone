import java.nio.ByteBuffer;

// stores data for each peer learned from PeerInfo.cfg

public class Peer {
    private int peerId;
    private String hostName;
    private int portNumber;
    private int hasFile;
    private byte[] bitfield;
    private boolean interested;
    private boolean choked;
    private long previousChoke;
    private long previousOptimumChoke;

    public Peer(int _peerId, String _hostName, int _portNumber, int _hasFile) {
        peerId = _peerId;
        hostName = _hostName;
        portNumber = _portNumber;
        hasFile = _hasFile;
        bitfield = new byte[(int) Math.ceil((double) peerProcess.getPieces() / 8.0)];
        interested = false;
        choked = false;
        previousChoke = System.currentTimeMillis();
        previousOptimumChoke = System.currentTimeMillis();

        if (_hasFile == 1) { // populate bitfield with 1's
            for (int i = 0; i < peerProcess.getPieces(); i++) {
                BitfieldUtility.setBit(bitfield, i, true);
            }
        } else {
            // populate bitfield with 0's
        }
    }

    public int getPeerId() {
        return peerId;
    }

    public String getHostName() {
        return hostName;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public boolean getHasFile() {
        return (hasFile == 1);
    }

    public byte[] getBitfield() {
        return bitfield;
    }

    public boolean isInterested() {
        return interested;
    }

    public void becomeInterested() {
        interested = true;
    }

    public void becomeUninterested() {
        interested = false;
    }

    public boolean isChoked() {
        return choked;
    }

    public void choke() {
        choked = true;
    }

    public void unchoke() {
        choked = false;
    }

    public long chokeTime() {
        return previousChoke;
    }

    public long optimumChokeTime() {
        return previousOptimumChoke;
    }

    public void updateBitfield(byte[] pieceIndex) {
        int index = ByteBuffer.wrap(pieceIndex).getInt();
        BitfieldUtility.setBit(bitfield, index, true);
        fileStatusCheck();
    }

    public void setBitfield(byte[] b) {
        bitfield = b;
        fileStatusCheck();
    }

    private void fileStatusCheck() {
        // To do: fix this
        boolean complete = true;
        for (int i = 0; i < bitfield.length; i++) {
            if (i == bitfield.length-1) {
                if (Byte.valueOf(bitfield[i]) > 0)
            } else if (Byte.valueOf(bitfield[i]) == Byte.MAX_VALUE) {
                complete = false;
                break;
            }
        }
        if (complete) {
            hasFile = 1;
        }

    }

}