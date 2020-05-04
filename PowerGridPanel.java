import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.imageio.ImageIO;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static java.lang.System.out;

public class PowerGridPanel extends JPanel {
	
	private int w, h, bid;
	private GameState gs;
	private BufferedImage frame, emptyMap, darkMap, startScreen, downArrow;
	private HashMap<String, BufferedImage> houses; //used for showing house image from string
	private Font f1;
	private boolean firstRound; 
	private HashMap<Integer, BufferedImage> plants;
	private PowerPlant auctionPlant;
	private JTextField offer;
	private JButton offerB, no;
	private JLabel baka, currentBid;
	private LinkedHashMap<JButton, PowerPlant> plantMarketButtons;
	
	public PowerGridPanel(int w, int h, GameState gs) {
		houses = new HashMap<>();
		this.w = w;
		this.h = h;
		this.gs = gs;
		plants = new HashMap<>();
		plantMarketButtons = new LinkedHashMap<>();
		
		setBackground(Color.WHITE);
		makeImages();
		firstRound = true;
		makeAuctionButtons();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int phase = gs.getPhase();
		if(phase == 1) {
			if(firstRound)
				gs.randomizePlayers();
			else
				gs.determineOrder();
			gs.setPhase(2);
			repaint();
		}
		else if(phase == 2) {
			fillScreen(g, darkMap);
			drawPlayers(g);
			ArrayList<PowerPlant> marketPlants = gs.getMarketPlants();
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 4; j++) {
					int cp = marketPlants.get(j+(i*4)).getNum(); //currentPlant 
					g.drawImage(plants.get(cp), 310+175*j, 125+200*i, 150, 150, null);
				}
			}
			addAuctionButtons();
			
			
		}
		else if(phase == 3) {
			
		}
		else if(phase == 4) {
			
		}
		else if(phase == 5) {
			
			firstRound = false;
		}
		g.drawImage(frame, 0, 0, w, h, null);
	}
	
	private void makeAuctionButtons() {
		
		ArrayList<PowerPlant> ppMarket = gs.getMarketPlants();
		int bidround = 0;
		
		baka = new JLabel("Pick a Power Plant to bid for it!");
		baka.setFont(f1);
		baka.setForeground(Color.WHITE);
		baka.setHorizontalAlignment(baka.CENTER);
		
		currentBid = new JLabel("Current Bid: " + bid);
		currentBid.setFont(f1.deriveFont(24f));
		currentBid.setForeground(Color.WHITE);
		currentBid.setHorizontalAlignment(baka.CENTER);
		
		offer = new JTextField("Enter Bid");
		offer.setHorizontalAlignment(offer.CENTER);
		offer.setFont(f1);
		
		offerB = new JButton("Bid");
		offerB.setFont(f1);
		
		offerB.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ButtonModel m = (ButtonModel) e.getSource();
				if (m.isPressed()) {
					String text = offer.getText();
					try {
						bid = Integer.parseInt(text);
						currentBid.setText("Current Bid: " + bid);
						gs.nextTurn();
						repaint();
					} catch(Exception e1) {
						offer.setText("Bid must be an Integer");
					}
				}
			}
		});
		no = new JButton("Pass");
		no.setFont(f1);
		
		no.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ButtonModel m = (ButtonModel) e.getSource();
				if (m.isPressed()) {
					if(firstRound && gs.getCurrentPlayer().equals(gs.getFirstPlayer()) && bidround == 0) {
						baka.setText("First Player Must Bid!");
						repaint();
					}
					else {
						gs.nextTurn();
						offer.setText("Enter Bid");
						gs.passBid();
						repaint();
					}
				}
			}
		});
		
		
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 4; j++) {
				JButton ppButton = new JButton();
				
				ppButton.setFocusPainted(false);
				ppButton.setOpaque(false);
				ppButton.setContentAreaFilled(false);
				//ppButton.setBorderPainted(false);
				plantMarketButtons.put(ppButton, ppMarket.get(j+(i*4)));
				final int col = j, row = i;
				ppButton.getModel().addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						ButtonModel m = (ButtonModel) e.getSource();
						if(m.isPressed()) {
							if(row == 1) {
								baka.setText("Can't Bid For Second Row!");
								repaint();
							}
							else {
								baka.setText("Bidding for " + ppMarket.get(col+(row*4)));
								auctionPlant = ppMarket.get(col);
								out.println(auctionPlant);
							}
						}
					}
				});
				
				
			}
		}
		
	}
	private void addAuctionButtons() {
		offerB.setBounds(w/2-100, h/2+225, 98, 50);
		offer.setBounds(w/2-100, h/2+170, 200, 50);
		no.setBounds(w/2+2, h/2+225, 98, 50);
		baka.setBounds(w/2-75, h/2+270, 150, 40);
		currentBid.setBounds(w/2-75, 70, 150, 40);
		add(offer);
		add(offerB);
		add(no);
		add(baka);
		add(currentBid);
		
		Iterator<JButton> iter = plantMarketButtons.keySet().iterator();
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 4; j++) {
				JButton b = iter.next();
				b.setBounds(310+175*j, 125+200*i, 150, 150);
				add(b);
			}
		}
	}
	private void fillScreen(Graphics g, BufferedImage b) {
		g.drawImage(b, 38, 29, w-77, h-58, null);
	}
	
	private void drawPlayers(Graphics g) {
		Font font = f1.deriveFont(30f);
		g.setColor(Color.WHITE);
		g.setFont(font);
		ArrayList<Player> players = gs.getPlayers();
		g.setColor(Color.WHITE);
		for(int i = 0; i < 4; i++) {
			Player p = players.get(i);
			g.drawImage(houses.get(p.getColor()), 120 + 45*(i), h-177, 35, 35, null);
			g.drawString(Integer.toString(p.getNumber()), 130 + 45*(i), h-150);
			//out.println(gs.getCurrentPlayer());
			if(p.equals(gs.getCurrentPlayer()))
				g.drawImage(downArrow, 125 + 45*(i), h-195, 24, 15, null);
		}
		g.drawString("Order: ", 50, h-150);
		g.drawString("Elektros: " + gs.getCurrentPlayer().getElektros(), w-180, h-50);
	}
	
	//makes the images so no clutter in also font
	private void makeImages() {
		try {
			frame = ImageIO.read(getClass().getResourceAsStream("images/ui/boardFrame.png"));
			///make the font
			f1 = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("fonts/FetteEngschrift.TTF")).deriveFont(16f);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		    ge.registerFont(f1);
		    ///
			houses.put("black", ImageIO.read(getClass().getResourceAsStream("images/houses/black.png")));
			houses.put("blue", ImageIO.read(getClass().getResourceAsStream("images/houses/blue.png")));
			houses.put("green", ImageIO.read(getClass().getResourceAsStream("images/houses/green.png")));
			houses.put("purple", ImageIO.read(getClass().getResourceAsStream("images/houses/purple.png")));
			houses.put("red", ImageIO.read(getClass().getResourceAsStream("images/houses/red.png")));
			houses.put("yellow", ImageIO.read(getClass().getResourceAsStream("images/houses/yellow.png")));
			
			darkMap = ImageIO.read(getClass().getResourceAsStream("images/maps/map3.jpg"));
			emptyMap = ImageIO.read(getClass().getResourceAsStream("images/maps/USA_MAP_3.jpg"));
			startScreen = ImageIO.read(getClass().getResourceAsStream("images/ui/startScreen.jpg"));
			downArrow = ImageIO.read(getClass().getResourceAsStream("images/ui/downArrow.png"));
			
			for(int i = 3; i < 40; i++) 
				plants.put(i, ImageIO.read(getClass().getResourceAsStream("images/powerplants/" + i + ".png")));
			
			for(int i = 42; i < 50; i+=2) {
				if(i!=48)
					plants.put(i, ImageIO.read(getClass().getResourceAsStream("images/powerplants/" + i + ".png")));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		}
		///
	}
}