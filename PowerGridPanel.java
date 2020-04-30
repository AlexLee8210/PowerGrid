import java.awt.Graphics;

import javax.swing.JPanel;

public class PowerGridPanel extends JPanel {
	
	private int phase;
	
	public PowerGridPanel() {
		phase = 0;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(phase == 1) {
			
		}
		else if(phase == 2) {
			
		}
		else if(phase == 3) {
			
		}
		else if(phase == 4) {
			
		}
		else if(phase == 5) {
			
		}
	}
	
	public void updatePhase(int p) {
		phase = p;
	}
}