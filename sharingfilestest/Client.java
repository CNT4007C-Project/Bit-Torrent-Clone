import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Client {

  public final static int SOCKET_PORT = 13267;      // you may change this
  public final static String SERVER = "127.0.0.1";  // localhost
  public final static String
	   FILE_TO_RECEIVED = "/Users/mustafamohamed/Desktop/3d5.jpg";  // you may change this, I give a
															// different name because i don't want to
															// overwrite the one used by server...

  public final static int FILE_SIZE = 104400; // file size temporary hard coded
											   // should bigger than the file to be downloaded

	public static void main (String [] args ) throws IOException {
	int bytesRead;
	int current = 0;
	FileOutputStream fos = null;
	BufferedOutputStream bos = null;
	Socket sock = null;
	try {
		sock = new Socket(SERVER, SOCKET_PORT);
		System.out.println("Connecting...");

		// receive file
		byte [] fullFile = new byte [104346];
		byte [] mybytearray  = new byte [32768];
		InputStream is = sock.getInputStream();
		fos = new FileOutputStream(FILE_TO_RECEIVED, true);
		bos = new BufferedOutputStream(fos);

		bytesRead = is.read(mybytearray,0,mybytearray.length);
		System.out.println(bytesRead);

		System.arraycopy(mybytearray, 0, fullFile, 32768*0, bytesRead);

		bytesRead = is.read(mybytearray,0,mybytearray.length);
		System.out.println(bytesRead);

		System.arraycopy(mybytearray, 0, fullFile, 32768*1, bytesRead);

		bytesRead = is.read(mybytearray,0,mybytearray.length);
		System.out.println(bytesRead);

		System.arraycopy(mybytearray, 0, fullFile, 32768*2, bytesRead);

		bytesRead = is.read(mybytearray,0,mybytearray.length);
		System.out.println(bytesRead);

		System.arraycopy(mybytearray, 0, fullFile, 32768*3, bytesRead);

		bos.write(fullFile, 0 , fullFile.length);
		bos.flush();
		
		// bytesRead = is.read(mybytearray,0,mybytearray.length);
		// System.out.println(bytesRead);

		// bos.write(mybytearray, 0 , bytesRead);
		// //bos.flush();

		// bytesRead = is.read(mybytearray,0,mybytearray.length);
		// System.out.println(bytesRead);

		// bos.write(mybytearray, 0 , bytesRead);
		// //bos.flush();

		// bytesRead = is.read(mybytearray,0,mybytearray.length);
		// System.out.println(bytesRead);

		System.out.println("File " + FILE_TO_RECEIVED
			+ " downloaded (" + bytesRead + " bytes read)");
	}
	finally {
		if (fos != null) fos.close();
		if (bos != null) bos.close();
		if (sock != null) sock.close();
	}
	}

}