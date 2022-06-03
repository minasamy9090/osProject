import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class ProcessControlBlock {
	int processID;
	String processState;
	int programCounter;
	int[] memoryBoundaries;

	public ProcessControlBlock(int processID, int[] memoryBoundaries) {
		this.processID = processID;
		this.processState = null;
		this.programCounter = 0;
		this.memoryBoundaries = memoryBoundaries;
	}

	public void printPCB() {
		System.out.println("Process ID : " + this.processID);
		System.out.println("Process State : " + this.processState);
		System.out.println("Program Counter : " + this.programCounter);
		System.out.println("Memory Boundaries: --> Start : " + memoryBoundaries[0]);
		System.out.println("Memory Boundaries: --> End : " + memoryBoundaries[1]);
	}

	public void incrementPC() {
		this.programCounter = this.programCounter + 1;
	}

	public void changeState(String newState) {
		this.processState = newState;
	}

}
