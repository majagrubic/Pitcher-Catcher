
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;


public class PitcherParameters {

	private PitcherParameters() {
	}

	public static PitcherParameters getInstance() {
		return new PitcherParameters();
	}
	
	 @Parameter(names="-port", description = "TCP socket port")
	 public Integer port;
	
	 @Parameter(names="-mps", description = "messages per second", required=false)
	 public Integer rate = 1;
	 
	 @Parameter(names="-size", description = "messages size", required=false)
	 public Integer size = 300;
	 
	 @Parameter(description = "hostname")
	 public List<String>hostname = new ArrayList<String>();
	 
	 public String getHostName() {
		 if (!hostname.isEmpty()) {
			 return hostname.get(0);
		 }
		 return null;
	 }
}
