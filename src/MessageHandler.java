import java.nio.charset.StandardCharsets;
import java.util.Arrays;

// a class for handling recieved messages

public class MessageHandler {

    public static void readMessage(byte[] message, int srcId) { // srcId represent the peer id of the source of the
                                                                // message.

        String header = new String(Arrays.copyOfRange(message, 0, 18), StandardCharsets.UTF_8);

        if (header == "P2PFILESHARINGPROJ") {

            // handshake message
            byte[] rawId = Arrays.copyOfRange(message, 28, 32);
            int peerId = java.nio.ByteBuffer.wrap(rawId).getInt();
            // TODO check if peerId is expected

        } else {

            int length = java.nio.ByteBuffer.wrap(Arrays.copyOfRange(message, 0, 4)).getInt();

            int messageType = Byte.toUnsignedInt(message[4]);

            switch (messageType) {

                case 0: // choke

                    break;
                case 1: // unchoke
                    break;
                case 2: // interested
                    break;
                case 3: // not interested
                    break;
                case 4: // have
                    break;
                case 5: // bitfield

                    // compare peer bitfield to this bitfield
                    byte[] bitfield = peerProcess.getBitfield();
                    byte[] peerBitfield = Arrays.copyOfRange(message, 5, 5 + bitfield.length + 1); // I think this
                                                                                                   // works?

                    if (BitfieldUtility.hasNeededPiece(bitfield, peerBitfield)) {
                        // TODO send "interested" message
                    } else {
                        // TODO send "not interested" message
                    }

                    // updates peer bitfield with the recent one from the message
                    peerProcess.getPeerDictionary().get(srcId).setBitfield(peerBitfield);

                    break;
                case 6: // request
                    break;
                case 7: // piece
                    break;
                default:

            }

        }

    }
}
