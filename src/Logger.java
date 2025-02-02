import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/* 
This file handles file writing operations so that it does not need to be included
within other classes.

Uses DateTimeHandler to take care of placing [Time] in logs.
*/

public class Logger {
    // user working directory + / + log_peer_[peerID].log =
    // ~/Project/log_peer_1001.log
    private static String directory = System.getProperty("user.dir") + "/";
    // private static FileWriter fileWriter = null;
    private static int peerIdRunning = peerProcess.getPeerId();

    private static String fileNamePeer(int peerId) {
        try {
            File peerFolder = new File("Peer_" + peerProcess.getPeerId());
            if(!peerFolder.exists()){
                peerFolder.mkdir();
            }
            File logFolder = new File("Peer_" + peerProcess.getPeerId() + "/Logs");
            if(!logFolder.exists()){
                logFolder.mkdir();
            }
        } catch (Exception e) {
            //TODO: handle exception
        }
        return "Peer_" + peerProcess.getPeerId() + "/Logs/log_peer_" + Integer.toString(peerId) + ".log";
    }

    private static String giveAbsDir(int peerId) {
        return directory + fileNamePeer(peerId);
    }

    public static void write(int x) { // Writes integer to logs if given a peerId
        try {
            FileWriter fileWriter = new FileWriter(giveAbsDir(peerIdRunning), true);
            String fileContent = Integer.toString(x);
            String time = DateTimeHandler.getTime();

            fileContent = "[" + time + "]: " + fileContent;
            fileWriter.write(fileContent);
            fileWriter.write('\n');
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void write(String x) { // Writes Strings to logs if given a peerId
        try {
            FileWriter fileWriter = new FileWriter(giveAbsDir(peerIdRunning), true);
            String fileContent = x;
            String time = DateTimeHandler.getTime();

            fileContent = "[" + time + "]: " + fileContent;
            fileWriter.write(fileContent);
            fileWriter.write('\n');
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(int peerId, int x) { // Writes integer to logs if given a peerId
        try {
            FileWriter fileWriter = new FileWriter(giveAbsDir(peerId), true);
            String fileContent = Integer.toString(x);
            String time = DateTimeHandler.getTime();

            fileContent = "[" + time + "]: " + fileContent;
            fileWriter.write(fileContent);
            fileWriter.write('\n');
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void write(int peerId, String x) { // Writes Strings to logs if given a peerId
        try {
            FileWriter fileWriter = new FileWriter(giveAbsDir(peerId), true);
            String fileContent = x;
            String time = DateTimeHandler.getTime();

            fileContent = "[" + time + "]: " + fileContent;
            fileWriter.write(fileContent);
            fileWriter.write('\n');
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}

/*
 * Reference:
 * 
 * File Reading and Writing:
 * https://stackabuse.com/reading-and-writing-files-in-java/
 * 
 */