import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.text.DefaultCaret;

import it.unisa.dia.gas.jpbc.Element;

public class PMQGUI implements ActionListener {
	
	//BGN Related
	private BGN bgn;	
	int kg=50;//BGN Key Generation
    private BGN.PublicKey pubkey;
    private BGN.PrivateKey prikey;

	//Variables	
	private int n = 50; //Size of Set D - number of IoTs
	private int m = 10; //Size of Vector beta in Control Center
	private int delta1 = 5;  // 0<=alfa<i> vec of each IoT<i> < delta1
	private int delta2 = 5; // 0<=beta vec items<delta2
	private int k = 10; //Size of Set DSatr - Selected IoT
	private int temp; //Generating DStar and Function f
	private Random rnd = new Random();
	private Element temp_1G;//Algorithm 2	
	private BigInteger selfblindRnd;//Algorithm 2
	
	private String strBGNInitializationReport;//Store BGN initialization report for later use.
	
	
	//Vectors
	private Vector<Integer> beta; //Vector beta in CC
	private Vector<Integer> DStar;//|DStar|=k
	private Vector<Element> VecA;//Algorithm 1
	private Vector<Element> VecB;//Algorithm 1		
	private Vector <Element> vec_c;//Algorithm 2
	private Vector <Element> vec_C;//Algorithm 3
	
	
	
	//Timers
	private Timer clock1 = new Timer(1000,this);
	private Timer clock2 = new Timer(20000,this);
	
	
	//Components
	private JFrame pmqFrame;
	private IoTDevice[] iot = new IoTDevice[n];
	private FogDevice fog;
	private ControlCenter cc;
	private JTextArea rpt;

	
	
