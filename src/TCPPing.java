import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.beust.jcommander.JCommander;


public class TCPPing {

	private static final String IPADDRESS_PATTERN = 
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	
	private static final int MIN_MESSAGE_SIZE = 50;
	private static final int MAX_MESSAGE_SIZE = 3000;
 
	private enum Mode {
		PITCHER("-p"),
		CATCHER("-c"),
		UNSUPPORTED("");

		private final String strParam;
		Mode(String strParam) {
			this.strParam = strParam;
		}
		static Mode fromString(String s){
			if (PITCHER.strParam.equalsIgnoreCase(s)) {
				return PITCHER;
			}
			if (CATCHER.strParam.equalsIgnoreCase(s)) {
				return CATCHER;
			}
			return UNSUPPORTED;
		}
	}
	
	private static void validateCatcherParams(CatcherParameters params) {
		Pattern p = Pattern.compile(IPADDRESS_PATTERN);
		Matcher matcher = p.matcher(params.ipAddress);
		if (!matcher.matches()) throw new IllegalArgumentException("Invalid IP address");
		if (params.port<0 || params.port > 65535) throw new IllegalArgumentException("Invalid port");
	}
	
	private static void validatePitcherParams(PitcherParameters params) {
		if (params.port<0 || params.port > 65535) throw new IllegalArgumentException("Invalid port");
		if (params.size<MIN_MESSAGE_SIZE || params.size>MAX_MESSAGE_SIZE) 
			throw new IllegalArgumentException("Invalid message size");
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			throw new IllegalArgumentException("At least one parameter expected");
		}
		Mode mode = Mode.fromString(args[0]);
		if (mode == Mode.UNSUPPORTED) {
			throw new IllegalArgumentException("Unsupported mode");
		}
		//remove mode from args
		args = Arrays.copyOfRange(args, 1, args.length);
		if (mode == Mode.CATCHER) {
			CatcherParameters params = CatcherParameters.getInstance();
			new JCommander(params, args); 
			validateCatcherParams(params);
			new Catcher(params).start();
		} else if (mode == Mode.PITCHER) {
			PitcherParameters params = PitcherParameters.getInstance();
			new JCommander(params, args); 
			validatePitcherParams(params);
			new Pitcher(params).start();
		}
	}
}
