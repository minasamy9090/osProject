import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Memory {
	ArrayList<String[]> mem;
	Queue<ArrayList<String[]>> disk;

	public Memory() {
		mem = new ArrayList<String[]>();
		disk = new LinkedList<ArrayList<String[]>>();
	}

	public int[] insertIntoMemory(ArrayList<String[]> process) {
		ArrayList<String[]> instruction = new ArrayList<String[]>();
		for (int i = 0; i < process.size(); i++) {
			if (process.get(i)[0].equals("file") || process.get(i)[0].equals("userInput")
					|| process.get(i)[0].equals("userOutput")) {
				continue;
			} else {
				instruction.add(process.get(i));
			}
		}
		int[] memBoundaries = new int[2];
		memBoundaries[0] = mem.size();
		if (mem.size() + instruction.size() < 40 || mem.size() + disk.peek().size() < 40) {
			if (disk.size() == 0) {
				for (int i = 0; i < instruction.size(); i++) {
					mem.add(instruction.get(i));
				}
			} else {
				if (mem.size() + disk.peek().size() < 40) {
					for (int i = 0; i < disk.peek().size(); i++) {
						mem.add(disk.peek().get(i));
					}
					disk.poll();
				}
				disk.add(instruction);
			}
		} else {
			disk.add(instruction);
		}
		memBoundaries[1] = mem.size() - 1;
		return memBoundaries;
	}

	public void printMemory() {
		for (int i = 0; i < mem.size(); i++) {
			System.out.println("***** START OF INDEX ******");
			System.out.println("Index :" + i);
			System.out.println("");
			for (int j = 0; j < mem.get(i).length; j++) {
				System.out.print(mem.get(i)[j] + " ");
			}
			System.out.println("");
			System.out.println("******* END OF INDEX ********");
		}
		System.out.println("*********** DISK ************");
		if (disk.size()==0) {
			System.out.println("Disk is Empty.");
		}else {
			System.out.println(disk.peek());
		}
	}

	public void insertPCB(String[] PCBID) {
		ArrayList<String[]> tmp = new ArrayList<String[]>();
		tmp.add(PCBID);
		insertIntoMemory(tmp);
	}

	public void insertVariable(String[] varVal) {
		ArrayList<String[]> tmp = new ArrayList<String[]>();
		tmp.add(varVal);
		for (int i = 0; i < mem.size(); i++) {
			if (tmp.get(0)[0].equals(mem.get(i)[0])) {
				mem.remove(i);
			}
		}
		insertIntoMemory(tmp);
	}

	public void removeFromMemory(ArrayList<String[]> process) {
		for (int i = 0; i < process.size(); i++) {
			mem.remove(process.get(i));
		}
		mem.remove(0);
	}

}
