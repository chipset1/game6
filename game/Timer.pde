//HTIMER 
class Timer {
	private int lastInterval, interval, cycleCounter, numCycles;
	private boolean usesFrames = false;

	public Timer(int timerInterval, int numberOfCycles) {
		interval = timerInterval;
		numCycles = numberOfCycles;
	}
	
	public void useFrames() {
		usesFrames = true;
	}

	// find better name
	public boolean canRun() {
		int curr = (usesFrames)? frameCount : millis();
		if(lastInterval < 0) lastInterval = curr;
		if(curr-lastInterval >= interval) {
			lastInterval = curr;
			if(numCycles > 0 && ++cycleCounter >= numCycles) stop();
			return true;
		}
		return false;
	}

	public void stop() {
		numCycles = 0;
		lastInterval = -1;
	}

}