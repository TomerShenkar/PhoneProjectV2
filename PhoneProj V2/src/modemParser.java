import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("serial")
public class modemParser extends mainProgramGUI implements serialListener{

	private static LocalDateTime modemTime;

	private static getLine GetLine = new getLine();
	private static boolean nextIsMSG = false;

	public void serialData(byte[] bytarr) {
		// Parser
		
		GetLine.addRaw(bytarr);
		while (!GetLine.getQ().isEmpty()) {
			String temp = GetLine.getNext();
			if (temp != null) {
				//System.out.println(temp);
				if (nextIsMSG) {
					textMain.append("Message is " + temp);
					nextIsMSG = false;
					
				} else if (temp.startsWith("RING")) {
					PhoneState = State.Ringing;
					textMain.append("Ringing");
					
				} else if (temp.startsWith("+CLIP:")) {
					String[] parts = temp.split("\"");
					String Number = parts[1];
					PhoneState = State.Ringing;
					textMain.append("Call from " + Number);
					
				} else if (temp.startsWith("NO CARRIER")) {
					PhoneState = State.Idle;
					textMain.append("End of call");
				}

				else if (temp.startsWith("+CCLK:")) {
					String ST = temp;
					String[] parts = ST.split("\"");
					String BigDate = parts[1].toString();
					String[] BigDateParts = BigDate.split(",");
					String Date = BigDateParts[0];
					String[] Dateparts = Date.split("/");

					String BigTime = BigDateParts[1];
					String[] Timeparts = BigTime.split("\\+");
					String FinalTime = Timeparts[0].toString();
					String[] FinalTimeparts = FinalTime.split(":");
					// System.out.println(FinalTime);

					int Year = Integer.parseInt(Dateparts[0]) + 2000;
					int Month = Integer.parseInt(Dateparts[1]);
					int Day = Integer.parseInt(Dateparts[2]);

					int Hour = Integer.parseInt(FinalTimeparts[0]);
					int Minute = Integer.parseInt(FinalTimeparts[1]);
					int Second = Integer.parseInt(FinalTimeparts[2]);

					modemTime = LocalDateTime.of(Year, Month, Day, Hour, Minute, Second);
					// System.out.println(modemTime);

					Timer timer = new Timer();
					TimerTask timertask = new TimerTask() {
						@Override
						public void run() {
							modemTime = modemTime.plusSeconds(1);
						}
					};
					timer.schedule(timertask, 1000L, 1000L);
				} else if (temp.startsWith("+CMT:")) {
					String Number = processMSG(temp);
					nextIsMSG = true;
					textMain.append("Text from " + Number);
				} else {
					// None of the above
				}
			}
		}
	}
		
	public static String processMSG(String MSG) {
		String[] MSGParts = MSG.split("\"");
		String Number = MSGParts[1];
		return Number;
	}
}
