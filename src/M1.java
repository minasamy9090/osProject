import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class M1 {
	private static HashMap<String, String> variables = new HashMap<String, String>();
	private static HashMap<String, Integer> mutex = new HashMap<String, Integer>();
	private static HashMap<Integer, ProcessControlBlock> processControlBlocks = new HashMap<Integer, ProcessControlBlock>();
	private static HashMap<ArrayList<String[]>, Integer> processIdentificationMap = new HashMap<ArrayList<String[]>, Integer>();
	static Queue<ArrayList<String[]>> ReadyQueue = new LinkedList<ArrayList<String[]>>();
	static GeneralQueue blockedQueue = new GeneralQueue();
	static Memory memory = new Memory();
	private static int timer = 0;
	private static SystemCalls systemCalls = new SystemCalls();

	private static void readInstructionFile(String fileName) {
		try {
			File myObj = new File(fileName);
			Scanner myReader = new Scanner(myObj);
			ArrayList<String[]> fileContent = fileReader(myReader);
			interpreter(fileContent);
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	private static void interpreter(ArrayList<String[]> fileContent) {
		for (int i = 0; i < fileContent.size(); i++) {
			if (fileContent.get(i)[0].equals("semWait")) {
				semWait(fileContent, i);
			} else if (fileContent.get(i)[0].equals("semSignal")) {
				semSignal(fileContent.get(i)[1]);
			}

		}
	}

	private static int readyQueueEnqueue(String call, ArrayList<String[]> remainingInstructions, int counter) {
		ArrayList<String[]> ready = new ArrayList<String[]>();
		String[] callList = new String[1];
		callList[0] = call;
		int i = counter;
		for (i = counter; i < remainingInstructions.size(); i++) {
			if (!remainingInstructions.get(i)[0].equals("semSignal")) {
				ready.add(remainingInstructions.get(i));
			} else {
				break;
			}
		}
		ready.add(callList);
		ReadyQueue.add(ready);
		int int_random = ThreadLocalRandom.current().nextInt();
		while (int_random < 0) {
			int_random = ThreadLocalRandom.current().nextInt();
		}
		processIdentificationMap.put(ready, int_random);
		String[] idOfPCB = new String[1];
		idOfPCB[0] = int_random + "";
		ProcessControlBlock pcb = new ProcessControlBlock(int_random, memory.insertIntoMemory(ready));
		memory.insertPCB(idOfPCB);
		pcb.changeState("ready");
		processControlBlocks.put(int_random, pcb);
		return i;
	}

	private static int blockedQueueEnqueue(ArrayList<String[]> remainingInstructions, int counter, String mutex) {
		ArrayList<String[]> blocked = new ArrayList<String[]>();
		int i = counter;
		for (i = counter; i < remainingInstructions.size(); i++) {
			if (!remainingInstructions.get(i)[0].equals("semSignal")) {
				blocked.add(remainingInstructions.get(i));
			} else {
				break;
			}
		}
		blockedQueue.enqueue(blocked, mutex);
		int int_random = ThreadLocalRandom.current().nextInt();
		while (int_random < 0) {
			int_random = ThreadLocalRandom.current().nextInt();
		}
		processIdentificationMap.put(blocked, int_random);
		String[] idOfPCB = new String[1];
		idOfPCB[0] = int_random + "";
		ProcessControlBlock pcb = new ProcessControlBlock(int_random, memory.insertIntoMemory(blocked));
		memory.insertPCB(idOfPCB);
		pcb.changeState("blocked");
		processControlBlocks.put(int_random, pcb);
		return i;
	}

	private static ArrayList<String[]> fileReader(Scanner myReader) {
		ArrayList<String[]> allData = new ArrayList<String[]>();
		while (myReader.hasNextLine()) {
			String data = myReader.nextLine();
			String[] arrOfStr = data.split(" ");
			allData.add(arrOfStr);
		}
		return allData;
	}

	private static void executeCommand(String[] command) {
		if (command.length == 2) {
			switch (command[0]) {
			case ("print"):
				print(command[1]);
				break;
			case ("readFile"):
				String fileContent = readFile(command[1]);
				break;
			default:
				System.out.println("Cannot Find Instruction");
			}
		} else if (command.length == 3) {
			switch (command[0]) {
			case ("printFromTo"):
				printFromTo(command[1], command[2]);
				break;
			case ("assign"):
				assign_3(command[1], command[2]);
				break;
			case ("writeFile"):
				writeFile(command[1], command[2]);
				break;
			default:
				System.out.println("Cannot Find Instruction");
			}
		} else if (command.length == 4) {
			switch (command[0]) {
			case ("assign"):
				String fileContent = readFile(command[3]);
				assign_3(command[1], fileContent);
				break;
			default:
				System.out.println("Cannot Find Instruction");
			}
		}
	}

	private static void semWait(ArrayList<String[]> fileContent, int previousCounter) {
		String call = fileContent.get(previousCounter)[1];

		if (mutex.get(call) < 1) {
			blockedQueueEnqueue(fileContent, previousCounter + 1, call);
			mutex.put(call, mutex.get(call) - 1);

		} else {
			readyQueueEnqueue(call, fileContent, previousCounter + 1);
			mutex.put(call, mutex.get(call) - 1);
		}
	}

	private static void semSignal(String call) {
		mutex.put(call, mutex.get(call) + 1);
	}

	private static void assign_3(String value1, String value2) {
		if (value2.equals("input")) {
			String inputValue = systemCalls.assignValue();
			variables.put(value1, inputValue);
			String[] tmp = new String[2];
			tmp[0] = value1;
			tmp[1] = inputValue;
			memory.insertVariable(tmp);
		} else {
			variables.put(value1, value2);
			String[] tmp = new String[2];
			tmp[0] = value1;
			tmp[1] = value2;
			memory.insertVariable(tmp);
		}
	}

	private static void printFromTo(String value1, String value2) {
		int lowerBound = Integer.parseInt(variables.get(value1));
		int upperBound = Integer.parseInt(variables.get(value2));
		while (lowerBound <= upperBound) {
			System.out.println(lowerBound);
			lowerBound++;
		}

	}

	private static void print(Object param_1) {
		System.out.println(variables.get(param_1));
	}

	private static void writeFile(String value1, String value2) {
		String fileName = variables.get(value1);
		String fileContent = variables.get(value2);

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(fileContent);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String readFile(String value1) {
		String fileName = variables.get(value1);
		String fileContent = "";
		try {
			File myObj = new File(fileName);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				fileContent = fileContent + data + "\n";
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		return fileContent;

	}

	private static void scheduler(Queue<ArrayList<String[]>> readyQueue, GeneralQueue blockedQueues, int counter,
			Stopwatch stopwatch1) {
		for (int i = 0; i < readyQueue.size(); i++) {
			if (mutex.get("userInput") == 1) {
				readyQueue.add(blockedQueues.dequeue("userInput"));
			}
			if (mutex.get("userOutput") == 1) {
				readyQueue.add(blockedQueues.dequeue("userOutput"));
			}
			if (mutex.get("file") == 1) {
				readyQueue.add(blockedQueues.dequeue("file"));
			}
			for (int j = 0; j < counter; j++) {
				ArrayList<String[]> commands = readyQueue.poll();
				if (processIdentificationMap.get(commands) != null) {
					System.out.println("***********PCB & Memory INFO***********");
					System.out.println("ID : " + processIdentificationMap.get(commands));
					if (processControlBlocks.get(processIdentificationMap.get(commands)) != null) {
						processControlBlocks.get(processIdentificationMap.get(commands)).changeState("running");
						processControlBlocks.get(processIdentificationMap.get(commands)).printPCB();
						memory.printMemory();
						memory.removeFromMemory(commands);
						System.out.println("************************************");
					}
				}
				int executedInSameProcess = 0;
				if (commands != null) {
					String call = commands.get(commands.size() - 1)[0];
					semaphoreDec(call);
					for (int k = 0; k < commands.size() - 1; k++) {
						processControlBlocks.get(processIdentificationMap.get(commands)).incrementPC();
						executeCommand(commands.get(k));
						System.out.println("--------STATS---------");
						System.out.println("Timer : " + ++timer);
						System.out.println("UserInput : " + mutex.get("userInput"));
						System.out.println("UserOutput : " + mutex.get("userOutput"));
						System.out.println("File : " + mutex.get("file"));
						System.out.println("----------------------");
						executedInSameProcess += 1;
						if (executedInSameProcess > counter) {
							break;
						}
					}
					semSignal(call);
					for (int k = 0; k < executedInSameProcess; k++) {
						commands.remove(0);
					}
					if (!(commands.size() == 0)) {
						readyQueue.add(commands);
					}
				} else {
					System.out.println("--> Program Finished.");
					stopwatch1.stop();
					System.out.println("--> Elapsed time in milliseconds: " + stopwatch1.getElapsedMilliseconds());
					System.out.println("--> Elapsed time in seconds: " + stopwatch1.getElapsedSeconds());
					System.exit(0);
				}
			}
		}
	}

	private static void semaphoreDec(String call) {
		mutex.put(call, mutex.get(call) - 1);
	}

	public static void main(String[] args) {
		mutex.put("userInput", 1);
		mutex.put("userOutput", 1);
		mutex.put("file", 1);

		Stopwatch stopwatch1 = new Stopwatch();
		stopwatch1.start();

		readInstructionFile("Program_1.txt");
		readInstructionFile("Program_2.txt");
		readInstructionFile("Program_3.txt");

		scheduler(ReadyQueue, blockedQueue, 2, stopwatch1);

	}
}