import java.awt.EventQueue;
import javax.swing.*;
import java.util.*;
import java.util.Timer;

import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import javax.swing.SwingWorker;

@SuppressWarnings("serial")
public class mainProgramGUI extends JFrame {
			
	private JPanel contentPane;
	private serialHandler SH = new serialHandler();
	
	protected static enum State {
		Idle, TypingNumber, TypingMessage, Dialing, Ringing, DuringCall;
	}

	protected static State PhoneState = State.Idle;
	protected static JTextArea textMain;
	protected static JLabel TimeLabel;
	protected static byte[] Addition;
	
	private static LocalDateTime modemTime;
	
	private static getLine GetLine = new getLine();
	private static boolean nextIsMSG = false;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainProgramGUI frame = new mainProgramGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public mainProgramGUI() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				SH.portCloser();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 9, 414, 111);
		contentPane.add(scrollPane);
		
		textMain = new JTextArea();
		scrollPane.setViewportView(textMain);
		
		JLabel TimeLabel = new JLabel("");
		
		scrollPane.setColumnHeaderView(TimeLabel);
		
		String[] names = SH.listOfPorts();
		JComboBox<String> COMMboBox = new JComboBox<String>();
		COMMboBox.setBounds(256, 131, 168, 20);
		for(int i = 0; i<names.length; i++) {
			COMMboBox.addItem(names[i]);
		}
		contentPane.add(COMMboBox);
		
		JButton OpenPortbtn = new JButton("Open Port");
		OpenPortbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(SH.portOpener(COMMboBox.getSelectedIndex())) {
					serialListener sl = new collectSerialData();
					SH.setListener(sl);
					 SwingWorker<Boolean, byte[]> worker = new SwingWorker<Boolean, byte[]>() {
						 @Override
						 protected Boolean doInBackground() throws Exception {
						  // Simulate doing something useful.
							 int x  = 10;
						  while(x < 1000) {
						   Thread.sleep(10);

						   // The type we pass to publish() is determined
						   // by the second template parameter.
						   publish(Addition);
						  }

						  // Here we can return some object of whatever type
						  // we specified for the first template parameter.
						  // (in this case we're auto-boxing 'true').
						  return true;
						 }

						 // Can safely update the GUI from this method.
						 protected void done() {
						 }

						 @Override
						 // Can safely update the GUI from this method.
						 protected void process(List<byte[]> chunks) {
						  // Here we receive the values that we publish().
						  // They may come grouped in chunks.
						  byte[] mostRecentValue = chunks.get(chunks.size()-1);
						  	
						  GetLine.addRaw(mostRecentValue);
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
												TimeLabel.setName(modemTime.toString());
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

						};

						worker.execute();
					OpenPortbtn.setEnabled(false);
				}
			}
		});
		OpenPortbtn.setBounds(134, 131, 112, 23);
		contentPane.add(OpenPortbtn);
		
		JButton Answerbtn = new JButton("A");
		Answerbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(PhoneState == State.Ringing) {
					SH.writeString("ATA", true);
				}
				else if(PhoneState == State.Idle) {
					//LATER - WHEN TYPING A NUMBER
				}
			}
		});
		Answerbtn.setBounds(10, 131, 50, 50);
		contentPane.add(Answerbtn);
		
		JButton Declinebtn = new JButton("D");
		Declinebtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(PhoneState == State.DuringCall || PhoneState == State.Ringing) {
					SH.writeString("ATH", true);
				}
			}
		});
		Declinebtn.setBounds(70, 131, 50, 50);
		contentPane.add(Declinebtn);
	}
	
	public static String processMSG(String MSG) {
String[] MSGParts = MSG.split("\"");
String Number = MSGParts[1];
return Number;
}	
}
