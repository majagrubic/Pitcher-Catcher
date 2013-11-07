
import com.beust.jcommander.Parameter;

public class CatcherParameters {

	private CatcherParameters() {
	}

	public static CatcherParameters getInstance() {
		return new CatcherParameters();
	}

	@Parameter(names = "-port", description = "TCP socket port")
	public Integer port;

	@Parameter(names = "-bind", description = "TCP socket bind address")
	public String ipAddress;
}
