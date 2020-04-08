import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class GameState {

	// private Board board;
	private JFrame frame;
	private PowerGridPanel panel;
	private int phase;
	//
	// - playerList : ArrayList<Player>
	private Player[] players;

	public GameState() {

		players = new Player[4];
		frame = new JFrame("Power Grid");
		panel = new PowerGridPanel();
		frame.setSize(1280, 700);
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("src/images/ui/factory.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame.setIconImage(img);
		frame.add(panel);
		frame.setVisible(true);
	}

	public void nextPhase() {

	}

	public void nextTurn() {

	}

	public void determineOrder() {

	}

	public void auction() {

	}

	public void buyCities() {

	}

	public void buyResources() {

	}

	public void endRound() {

	}

	public void nextStep() {

	}

	public String[] chosenAreas() {
		return null;
	}
}
