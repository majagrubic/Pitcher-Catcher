

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * Receives TCP messages from Pitcher and sends response to them
 * Response is in format:
 *  ___________________________________________________________________________________
 * | 				|         |                        | 						|	  | 
 * |int messageSize | long id | long receivedTimestamp | long currentTimestamp	| msg |
 *
 */
public class Catcher extends Thread {

	private static final int LONG_SIZE = 8;
	Socket socket;
	private String ipAddress;
	private int port;
	
	public Catcher(CatcherParameters params){
		this.port = params.port;
		this.ipAddress = params.ipAddress;
	}
	
	public void run() {
		System.out.println("*Catcher started*");
		try {
			InetAddress address = InetAddress.getByName(ipAddress);
			ServerSocket serverSocket = new ServerSocket(port, 50, address);
			while (true) {
				socket = serverSocket.accept();
				new ReceiverThread(socket).start();
			}		
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try { socket.close(); } catch (IOException ignorable){}
		}
	}
	
	private class ReceiverThread extends Thread {
		
		private Socket socket;
		DataOutputStream out;
		DataInputStream dis;
		
		public ReceiverThread(Socket s) {
			this.socket = s;
		}
		
		@Override
		public void run() {
			this.out = null;
			this.dis = null;
			try {
				synchronized (socket) {
					this.dis = new DataInputStream(socket.getInputStream());
					byte[] buff = new byte[4];
					dis.read(buff);
					int n = Util.byteArrayToInt(buff);
					byte[] msg = new byte[n];
					dis.read(msg);

					byte[] output = process(msg);
					out = new DataOutputStream(socket.getOutputStream());
					out.write(buff);
					out.write(output);
					out.flush();
					socket.notify();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try{ socket.close();} catch(Exception ignorable){}
			}
		
		}
		
		private byte[] process(byte[]msg) {
			int n = msg.length;
			byte[] output = new byte[n];
		
			int offset = 0;
			//read in id
			byte[] id = Arrays.copyOfRange(msg, offset, offset+LONG_SIZE);
			offset += LONG_SIZE;
			
			//read in timestamp
			byte[] timestamp =  Arrays.copyOfRange(msg, offset, offset+LONG_SIZE);
		
			//rest of the message disregarded
			
			//create output message
			offset = 0;
			for (int i=0; i<LONG_SIZE; i++) {
				output[i+offset] = id[i];
			}
			offset+=LONG_SIZE;
			for (int i=0; i<LONG_SIZE; i++) {
				output[i+offset] = timestamp[i];
			}
			offset+=LONG_SIZE;
			
			long currentTimestamp = System.currentTimeMillis();
			byte[] currentTmsp = Util.longToByteArray(currentTimestamp);
			for (int i=0; i<currentTmsp.length; i++) {
				output[i+offset] = currentTmsp[i];
			}

			//if neccessary, fill in the rest of the message
			//return output
			return output;
		}
	}

}
