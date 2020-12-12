import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class FileManager {

    private static String fileName = peerProcess.getFileName();
    private static int numPieces = peerProcess.getPieces();
    private static ArrayList<Piece> piecesList;

    public FileManager() {
        piecesList = new ArrayList<Piece>();

        try {
            File peerFolder = new File("Peer_" + peerProcess.getPeerId());
            if (!peerFolder.exists()) {
                peerFolder.mkdir();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        if (peerProcess.getPeerDictionary().get(peerProcess.getPeerId()).getHasFile()) { // Init if have the file
            for (int i = 0; i < numPieces; i++) {
                piecesList.add(new Piece(i, true));

            }
        } else { // Init if does not have file
            for (int i = 0; i < numPieces; i++) {
                piecesList.add(new Piece(i, false));
            }
        }
    }

    public byte[] fileToPieces(int i) {
        return piecesList.get(i).getBytes();
    }

    public void piecesToFile(int i, byte[] piece) { // i is indexed at 0
        piecesList.get(i).setBytes(piece);
    }

    public boolean hasAllPieces() {
        for (int i = 0; i < numPieces; i++) {
            if (piecesList.get(i).getHasBytes(i) == false) {
                return false;
            }
        }
        File fileCheck = new File(System.getProperty("user.dir") + "/Peer_" + peerProcess.getPeerId() + "/" + fileName);
        if (!fileCheck.exists()) {
            try {
                for (int i = 0; i < numPieces; i++) {
                    byte[] temp = new byte[piecesList.get(i).getPieceSize()];
                    FileInputStream fis = new FileInputStream(new File(System.getProperty("user.dir") + "/Peer_"
                            + peerProcess.getPeerId() + "/Pieces/" + fileName + "-Piece" + Integer.toString(i)));
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    bis.read(temp, 0, temp.length);
                    bis.close();
                    fis.close();

                    FileOutputStream fos = new FileOutputStream(
                            System.getProperty("user.dir") + "/Peer_" + peerProcess.getPeerId() + "/" + fileName, true);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    bos.write(temp, 0, temp.length);
                    bos.flush();
                    bos.close();
                    fos.close();
                }

                File deletePieces = new File(
                        System.getProperty("user.dir") + "/Peer_" + peerProcess.getPeerId() + "/Pieces");
                if (deletePieces.exists()) {
                    String[] entries = deletePieces.list();
                    for (String s : entries) {
                        File currentFile = new File(deletePieces.getPath(), s);
                        currentFile.delete();
                    }
                    deletePieces.delete();
                }
                Logger.write(
                        "Peer " + Integer.toString(peerProcess.getPeerId()) + " has downloaded the complete file.");

                peerProcess.getPeerDictionary().get(peerProcess.getPeerId()).setHasFile(1);

                /*
                 * for (HashMap.Entry<Integer, Peer> entry :
                 * peerProcess.getPeerDictionary().entrySet()) { if
                 * (entry.getValue().getHasFile()) { System.out.println(entry.getKey() +
                 * " has file"); } else { System.out.println(entry.getKey() +
                 * " has bitfield: ");
                 * BitfieldUtility.printBitfield(entry.getValue().getBitfield()); } }
                 */

            } catch (Exception e) {
                return true;
                // TODO: handle exception
            }
        }
        return true;
    }

}
