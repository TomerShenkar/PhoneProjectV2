import java.util.Scanner;
public class mainProgram {
	
	static Scanner reader = new Scanner(System.in);
	public static void main(String[] args) {
		
		serialHandler SH = new serialHandler();
		String[] names = SH.listOfPorts();
		
		System.out.println("Pick your port:");
		for(int i = 0; i<names.length; i++) {
			System.out.println(i + " " + names[i]);
		}
		int pick = reader.nextInt();
		if(SH.portOpener(pick)) {
			serialListener sl = new collectSerialData();
			SH.setListener(sl);
			
			while(true) {
				System.out.println("Type a thing");
				String msg = reader.nextLine();
				SH.writeString(msg, true);
			}
			
			//SH.portCloser();
			//System.out.println("Cya l8r");
		}
		else
		{
			System.out.println("Error message");
		}
		
	}
}
