import java.nio.charset.StandardCharsets;
import java.util.Arrays;

// a class for handling recieved messages

public class MessageHandler {

    public static void readMessage(byte[] message) {

        String header = new String(Arrays.copyOfRange(message, 0, 18), StandardCharsets.UTF_8);

        if (header == "P2PFILESHARINGPROJ") {

            // handshake message
            byte[] rawId = Arrays.copyOfRange(message, 28, 32);
            int peerId = java.nio.ByteBuffer.wrap(rawId).getInt();

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
