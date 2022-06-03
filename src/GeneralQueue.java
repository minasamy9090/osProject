import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class GeneralQueue {

	ArrayList<String> generalBlockedQueue;
	Queue<ArrayList<String[]>> inputBlockedQueue;
	Queue<ArrayList<String[]>> outputBlockedQueue;
	Queue<ArrayList<String[]>> fileBlockedQueue;

	public GeneralQueue() {
		this.generalBlockedQueue = new ArrayList<String>();
		this.inputBlockedQueue = new LinkedList<ArrayList<String[]>>();
		this.outputBlockedQueue = new LinkedList<ArrayList<String[]>>();
		this.fileBlockedQueue = new LinkedList<ArrayList<String[]>>();
	}

	public void enqueue(ArrayList<String[]> command, String mutex) {
		if (mutex.equals("file")) {
			fileBlockedQueue.add(command);
		} else if (mutex.equals("userInput")) {
			inputBlockedQueue.add(command);
		} else if (mutex.equals("userOutput")) {
			outputBlockedQueue.add(command);
		}
		generalBlockedQueue.add(arrayListToString(command) + " " + mutex);
		// --> generalBlockedQueue.split(" ");
	}

	public ArrayList<String[]> dequeue(String mutex) {
		ArrayList<String[]> returnedCommand = new ArrayList<String[]>();
		if (mutex.equals("file")) {
			returnedCommand = fileBlockedQueue.poll();
		} else if (mutex.equals("userInput")) {
			returnedCommand = inputBlockedQueue.poll();
		} else if (mutex.equals("userOutput")) {
			returnedCommand = outputBlockedQueue.poll();
		}
		for (int i = 0; i < generalBlockedQueue.size(); i++) {
			String[] arrOfStr = generalBlockedQueue.get(i).split(" ");
			if (arrOfStr[arrOfStr.length - 1].equals(mutex)) {
				generalBlockedQueue.remove(i);
				break;
			}
		}
		return returnedCommand;
	}

	public static String arrayListToString(ArrayList<String[]> command) {
		String output = "";
		for (int i = 0; i < command.size(); i++) {
			for (int j = 0; j < command.get(i).length; j++) {
				output += command.get(i)[j] + " ";
			}
		}
		return output;
	}

	public static void main(String[] args) {
		GeneralQueue test = new GeneralQueue();
		ArrayList<String[]> xy = new ArrayList<String[]>();
		ArrayList<String[]> xz = new ArrayList<String[]>();
		ArrayList<String[]> xv = new ArrayList<String[]>();
		String[] y = new String[4];
		String[] z = new String[4];
		String[] v = new String[4];
		y[0] = "assign";
		y[1] = "a";
		y[2] = "b";
		y[3] = "file";
		z[0] = "assign";
		z[1] = "a";
		z[2] = "z";
		z[3] = "file";
		v[0] = "assign";
		v[1] = "a";
		v[2] = "b";
		v[3] = "userInput";
		xy.add(y);
		xz.add(z);
		xv.add(v);
		test.enqueue(xy, "file");
		test.enqueue(xz, "file");
		test.enqueue(xv, "userInput");
		System.out.println(test.generalBlockedQueue);
		test.dequeue("userInput");
		System.out.println(test.generalBlockedQueue);
		test.dequeue("file");
		System.out.println(test.generalBlockedQueue);
	}
}
