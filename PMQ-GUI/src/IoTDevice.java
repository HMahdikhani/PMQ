import java.util.Random;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.util.Vector;

public class IoTDevice extends JButton {
  private static final long serialVersionUID = 1L;	
  private String IoTDeviceID;
  private Vector<Integer> alfa;
  private int m; //number of element in vector alfa
  private int delta1; //each vectorm element range <delta2<
  public IoTDevice(String ID, int no, int d)
  {
	  setText(ID);
	  IoTDeviceID=ID;
	  m=no;
	  alfa = new Vector<Integer>(m);
	  delta1 = d;
	  //setSize(60,40);
	  //setBorder(null);
	  //setPreferredSize(new Dimension(60, 40));
	  //setIcon(new ImageIcon(getClass().getResource("/icons/2.png")));
	 
	 
	  setHorizontalTextPosition(SwingConstants.RIGHT);
	  setVerticalTextPosition(SwingConstants.CENTER);
  }
  public void GenerateVecAlfa()
  {
	  alfa.clear();
	  Random rand = new Random();
	  for (int i=0;i<m;i++)
	  {
		  alfa.add(rand.nextInt(delta1));
		  }
	 //System.out.println(alfa.toString());
	  
  }
  public Vector<Integer> GetAlfa()
  {
	  return alfa;
  }
  public String getIoTDeviceStatus()
  {
	  return "My Current Status is" + IoTDeviceID;
  }
}
