import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class mainProgramGUI extends JFrame {
			
	private JPanel contentPane;
	private serialHandler SH = new serialHandler();
	
	protected static enum State {
		Idle, TypingNumber, TypingMessage, Dialing, Ringing, DuringCall;
	}

	protected static State PhoneState = State.Idle;
	protected static JTextArea textMain;

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
		
		JLabel TimeLabel = new JLabel("Time");
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
					serialListener sl = new modemParser();
					SH.setListener(sl);
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
}
