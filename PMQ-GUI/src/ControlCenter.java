import javax.swing.JButton;

import java.util.Random;
import java.util.Vector;

public class ControlCenter extends JButton {
  private static final long serialVersionUID = 1L;
  private String ControlCenter;
  private int m = 10;
  private int delta1 = 100;
  public Vector<Integer> BetaVec = new Vector<Integer>(m);
  public ControlCenter()
  {
	  Random rand = new Random();
	  for(int i=1 ; i<=m; i++)
	  {
		  BetaVec.add(rand.nextInt() % delta1);
	  }
  }
  public Vector<Integer> GetBetaVect()
  {
     return BetaVec;
  }
  public String getControlCenterStatus()
  {
	  return "I am CC " + ControlCenter;
  }
}
