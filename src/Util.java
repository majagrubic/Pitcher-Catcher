
import java.nio.ByteBuffer;
import java.util.Random;

public class Util {

	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static Random rnd = new Random();

	public static String randomStringOfLength(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}

	public static long getRandomLong() {
		return (long) (rnd.nextDouble() * 1234567L);
	}

	public static int byteArrayToInt(byte[] b) {
		int value = 0;
		int offset = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i + offset] & 0x000000FF) << shift;
		}
		return value;
	}

	public static byte[] intToByteArray(int value) {
		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
				(byte) (value >>> 8), (byte) value };
	}

	public static long byteArrayToLong(byte[] buf) {
		ByteBuffer bb = ByteBuffer.wrap(buf);
		long l = bb.getLong();
		return l;
	}

	public static byte[] longToByteArray(long longNr) {
		byte[] bytes = ByteBuffer.allocate(8).putLong(longNr).array();
		return bytes;
	}
}
