import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/* 
This file handles file writing operations so that it does not need to be included
within other classes.

Uses DateTimeHandler to take care of placing [Time] in logs.
*/

public class Logging {
    //user working directory + / + log_peer_[peerID].log = ~/Project/log_peer_1001.log
    private static String directory = System.getProperty("user.dir") + "/"; 
    private static String fileName = null;
    private static FileWriter fileWriter = null;
    private static int peerIdRunning = peerProcess.getPeerId();

    private static String fileNamePeer(int peerId){
        return "log_peer_" + Integer.toString(peerId) + ".log"; 
    }

    private static String giveAbsDir(int peerId){
        return directory + fileNamePeer(peerId);
    }

    public static void write (int x){ //Writes integer to logs if given a peerId 
        try {
            fileWriter = new FileWriter(giveAbsDir(peerIdRunning));
            String fileContent = Integer.toString(x);
            String time = DateTimeHandler.getTime();
            
            fileContent = "[" + time + "]: " + fileContent;
            fileWriter.write(fileContent);
            fileWriter.close();
        } 
        catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void write (String x){ //Writes Strings to logs if given a peerId
        try { 
            fileWriter = new FileWriter(giveAbsDir(peerIdRunning));
            String fileContent = x;
            String time = DateTimeHandler.getTime();

            fileContent = "[" + time + "]: " + fileContent;
            fileWriter.write(x);
            fileWriter.close();
        } 
        catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void write (int peerId, int x){ //Writes integer to logs if given a peerId
        try {
            fileWriter = new FileWriter(giveAbsDir(peerId));
            String fileContent = Integer.toString(x);
            String time = DateTimeHandler.getTime();

            fileContent = "[" + time + "]: " + fileContent;
            fileWriter.write(fileContent);
            fileWriter.close();
        } 
        catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void write (int peerId, String x){ //Writes Strings to logs if given a peerId
        try { 
            fileWriter = new FileWriter(giveAbsDir(peerId));
            String fileContent = x;
            String time = DateTimeHandler.getTime();

            fileContent = "[" + time + "]: " + fileContent;
            fileWriter.write(x);
            fileWriter.close();
        } 
        catch (IOException e) {
            System.out.println(e);
        }
    }
}


/* Reference:

File Reading and Writing: https://stackabuse.com/reading-and-writing-files-in-java/

*/