// stores data for each peer learned from PeerInfo.cfg

public class Peer {
    private int peerId;
    private String hostName;
    private int portNumber;
    private int hasFile;
    private byte[] bitfield;

    public Peer(int _peerId, String _hostName, int _portNumber, int _hasFile) {
        peerId = _peerId;
        hostName = _hostName;
        portNumber = _portNumber;
        hasFile = _hasFile;
        bitfield = new byte[(int) Math.ceil((double) peerProcess.getPieces() / 8.0)];

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

    public void setBitfield(byte[] b) {
        bitfield = b;
    }
}