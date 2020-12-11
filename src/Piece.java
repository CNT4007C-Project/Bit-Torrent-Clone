import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Piece {

    private int pieceSize;
    private boolean hasBytes;
    private String fileName = peerProcess.getFileName();
    private int pieceNum;

    public Piece(int i, boolean b) {
        pieceNum = i;
        hasBytes = b;
        pieceSize = Math.min(peerProcess.getPieceSize(), peerProcess.getFileSize() - i * peerProcess.getPieceSize());
    }

    public boolean getHasBytes(int i) {
        return hasBytes;
    }

    public void setBytes(byte[] bytes) {
        if (!(peerProcess.getPeerDictionary().get(peerProcess.getPeerId()).getHasFile())) {
            try {
                File directory = new File(
                        System.getProperty("user.dir") + "/Peer_" + peerProcess.getPeerId() + "/Pieces");
                if (!directory.exists()) {
                    directory.mkdir();
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + "/Peer_"
                        + peerProcess.getPeerId() + "/Pieces/" + fileName + "-Piece" + Integer.toString(pieceNum));
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bos.write(bytes, 0, pieceSize);
                bos.flush();
                bos.close();
                fos.close();
                hasBytes = true;
            } catch (Exception e) {
                // TODO: handle exception
            }
            hasBytes = true;
        } else {
            // System.out.println("Peer already has full file. No need to set bytes.");
        }
    }

    public byte[] getBytes() {
        byte[] temp = new byte[pieceSize];
        if (!(peerProcess.getPeerDictionary().get(peerProcess.getPeerId()).getHasFile())) { // Doesn't have file but has
                                                                                            // pieces
            if (hasBytes) {
                try {
                    FileInputStream fis = new FileInputStream(new File(System.getProperty("user.dir") + "/Peer_"
                            + peerProcess.getPeerId() + "/Pieces/" + fileName + "-Piece" + Integer.toString(pieceNum)));
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    bis.read(temp, 0, pieceSize);
                    bis.close();
                    fis.close();
                    return temp;
                } catch (Exception e) {
                    return null;
                    // TODO: handle exception
                }
            } else {
                return null;
            }
        } else { // has full file
            try {
                FileInputStream fis = new FileInputStream(
                        new File(System.getProperty("user.dir") + "/Peer_" + peerProcess.getPeerId() + "/" + fileName));
                BufferedInputStream bis = new BufferedInputStream(fis);
                bis.skip(pieceNum * pieceSize);
                bis.read(temp, 0, pieceSize);
                bis.close();
                fis.close();
                return temp;
            } catch (Exception e) {
                System.out.println(e);
                return null;
                // TODO: handle exception
            }
        }
    }

    public int getPieceSize() {
        return pieceSize;
    }
}
