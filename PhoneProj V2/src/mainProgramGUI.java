import java.awt.EventQueue;
import javax.swing.*;
import java.util.*;
//import java.util.Timer;

import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//import java.time.LocalDateTime;
import javax.swing.SwingWorker;

@SuppressWarnings("serial")
public class mainProgramGUI extends JFrame {
	private serialHandler SH = new serialHandler();

	protected static enum State {
		Idle, TypingNumber, TypingMessage, Dialing, Ringing, DuringCall;
	}

	protected static State PhoneState = State.Idle;
	protected static JLabel TimeLabel;
	protected static String Addition;

	//private static LocalDateTime modemTime;
	private static String phoneNum = "";
	private static getLine GetLine = new getLine();
	private static boolean nextIsMSG = false;
	private JPanel contentPane;
	private static JTextArea textMain;

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
		setBounds(100, 100, 333, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		
		String[] names = SH.listOfPorts();
		JComboBox<String> COMMboBox = new JComboBox<String>();
		COMMboBox.setBounds(100, 130, 120, 25);
		for (int i = 0; i < names.length; i++) {
			COMMboBox.addItem(names[i]);
		}
		contentPane.add(COMMboBox);

		JButton OpenPortbtn = new JButton("Open Port");
		OpenPortbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (SH.portOpener(COMMboBox.getSelectedIndex())) {
					serialListener sl = new collectSerialData();
					SH.setListener(sl);
					SH.writeString("AT+CCLK?\r", true);
					SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
						@Override
						protected Boolean doInBackground() throws Exception {
							// Simulate doing something useful.
							int x = 10;
							while (x < 1000) {
								Thread.sleep(1000);

								// The type we pass to publish() is determined
								// by the second template parameter.
								if (Addition != null) {
									GetLine.addRaw(Addition);
									// System.out.print(OldestValue);

									while (!GetLine.getQ().isEmpty()) {
										String temp = GetLine.getNext();
										publish(Sim900Parse(temp));
									}
									// publish(Addition);
									// System.out.print(Addition);
									// Addition = "";
								}
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
						protected void process(List<String> chunks) {
							// Here we receive the values that we publish().
							// They may come grouped in chunks.
							if (chunks.size() > 0) {
								for (int i = 0; i < chunks.size(); i++) {
									// String OldestValue = chunks.get(i);
									textMain.setText(chunks.get(i));
									textMain.setText("\r\n");
								}
								chunks.clear();
							}
						}

					};

					worker.execute();
					OpenPortbtn.setEnabled(false);
				}
			}
		});
		OpenPortbtn.setBounds(100, 155, 120, 25);
		contentPane.add(OpenPortbtn);

		JButton Answerbtn = new JButton("A");
		Answerbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (PhoneState == State.Ringing) {
					SH.writeString("ATA", true);
				} else if (PhoneState == State.TypingNumber) {
					if (phoneNum.length() > 0) {
						SH.writeString("ATD" + phoneNum + ";", true);
						PhoneState = State.Dialing;
						textMain.setText("Calling " + phoneNum);
					}
				}
			}
		});
		Answerbtn.setBounds(10, 130, 50, 50);
		contentPane.add(Answerbtn);

		JButton Declinebtn = new JButton("D");
		Declinebtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				phoneNum = "";
				if (PhoneState == State.DuringCall || PhoneState == State.Ringing || PhoneState == State.Dialing) {
					SH.writeString("ATH", true);
					textMain.setText("End of call");
				}
			}
		});
		Declinebtn.setBounds(260, 130, 50, 50);
		contentPane.add(Declinebtn);

		JButton button_1 = new JButton("1");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				typingNumber("1");
			}
		});
		button_1.setBounds(70, 200, 50, 50);
		contentPane.add(button_1);

		JButton button_2 = new JButton("2");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				typingNumber("2");
			}
		});
		button_2.setBounds(133, 200, 50, 50);
		contentPane.add(button_2);

		JButton button_3 = new JButton("3");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				typingNumber("3");
			}
		});
		button_3.setBounds(193, 200, 50, 50);
		contentPane.add(button_3);

		JButton button_4 = new JButton("4");
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				typingNumber("4");
			}
		});
		button_4.setBounds(70, 260, 50, 50);
		contentPane.add(button_4);

		JButton button_5 = new JButton("5");
		button_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				typingNumber("5");
			}
		});
		button_5.setBounds(133, 260, 50, 50);
		contentPane.add(button_5);

		JButton button_6 = new JButton("6");
		button_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				typingNumber("6");
			}
		});
		button_6.setBounds(192, 260, 50, 50);
		contentPane.add(button_6);

		JButton button_7 = new JButton("7");
		button_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				typingNumber("7");
			}
		});
		button_7.setBounds(70, 320, 50, 50);
		contentPane.add(button_7);

		JButton button_8 = new JButton("8");
		button_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				typingNumber("8");
			}
		});
		button_8.setBounds(133, 320, 50, 50);
		contentPane.add(button_8);

		JButton button_9 = new JButton("9");
		button_9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				typingNumber("9");
			}
		});
		button_9.setBounds(192, 320, 50, 50);
		contentPane.add(button_9);

		JButton button_Star = new JButton("*");
		button_Star.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				typingNumber("*");
			}
		});
		button_Star.setBounds(70, 380, 50, 50);
		contentPane.add(button_Star);

		JButton button_0 = new JButton("0");
		button_0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				typingNumber("0");
			}
		});
		button_0.setBounds(133, 380, 50, 50);
		contentPane.add(button_0);

		JButton button_Hash = new JButton("#");
		button_Hash.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				typingNumber("#");
			}
		});
		button_Hash.setBounds(192, 380, 50, 50);
		contentPane.add(button_Hash);

		JButton btnClr = new JButton("CLR");
		btnClr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (phoneNum != null && phoneNum.length() > 0) {
					phoneNum = phoneNum.substring(0, phoneNum.length() - 1);
					textMain.setText(phoneNum);
					if (phoneNum.length() == 0) {
						PhoneState = State.Idle;
					}
				}
			}
		});
		btnClr.setBounds(125, 108, 70, 20);
		contentPane.add(btnClr);
		
		textMain = new JTextArea();
		textMain.setBounds(10, 11, 300, 86);
		contentPane.add(textMain);
	}

	public static String processMSG(String MSG) {
		String[] MSGParts = MSG.split("\"");
		String Number = MSGParts[1];
		return Number;
	}

	public static void typingNumber(String key) {
		phoneNum = phoneNum + key;
		textMain.setText(phoneNum);
		PhoneState = State.TypingNumber;
	}
	/*
	 *  public static String settingTime() {
			modemTime = modemTime.plusSeconds(1);
			//TimeLabel.setText(modemTime.toString());
			String s = modemTime.toString();
			String[] sparts = s.split("T");
			return(sparts[1]);
		}
	 */

	
	private String Sim900Parse(String temp) {
		if (temp != null) {
			if (nextIsMSG) {
				nextIsMSG = false;
				return ("Message is " + temp);

			} else if (temp.startsWith("RING")) {
				PhoneState = State.Ringing;
				return ("Ringing");

			} else if (temp.startsWith("+CLIP:")) {
				String[] parts = temp.split("\"");
				String Number = parts[1];
				PhoneState = State.Ringing;
				return ("Call from " + Number);

			} else if (temp.startsWith("NO CARRIER")) {
				PhoneState = State.Idle;
				return ("End of call");
			} else if (temp.startsWith("+CCLK:")) {
				/*
				 * 				String ST = temp;
				String[] parts = ST.split("\"");
				String BigDate = parts[1].toString();
				String[] BigDateParts = BigDate.split(",");
				String Date = BigDateParts[0];
				String[] Dateparts = Date.split("/");

				String BigTime = BigDateParts[1];
				String[] Timeparts = BigTime.split("\\+");
				String FinalTime = Timeparts[0].toString();
				String[] FinalTimeparts = FinalTime.split(":"); // System.out.println(FinalTime);

				int Year = Integer.parseInt(Dateparts[0]) + 2000;
				int Month = Integer.parseInt(Dateparts[1]);
				int Day = Integer.parseInt(Dateparts[2]);

				int Hour = Integer.parseInt(FinalTimeparts[0]);
				int Minute = Integer.parseInt(FinalTimeparts[1]);
				int Second = Integer.parseInt(FinalTimeparts[2]);

				modemTime = LocalDateTime.of(Year, Month, Day, Hour, Minute, Second); 
				
				Timer timer = new Timer();
				TimerTask timertask = new TimerTask() {
					
					
					@Override
					public void run() {
						modemTime = modemTime.plusSeconds(1);
						//TimeLabel.setText(modemTime.toString());
						setTitle(modemTime.toString());
					}
				};
				timer.schedule(timertask, 1000L, 1000L);
				 */
			}

			else if (temp.startsWith("+CMT:")) {
				String Number = processMSG(temp);
				nextIsMSG = true;
				return ("Text from " + Number);
			} else {
				// None of the above
			}
		}
		return "";
	}
}
