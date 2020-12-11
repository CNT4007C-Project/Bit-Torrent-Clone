import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

  public final static int SOCKET_PORT = 13267;  // you may change this
  public final static String FILE_TO_SEND = "/Users/mustafamohamed/Github/Bit-Torrent-Clone/3d5.jpg";  // you may change this

  public static void main (String [] args ) throws IOException {
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    OutputStream os = null;
    ServerSocket servsock = null;
    Socket sock = null;
    try {
      servsock = new ServerSocket(SOCKET_PORT);
      while (true) {
        System.out.println("Waiting...");
        try {
          sock = servsock.accept();
          System.out.println("Accepted connection : " + sock);
          // send file
          File myFile = new File (FILE_TO_SEND);
          byte [] mybytearray  = new byte [104346];
          fis = new FileInputStream(myFile);
          bis = new BufferedInputStream(fis);
          bis.read(mybytearray,0,mybytearray.length);
          os = sock.getOutputStream();
          System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
		  
		  os.write(mybytearray,0*32768,Math.min(32768,mybytearray.length-0*32768));
		  os.flush();

		  os.write(mybytearray, 1*32768, Math.min(32768, mybytearray.length-1*32768));
		  os.flush();

		  os.write(mybytearray, 2*32768, Math.min(32768, mybytearray.length-2*32768));
		  os.flush();

		  os.write(mybytearray, 3*32768, Math.min(32768, mybytearray.length-3*32768));
		  os.flush();
		//   for(int i = 1; i < 300; i++){
		// 	os.write(mybytearray,i,1);
        //   	os.flush();
		//   }
          System.out.println("Done.");
        }
        finally {
          if (bis != null) bis.close();
          if (os != null) os.close();
          if (sock!=null) sock.close();
        }
      }
    }
    finally {
      if (servsock != null) servsock.close();
    }
  }
}
