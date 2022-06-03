import java.util.Scanner;

public class SystemCalls {
	public SystemCalls() {
	}

	public String assignValue() {
		Scanner myObj = new Scanner(System.in);
		System.out.print("Please enter a value : ");
		String inputValue = myObj.nextLine();
		return inputValue;
	}
}
