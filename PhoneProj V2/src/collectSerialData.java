import java.util.Arrays;

@SuppressWarnings("serial")
public class collectSerialData extends mainProgramGUI implements serialListener{

	public void serialData(byte[] bytarr) { //Processing the data 
		byte[] s = Arrays.copyOfRange(bytarr, 0, bytarr.length);
		Addition = s;
	}
		
}
