


public class TimeStatistics {

	private TimeStatistics(){
	}
	
	public static TimeStatistics getInstance() {
		return new TimeStatistics();
	}
	
	//total time required for message to complete the A->B->A cycle
	private long totalCycleTime = 0;
	
	//average times; will get reset every second
	//time required for message to complete the A->B->A cycle
	private long currentCycleTime = 0;
	//time required for message to complete the A->B path
	private long currentABTime = 0;
	//time required for message to complete the B->A path
	private long currentBATime = 0;
	
	
	public synchronized void updateBATime(long abTime) {
		currentABTime+= abTime;
	}
	
	public synchronized void resetTimes() {
		currentCycleTime = 0;
		currentABTime = 0;
		currentBATime = 0;
	}
	
	public void updateStatistics(long timestampA, long timestampB, long currentA) {
		//time to complete A->B->A circle
		long cycleTime = currentA - timestampA;
		totalCycleTime+=cycleTime;
		currentCycleTime+=cycleTime;
		
		//time to complete A->B
		currentABTime+= Math.abs(timestampB - timestampA);
		
		//time to complete B->A
		currentBATime+= Math.abs(currentA - timestampB);
	}
	
	public long getCurrentCycleTime() {
		return currentCycleTime;
	}
	
	public long getCurrentABTime() {
		return currentABTime;
	}
	
	public long getCurrentBATime(){
		return currentBATime;
	}
	
	public long getTotalCycleTime() {
		return totalCycleTime;
	}
	
	
	
}
