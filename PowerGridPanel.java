import java.awt.Graphics;

import javax.swing.JPanel;

public class PowerGridPanel extends JPanel {
	
	private int phase;
	private int w, h;
	private GameState gs;
	
	public PowerGridPanel(int w, int h, GameState gs) {
		phase = 0;
		this.w = w;
		this.h = h;
		this.gs = gs;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(phase == 0) {
			gs.getPlayers();
		}
		else if(phase == 1) {
			
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