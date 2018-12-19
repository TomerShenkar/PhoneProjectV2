@SuppressWarnings("serial")
public class collectSerialData extends mainProgramGUI implements serialListener{

	public void serialData(byte[] rawData) { //Processing the data 
		//byte[] s = Arrays.copyOfRange(bytarr, 0, bytarr.length);
		//System.out.print(new String(s));
		Addition += new String(rawData);
	}
		
}
