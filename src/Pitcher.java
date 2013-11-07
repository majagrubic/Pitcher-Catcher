import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * Sends messages to Catcher and displays network statistics.
 * 
 * Sent message is in format:
 *  ___________________________________________________________________________________
 * | 				|         |                        | 							  | 
 * |int messageSize | long id | long currentTimestamp  | msg (random array of bytes)  |
 *
 */
public class Pitcher extends Thread {

	private static final int LONG_SIZE = 8;
	private List<Long> sentIds;
	private int port;
	private int rate;
	private int msgSize;
	private String hostName;

	// total nr of sent messages
	private long nrOfSentMessages = 0;
	//statistics tracking
	private TimeStatistics statistics;

	public Pitcher(PitcherParameters params) {
		this.port = params.port;
		this.rate = params.rate;
		this.msgSize = params.size;
		this.hostName = params.getHostName();
		this.sentIds = Collections.synchronizedList(new ArrayList<Long>());
		this.statistics = TimeStatistics.getInstance();
	}

	public void run() {
		System.out.println("*Pitcher started*");
		//send messages each 1/rate seconds
		new Timer().scheduleAtFixedRate(new SendMessageTask(hostName, port), 2000, 1000 / rate);
		//display text each second
		new Timer().scheduleAtFixedRate(new DisplayTask(), 1000, 1000);
	}

	private class SendMessageTask extends TimerTask {

		private Socket socket;
		private String address;
		private int port;

		public SendMessageTask(String inetAddress, int port) {
			this.address = inetAddress;
			this.port = port;
		}

		@Override
		public void run() {
			try {
				socket = new Socket(InetAddress.getByName(address), port);
				DataOutputStream out = new DataOutputStream(
						socket.getOutputStream());
				byte[] msg = null;
				synchronized (socket) {
				// send message size
				out.write(Util.intToByteArray(msgSize));

				// send message id
				long id = Util.getRandomLong();
				byte[] bId = Util.longToByteArray(id);
				out.write(bId);

				// send timestamp
				long timeStamp = System.currentTimeMillis();
				byte[] bTimeStamp = Util.longToByteArray(timeStamp);
				out.write(bTimeStamp);

				// send some random string
				byte[] rest = new byte[msgSize - 2 * LONG_SIZE];
				out.write(rest);
				out.flush();

				// add id to list of sent ids for tracking
				sentIds.add(id);
				// increase total number of sent messages
				increaseNrOfSentMessages();

				DataInputStream dis = new DataInputStream(
						socket.getInputStream());
				byte[] buff = new byte[4];
				dis.read(buff);
				int n = Util.byteArrayToInt(buff);
				msg = new byte[n];
				dis.read(msg);
				socket.notify();
				}
				process(msg);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
		}
	}

	private synchronized void increaseNrOfSentMessages() {
		nrOfSentMessages++;
	}

	private synchronized long getNrOfSentMessages() {
		return nrOfSentMessages;
	}

	private void process(byte[] msg) throws IOException {
		int offset = 0;
		// read in id
		long id = Util.byteArrayToLong(Arrays.copyOfRange(msg, offset, offset + LONG_SIZE));
		offset += LONG_SIZE;

		if (sentIds.contains(id)) {
			// everything ok; remove id from the list of sentIds
			sentIds.remove(id);
		}
		if (sentIds.size() > 0)
			System.out.println("Lost packets: " + sentIds.toString());

		// read in original timestamp (timestamp A)
		long timestampA = Util.byteArrayToLong(Arrays.copyOfRange(msg, offset, offset + LONG_SIZE));
		offset += LONG_SIZE;

		// read in Catcher's timestamp (timestamp B)
		long timestampB = Util.byteArrayToLong(Arrays.copyOfRange(msg, offset, offset + LONG_SIZE));
		offset += LONG_SIZE;

		// update statistics
		synchronized (statistics) {
			statistics.updateStatistics(timestampA, timestampB,
					System.currentTimeMillis());
			statistics.notify();
		}

		// rest of the message disregard
	}

	private class DisplayTask extends TimerTask {

		@Override
		public void run() {
			StringBuffer sb = new StringBuffer();
		    DecimalFormat df = new DecimalFormat("#.##");
			synchronized (statistics) {
				String newLine = System.getProperty("line.separator");
				sb.append("------------------").append(newLine);
				// 1. date
				DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
				Date date = new Date();
				sb.append(dateFormat.format(date)).append(" | ");
				// 2. total nr of sent messages
				sb.append(getNrOfSentMessages()).append(" | ");
				// 3. nr of messages in previous second
				sb.append(rate).append(newLine);
				// 4. average time of A->B->A cycle
				sb.append(df.format((double)statistics.getCurrentCycleTime() / rate)).append(" | ");
				// 5. total time of A->B->A cycle
				sb.append(statistics.getTotalCycleTime()).append(" | ");
				// 6. average time of A->B cycle
				sb.append(df.format((double)statistics.getCurrentABTime() / rate)).append(" | ");
				// 7. average time of B->A cycle
				sb.append(df.format((double)statistics.getCurrentBATime() / rate)).append(" | ");
				System.out.println(sb.toString());

				statistics.resetTimes();
				statistics.notify();
			}
		}
	}
}