	@Override		
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == clock1)
		{
			//clock1.stop();
			//clock2.getActionListeners()[0].actionPerformed(new ActionEvent(clock2, ActionEvent.ACTION_PERFORMED, null));
			//clock2.start();
			clock1.stop();
			
			//BGN Initialization - Start
			
			//BGN Setup - Start
			bgn = new BGN();					
		    bgn.keyGeneration(kg);
		    pubkey = bgn.getPubkey();
		    prikey = bgn.getPrikey();
		    rpt.setText(bgn.getBGNInfo(kg, pubkey, prikey));
		    rpt.update(rpt.getGraphics());
		   //BGN Setup - End
			
			//Control Center
			beta = new Vector<Integer>(m); //Vector beta in CC
			Random rand = new Random();
			for (int i=0;i<m;i++)
			{
				beta.add(rand.nextInt(delta2));
			}
			rpt.append("Vector β in CC (|β| = "+m+") -»> "+beta.toString()+"\n");
			rpt.update(rpt.getGraphics());
			//Control Center
		
			
			
			DStar= new Vector<Integer>(k);//|DStar|=k
			//int temp; //Generating DStar and Function f
			
			VecA = new Vector<Element>(n);//Algorithm 1
			VecB = new Vector<Element>(m);//Algorithm 1
			
			//BigInteger selfblindRnd;//Algorithm 2
			vec_c = new Vector<Element>(n);//Algorithm 2
			//Element temp_1G;//Algorithm 2
			
			vec_C = new Vector<Element>(k);//Algorithm 3
			
			strBGNInitializationReport = rpt.getText();
			//BGN Initialization - End
	
			//Call clock2 manually for first run
			clock2.getActionListeners()[0].actionPerformed(new ActionEvent(clock2, ActionEvent.ACTION_PERFORMED, null));
			//Call clock2 manually for first run
			clock2.start();
		
		} else if (e.getSource() == clock2)
		{
			//System.out.println("clock2");
			//********************************
			//IoTs 
			//D=IoTs >> |D|=Total Number of IoTs = n
			//DStar=Selected IoTs >> |DStar|=Number of Selected IoTs = k
			//Each IoT has a vector alfa with length m and each 0 < alfa<i> <delta1
			//IoT[i]		
			rpt.setText(strBGNInitializationReport);
			rpt.update(rpt.getGraphics());
			for(int i=0;i<n;i++)
				iot[i].setEnabled(false);
			for(int i=0;i<n;i++){
				iot[i].GenerateVecAlfa();
				rpt.append("Vector α in IoT["+(i)+"] (|α| = "+m+") -»> "+iot[i].GetAlfa().toString()+"\n");
				rpt.update(rpt.getGraphics());
			}		
			
			
			//********************************
			//Select IoTs
			//DStar=Selected IoTs >> |DStar|=Number of Selected IoTs = k
			//Function f(); Simply make a random mapping					
			DStar.clear();
	        while(DStar.size()< k) {
	        	temp = rnd.nextInt(n);       
	        	if(DStar.contains(temp) == false)
	        		DStar.add(temp);
	        }
	        rpt.append("Subset D* (|D*| = "+k+") -»> "+DStar.toString()+"\n");
	        rpt.update(rpt.getGraphics());
	        for(int i=0;i<n;i++)
	        	if (DStar.contains(i))
	        	{
	        		iot[i].setEnabled(true);
	        		iot[i].update(iot[i].getGraphics());
	        	}
	        
	        
	        
		    
	        //********************************
			//Algorithm 1
	        rpt.append("********** Algorithm 1 **********\n");
	        rpt.update(rpt.getGraphics());
			VecA.clear();
			//Vector<Integer> VecA = new Vector<Integer>(n);
	        try {
	        	for(int i=0;i<n;i++)
	        		if (DStar.contains(i) == true)
	        			VecA.add(BGN.encrypt(1, pubkey));
	        		else
	        			VecA.add(BGN.encrypt(0, pubkey));
	        	} catch (Exception e1) {e1.printStackTrace(); }
	        rpt.append("Vector A {E(0),E(1)}* |A| = "+n+" -»> "+VecA.toString()+"\n");
	        rpt.update(rpt.getGraphics());
	        	        
	        VecB.clear();
	        try {
	        	for(int i=0;i<m;i++)
	        		VecB.add(BGN.encrypt(beta.get(i), pubkey));       		
	        	} catch (Exception e2) {e2.printStackTrace(); }
	        rpt.append("Vector B {E(β)} |B| = "+m+" -»> " + VecB.toString()+"\n");
	        rpt.update(rpt.getGraphics());
	        
			
	        
	        //********************************
	        //Algorithm 2
	        rpt.append("********** Algorithm 2 **********\n");
	        rpt.update(rpt.getGraphics());
	        vec_c.clear();
	        
	        for(int i=0;i<n;i++)
	        {
	        	temp_1G = pubkey.getPairing().getG1().newRandomElement().setToOne();
	        	for(int j=0;j<m;j++)
	        		temp_1G = BGN.add(temp_1G, BGN.mul1(VecB.get(j), iot[i].GetAlfa().get(j)));
	    		selfblindRnd = pubkey.getPairing().getZr().newRandomElement().toBigInteger();//generate r for h^r and self-blinding
	    		temp_1G = BGN.selfBlind(temp_1G, selfblindRnd, pubkey);
	    		
	        	rpt.append("   Self Blinded Response - IoT["+i+"] -»> " + temp_1G.toString()+"\n");
	        	rpt.update(rpt.getGraphics());
	        	vec_c.add(temp_1G);
	        }
	        rpt.append("Vector c {(Π B^α).h^r} |c| = "+n+" -»> "+ vec_c.toString()+"\n");
	        rpt.update(rpt.getGraphics());
	     

	        
	        //********************************
	        //Algorithm 3
	        rpt.append("********** Algorithm 3 **********\n");
	        rpt.update(rpt.getGraphics());
	        vec_C.clear();
			for(int i=0;i<n;i++)
				if(DStar.contains(i))
				   vec_C.add(BGN.mul2( vec_c.get(i), VecA.get(i), pubkey));
			
			rpt.append("Vector C {e(A,c)} |C|=[DStar| = "+k+" -»> "+ vec_C.toString()+"\n");
			rpt.update(rpt.getGraphics());
			
			
	
			//********************************
	        //Decryption Process in Control Center
			rpt.append("********** Decryption Started in CC **********\n");
			rpt.update(rpt.getGraphics());
			for(Element f:vec_C)
			{
				try{
					rpt.append("Result -»> " + BGN.decrypt_mul2(f, pubkey, prikey)+"\n");
					rpt.update(rpt.getGraphics());
				}catch(Exception e3) {e3.printStackTrace(); }
			}
			rpt.append("********** Decryption Ended in CC **********\n");
			rpt.update(rpt.getGraphics());
			
			
			
		} else if (e.getSource() == cc)
		{
			JOptionPane.showMessageDialog(((ControlCenter)(e.getSource())).getParent().getParent(), "Vector β in Control Center -»> "+ beta.toString() );
		} else if (e.getSource() == fog)
		{
			JOptionPane.showMessageDialog(((FogDevice)(e.getSource())).getParent().getParent(), "Fog Device");
		} else
		{
			JOptionPane.showMessageDialog(((IoTDevice)(e.getSource())).getParent().getParent(), "Vector α in IoT" +((IoTDevice)(e.getSource())).getText() +" -»> "+ ((IoTDevice)(e.getSource())).GetAlfa().toString());
			//JOptionPane.showMessageDialog(((JButton)(e.getSource())).getParent().getParent(), "Vector α in IoT" +((JButton)(e.getSource())).getText());
			//System.out.println("btn");
		}

	}
	
	
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PMQGUI window = new PMQGUI();
					window.pmqFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public PMQGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		pmqFrame = new JFrame();
		pmqFrame.setTitle("Privacy-Preserving Multi(PMQ) Dot-Product Query in Fog Computing-Enhanced IoT");
		pmqFrame.setLayout(null);
		pmqFrame.setSize(1200,750);
		pmqFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		 //IoT Devices - START
	    JPanel iotPanel = new JPanel();
	    iotPanel.setLayout(null);
	    iotPanel.setBounds(2, 2, 870, 550);
	    iotPanel.setMaximumSize(new Dimension(875,550));
		iotPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "IoT Devices"));
		
		
		
		int xpos=10;
		int ypos=15;
		
		for(int i=0;i<n;i++)
		{
			iot[i] = new IoTDevice("["+Integer.toString(i)+"]",m, delta1);
			if(n<=100)
			{ 
				//iot[i].setIcon(new ImageIcon(getClass().getResource("/icons/"+rnd.nextInt(45)+".png")));
				iot[i].setBounds(xpos, ypos, 102, 35);				
				ypos+=38;
				if (ypos>530){xpos+=105;ypos=15;}
			}
			else
			{
				iot[i].setBounds(xpos, ypos, 70, 20);	
				ypos+=23;
				if (ypos>530){xpos+=73;ypos=15;}
			}
			iot[i].setEnabled(false);
			iot[i].addActionListener(this);
			iotPanel.add(iot[i]);
		}		
		pmqFrame.getContentPane().add(iotPanel);
		//IoT Devices - END
		
		
		
		//Fog Device - START
		JPanel fogPanel = new JPanel();
		fogPanel.setLayout(null);
		fogPanel.setBounds(877,2,158,550);
		fogPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Fog Device"));		
	
		fog =  new FogDevice();
		fog.setText("Fog Device");
		fog.setHorizontalTextPosition(SwingConstants.CENTER);
		fog.setVerticalTextPosition(SwingConstants.BOTTOM);
	    fog.setBounds(7, 15, 144, 150);
	    fog.setIcon(new ImageIcon(getClass().getResource("/icons/fd.png")));
		
		fog.addActionListener(this);
		fogPanel.add(fog);
		pmqFrame.getContentPane().add(fogPanel);
		//Fog Device - END
		
		
		
		//Control Center - START
		JPanel ccPanel = new JPanel();
		ccPanel.setLayout(null);
		ccPanel.setBounds(1040, 2, 158, 550);
		ccPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Control Center"));		
	
		cc =  new ControlCenter();
		cc.setText("Control Center");
		cc.setHorizontalTextPosition(SwingConstants.CENTER);
		cc.setVerticalTextPosition(SwingConstants.BOTTOM);
	    cc.setBounds(7, 15, 144, 150);
	    cc.setIcon(new ImageIcon(getClass().getResource("/icons/cc.png")));

		cc.addActionListener(this);
		ccPanel.add(cc);
		pmqFrame.getContentPane().add(ccPanel);
		//Control Center - END
		
		
		
		//Report Status - START
		JPanel rptPanel = new JPanel();
		
		rptPanel.setLayout(null);
		rptPanel.setBounds(2,555,1196,190);
		rptPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Status"));
		rpt = new JTextArea();
		//rpt.setText("Status\nStatus");
		DefaultCaret caret = (DefaultCaret)rpt.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		rpt.setBounds(10, 15, 1176, 160);
		rpt.setFont(new Font("Tahoma", Font.PLAIN,11));
		JScrollPane scrollPane = new JScrollPane(rpt);
		scrollPane.setBounds(10,15,1176,160);

		rptPanel.add(scrollPane);
		//pmq.getContentPane().add(iotPannel,BorderLayout.CENTER);
					
		pmqFrame.getContentPane().add(rptPanel);
		//Report Status - END
		
		rpt.setText("PMQ Initializing ... -»> \n");
		clock1.start();
		
	}

	
}
