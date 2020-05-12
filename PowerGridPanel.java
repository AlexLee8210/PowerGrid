import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

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
	
	private int w, h, prevPhase;
	private GameState gs;
	private BufferedImage frame, emptyMap, darkMap, downArrow, menuBg, usaMap;
	private HashMap<String, BufferedImage> houses, resources; //used for showing image from string
	private Font f1;
	//private boolean firstRound; //used in auction
	//auction stuff
	private HashMap<Integer, BufferedImage> plants;
	private PowerPlant auctionPlant;
	private JTextField offer;
	private JButton offerB, no, pp1, pp2, pp3;
	private JLabel auctionText, currentBid;
	private LinkedHashMap<JButton, PowerPlant> plantMarketButtons;
	private boolean bidWin, tooManyPP, win;
	private Player bidWinner;
	private int bid, bidRound;
	//aucton stuff end
	//ResourceMarket
	private JLabel marketText;
	private JButton rpp1, rpp2, rpp3;
	private PowerPlant rPP;
	private String ppType;
	private JButton nextPhase3, checkPrice, buyResource;
	private JTextField[] buy;
	//ResourceMarketEnd
	//CityBuilding start
	private HashMap<City, JButton> cityButtons;
	private JLabel cityText;
	private City currentCity, startCity, destCity;
	private JButton buyCity, resetCities, nextPhase4;
	//CityBuilding end
	//phase5 start
	private int[] playertempelektros;
	private JButton nextPhase5;
	//phase5 end
	public PowerGridPanel(int w, int h, GameState gs) {
		win = false;
		houses = new HashMap<>();
		prevPhase = 0;
		this.w = w;
		this.h = h;
		this.gs = gs;
		plants = new HashMap<>();
		resources = new HashMap<>();
		plantMarketButtons = new LinkedHashMap<>();
		setBackground(Color.WHITE);
		makeImages();
		
		makeAuctionButtons();
		auctionPlant = null;
		bidRound = 0;
		tooManyPP = false;
		rPP = null;
		ppType = null;
		makeResourceStuff();
		
		makeCityButtons();
		
		playertempelektros = new int[4];
		make5();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(win) {
			remove5();
			g.setFont(f1.deriveFont(24f));
			g.setColor(Color.WHITE);
			fillScreen(g, darkMap);
			g.drawImage(menuBg, w/2-300, h/2-200, 600, 400, null);
			//g.drawString(gs.getWinner() + " wins", w/2-120, h/2+50);
			TreeMap<Player, Integer> scoreBoard = gs.scoreBoard();
			int i = 0;
			for(Player p: scoreBoard.keySet()) {
				i++;
				g.drawString(i+". " + p + " with " + scoreBoard.get(p) + " powered cities, "
						+ p.getElektros() + " elektros, " + p.getCities().size() + " cities", w/2-220, 220+i*50);
			}
		}
		else {
			int phase = gs.getPhase();
			if(phase == 1) {
				if(gs.isFirstRound())
					gs.randomizePlayers();
				else {
					gs.determineOrder();
					remove5();
				}
				gs.nextPhase();
				out.println("phase:" + phase);
				prevPhase = 1;
				repaint();
			}
			else if(phase == 2) {
				out.println("PHASE@@@");
				if(prevPhase == 1) {
					addAuctionStuff();
				}
				out.println("phase2");
				drawPhase2(g);
				prevPhase = 2;
			}
			else if(phase == 3) {
				if(bidWin) {
					drawPhase2(g);
				}
				else {
					if(prevPhase == 2) {
						addResourceStuff();
						removeAuctionStuff();
						gs.determineReverseOrder();
					}
					drawPhase3(g);
					prevPhase = 3;
				}
			}
			else if(phase == 4) {
				if(prevPhase == 3) {
					removeResourceStuff();
					addCityButtons();
					gs.setTurn(0);
				}
				drawPhase4(g);
				prevPhase = 4;
			}
			else if(phase == 5) {
				if(prevPhase == 4) {
					removeCityButtons();
					add5();
				}
				drawPhase5(g);
				prevPhase = 5;
			}
		}	
		g.drawImage(frame, 0, 0, w, h, null);
		
	}
	///auctioning
	private void drawPhase2(Graphics g) {
		if(tooManyPP) {
			fillScreen(g, darkMap);
			g.setColor(Color.WHITE);
			g.setFont(f1.deriveFont(30f));
			ArrayList<Player> players = gs.getPlayers();
			for(int i = 0; i < 4; i++) {
				Player p = players.get(i);
				g.drawImage(houses.get(p.getColor()), 120 + 45*(i), h-177, 35, 35, null);
				g.drawString(Integer.toString(p.getNumber()), 130 + 45*(i), h-150);
				if(p.equals(bidWinner))
					g.drawImage(downArrow, 125 + 45*(i), h-195, 24, 15, null);
			}
			drawResourceMarket(g);
			g.setFont(f1.deriveFont(30f));
			g.drawString("Order: ", 50, h-150);
			g.drawString("Elektros: " + gs.getCurrentPlayer().getElektros(), w-180, h-50);
			g.drawString("Owned Power Plants: ", 50, 60);
			ArrayList<PowerPlant> cppp = gs.getCurrentPlayer().getPlants();
			g.setFont(f1.deriveFont(30f));
			for(int i = 0; i < cppp.size(); i++) {
				g.drawImage(plants.get(cppp.get(i).getNum()), 50, 80+i*110, 100, 100, null);
			}
			ArrayList<PowerPlant> marketPlants = gs.getMarketPlants();
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 4; j++) {
					int cp = marketPlants.get(j+(i*4)).getNum(); //currentPlant 
					g.drawImage(plants.get(cp), 310+175*j, 125+200*i, 150, 150, null);
					if(auctionPlant != null && auctionPlant.getNum() == cp)
						g.drawImage(downArrow, 369+175*j, 100+200*i, 32, 20, null);
				}
			}
			players = gs.getPlayers();
			g.setColor(new Color(0f, 0f, 0f, .5f));
			for(int i = 0; i < 4; i++)
				if(!gs.getCanBid().get(players.get(i)))
					g.fillRect(120 + 45*(i), h-177, 35, 36);
			offerB.setVisible(false);
			offer.setVisible(false);
			no.setVisible(false);
//			offerB.setBounds(w/2-100, h/2+225, 98, 50);
//			offer.setBounds(w/2-100, h/2+170, 200, 50);
//			no.setBounds(w/2+2, h/2+225, 98, 50);
			auctionText.setBounds(w/2-150, h/2+270, 300, 40);
			currentBid.setBounds(w/2-75, 70, 150, 40);
//			
//			g.drawImage(menuBg, w/2+2, h/2+225, 98, 50, null);
			Iterator<JButton> iter = plantMarketButtons.keySet().iterator();
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 4; j++) {
					JButton b = iter.next();
					b.setBounds(310+175*j, 125+200*i, 150, 150);
				}
			}
			g.setColor(Color.WHITE);
			g.setFont(f1.deriveFont(24f));
			g.drawImage(menuBg, w/2-150, h/2-75, 300, 150, null);
			g.drawString("You have too many Power Plants", w/2-115, h/2-5);	
			g.drawString("Pick a plant to remove", w/2-85, h/2+25);
			pp1.setBounds(50, 80, 100, 100);
			pp2.setBounds(50, 190, 100, 100);
			pp3.setBounds(50, 300, 100, 100);
		}
		else {
			g.setFont(f1.deriveFont(16f));
			g.setColor(Color.WHITE);
			
			fillScreen(g, darkMap);
			drawPlayers(g, false);
			drawResourceMarket(g);
			g.setFont(f1.deriveFont(30f));
			g.drawString("Owned Power Plants: ", 50, 60);
			ArrayList<PowerPlant> cppp = gs.getCurrentPlayer().getPlants();
			for(int i = 0; i < cppp.size(); i++) {
				g.drawImage(plants.get(cppp.get(i).getNum()), 50, 80+i*110, 100, 100, null);
			}
			ArrayList<PowerPlant> marketPlants = gs.getMarketPlants();
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 4; j++) {
					int cp = marketPlants.get(j+(i*4)).getNum(); //currentPlant 
					g.drawImage(plants.get(cp), 310+175*j, 125+200*i, 150, 150, null);
					if(auctionPlant != null && auctionPlant.getNum() == cp)
						g.drawImage(downArrow, 369+175*j, 100+200*i, 32, 20, null);
				}
			}
			ArrayList<Player> players = gs.getPlayers();
			g.setColor(new Color(0f, 0f, 0f, .5f));
			for(int i = 0; i < 4; i++)
				if(!gs.getCanBid().get(players.get(i)))
					g.fillRect(120 + 45*(i), h-177, 35, 36);
			offerB.setBounds(w/2-100, h/2+225, 98, 50);
			offer.setBounds(w/2-100, h/2+170, 200, 50);
			no.setBounds(w/2+2, h/2+225, 98, 50);
			auctionText.setBounds(w/2-150, h/2+270, 300, 40);
			currentBid.setBounds(w/2-75, 70, 150, 40);
			offerB.setVisible(true);
			offer.setVisible(true);
			no.setVisible(true);
			g.drawImage(menuBg, w/2-100, h/2+225, 98, 50, null);
			g.drawImage(menuBg, w/2+2, h/2+225, 98, 50, null);
			Iterator<JButton> iter = plantMarketButtons.keySet().iterator();
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 4; j++) {
					JButton b = iter.next();
					b.setBounds(310+175*j, 125+200*i, 150, 150);
				}
			}
			if(bidWin) {
				bid = 0;
				currentBid.setText("Current Bid: " + bid);
				g.setFont(f1.deriveFont(30f));
				bidWin = false;
				bidRound = 0;
				
				if(bidWinner.getNumPlants() == 3) {
					tooManyPP = true;
					drawPhase2(g);
				}
				else {
					bidWinner.addPlant(auctionPlant);
					auctionText.setText(bidWinner.toString() + " gets Power Plant " + auctionPlant.getNum());
					auctionPlant = null;
					bidWinner = null;
				}
			}
		}
//		if(bidWin)
//			bidWin = false;

	}
	private void makeAuctionButtons() {
		ArrayList<PowerPlant> ppMarket = gs.getMarketPlants();
		
		pp1 = new JButton();
		pp1.setFocusPainted(false);
		pp1.setOpaque(false);
		pp1.setContentAreaFilled(false);
		//pp1.setBorderPainted(false);
		pp1.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ButtonModel m = (ButtonModel) e.getSource();
				if (m.isPressed() && tooManyPP) {
					bidWinner.removePlant(0);
					bidWinner.addPlant(auctionPlant);
					bidWinner = null;
					auctionPlant = null;
					tooManyPP = false;
					repaint();
				}
			}
		});
		pp2 = new JButton();
		pp2.setFocusPainted(false);
		pp2.setOpaque(false);
		pp2.setContentAreaFilled(false);
		//pp2.setBorderPainted(false);
		pp2.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ButtonModel m = (ButtonModel) e.getSource();
				if (m.isPressed() && tooManyPP) {
					bidWinner.removePlant(1);
					bidWinner.addPlant(auctionPlant);
					bidWinner = null;
					auctionPlant = null;
					tooManyPP = false;
					repaint();
				}
			}
		});
		
		pp3 = new JButton();
		pp3.setFocusPainted(false);
		pp3.setOpaque(false);
		pp3.setContentAreaFilled(false);
		//pp3.setBorderPainted(false);
		pp3.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ButtonModel m = (ButtonModel) e.getSource();
				if (m.isPressed() && tooManyPP) {
					bidWinner.removePlant(2);
					bidWinner.addPlant(auctionPlant);
					bidWinner = null;
					auctionPlant = null;
					tooManyPP = false;
					repaint();
				}
			}
		});

		auctionText = new JLabel("Pick a Power Plant to bid for it!");
		auctionText.setFont(f1.deriveFont(20f));
		auctionText.setForeground(Color.WHITE);
		auctionText.setHorizontalAlignment(auctionText.CENTER);
		
		currentBid = new JLabel("Current Bid: " + bid);
		currentBid.setFont(f1.deriveFont(30f));
		currentBid.setForeground(Color.WHITE);
		currentBid.setHorizontalAlignment(auctionText.CENTER);
		
		offer = new JTextField("Enter Bid");
		offer.setHorizontalAlignment(offer.CENTER);
		offer.setFont(f1);
		
		offerB = new JButton("Bid");
		offerB.setFont(f1);
		offerB.setFocusPainted(false);
		offerB.setOpaque(false);
		offerB.setContentAreaFilled(false);
		offerB.setBorderPainted(false);
		offerB.setForeground(Color.WHITE);
		offerB.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ButtonModel m = (ButtonModel) e.getSource();
				if (m.isPressed()) {
					String text = offer.getText();
					if(auctionPlant != null) {
						try {
							int tempBid = Integer.parseInt(text);
							if(tempBid > bid) {
								if(tempBid >= auctionPlant.getNum()) {
									if(tempBid > gs.getCurrentPlayer().getElektros())
										auctionText.setText("You don't have enough Elektros!");
									else {
										bid = Integer.parseInt(text);
										currentBid.setText("Current Bid: " + bid);
										auctionText.setText(gs.getCurrentPlayer() + " bids " + text + " elektros");
										gs.nextTurn();
										gs.checkBidWinner();
										bidRound++;
									}
								}
								else
									auctionText.setText("Bid must be greater than the power plant size!");
							}
							else {
								auctionText.setText("Bid must be greater than the current bid!");
							}
							
						} catch(Exception e1) {
							auctionText.setText("Bid must be an Integer");
						}
					}
					else {
						auctionText.setText("Pick a Power Plant to bid for!");
					}
					repaint();
				}
			}
		});
		no = new JButton("Pass");
		no.setForeground(Color.WHITE);
		no.setFont(f1);
		no.setFocusPainted(false);
		no.setOpaque(false);
		no.setContentAreaFilled(false);
		no.setBorderPainted(false);
		no.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ButtonModel m = (ButtonModel) e.getSource();
				if (m.isPressed()) {
					if(gs.isFirstRound() && gs.getCurrentPlayer().equals(gs.getAuctionStartPlayer()) && bidRound == 0) {
						auctionText.setText("First Player Must Bid!");
					}
					else {
						auctionText.setText(gs.getCurrentPlayer().toString() + " passes!");
						gs.passBid();
						gs.nextTurn();
						bidRound++;
						gs.checkBidWinner();
					}
					repaint();
				}
			}
		});
		
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 4; j++) {
				JButton ppButton = new JButton();
				
				ppButton.setFocusPainted(false);
				ppButton.setOpaque(false);
				ppButton.setContentAreaFilled(false);
				ppButton.setBorderPainted(false);
				plantMarketButtons.put(ppButton, ppMarket.get(j+(i*4)));
				final int col = j, row = i;
				ppButton.getModel().addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						ButtonModel m = (ButtonModel) e.getSource();
						if(m.isPressed()) {
							if(row == 1) {
								auctionText.setText("Can't Bid For Second Row!");
							}
							else {
								if(gs.getAuctionStartPlayer().equals(gs.getCurrentPlayer()) && bidRound == 0) {
									auctionText.setText("Bidding for Power Plant: " + ppMarket.get(col+(row*4)));
									auctionPlant = ppMarket.get(col);
								}
								else {
									auctionText.setText("You must Bid or Pass for Power Plant " + auctionPlant);
								}
							}
						}
					}
				});
				
				
			}
		}
		
		offerB.setBounds(w/2-100, h/2+225, 98, 50);
		offer.setBounds(w/2-100, h/2+170, 200, 50);
		no.setBounds(w/2+2, h/2+225, 98, 50);
		auctionText.setBounds(w/2-150, h/2+270, 300, 40);
		currentBid.setBounds(w/2-75, 70, 150, 40);
		addAuctionStuff();
	}
	public int getBid() {
		return bid;
	}
	public void setBidder(Player p) {
		bidWin = true;
		bidWinner = p;
	}
	public PowerPlant getAuctionPlant() {
		return auctionPlant;
	}
	public void removeAuctionPlant() {
		auctionPlant = null;
	}
	public void addAuctionStuff() {
		add(offerB);
		add(offer);
		add(no);
		add(auctionText);
		add(currentBid);
		add(pp1);
		add(pp2);
		add(pp3);
		Iterator<JButton> iter = plantMarketButtons.keySet().iterator();
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 4; j++) {
				JButton b = iter.next();
				add(b);
			}
		}			
	}
	public void removeAuctionStuff() {
		remove(offerB);
		remove(offer);
		remove(no);
		remove(auctionText);
		remove(currentBid);
		remove(pp1);
		remove(pp2);
		remove(pp3);
		Iterator<JButton> iter = plantMarketButtons.keySet().iterator();
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 4; j++) {
				JButton b = iter.next();
				remove(b);
			}
		}	
	}
	///auctioning end
	///resource buying start (Phase 3)
	private void drawPhase3(Graphics g) {
		g.setFont(f1.deriveFont(30f));
		g.setColor(Color.WHITE);
		fillScreen(g, darkMap);
		g.drawImage(frame, 0, 0, w, h, null);
		g.drawString("Power Plants: ", w/2-20, 60);
		Player cp = gs.getCurrentPlayer();
		int i = 0;
		g.setFont(f1.deriveFont(24f));
		for(PowerPlant pp: cp.getPlants()) {
			g.drawImage(plants.get(pp.getNum()), w/2-250+175*i, 80, 150, 150, null);
			if(ppType!=null && !ppType.equals("hybrid"))
				g.drawString("Num Mats: " + pp.getMats(), w/2-230+175*i, h/2-90);
			else if(ppType!=null) {
				g.drawString("Num Coal: " + pp.getNumCoal(), w/2-230+175*i, h/2-90);
				g.drawString("Num Oil: " + pp.getNumOil(), w/2-230+175*i, h/2-75);
			}
			i++;
		}
		drawPlayers(g, true);
		Font font = f1.deriveFont(30f);
		g.setColor(Color.WHITE);
		g.setFont(font);
		TreeMap<String, Integer> resourceMarket = gs.getResourceMarket();
		g.setFont(font.deriveFont(30f));
		int size = 100;
		g.drawImage(resources.get("coal"), 310, h/2, size, size, null);
		g.drawString("Coal: " + resourceMarket.get("coal"), 320, h/2-10);
		g.drawImage(resources.get("garbage"), 385+size, h/2, size, size, null);
		g.drawString("Garbage: " + resourceMarket.get("garbage"), 390+size, h/2-10);
		g.drawImage(resources.get("oil"), 460+size*2, h/2, size, size, null);
		g.drawString("Oil: " + resourceMarket.get("oil"), 475+size*2, h/2-10);
		g.drawImage(resources.get("uranium"), 535+size*3, h/2, size, size, null);
		g.drawString("Uranium: " + resourceMarket.get("uranium"), 535+size*3, h/2-10);
		g.drawImage(menuBg, w-200, h-125, 150, 50, null);
		g.drawImage(menuBg, w-355, h-125, 150, 50, null);
		g.drawImage(menuBg, w-510, h-125, 150, 50, null);
		rpp1.setBounds( w/2-250, 80, 150, 150);
		if(cp.getNumPlants() == 2) {
			rpp2.setBounds( w/2-75, 80, 150, 150);
			rpp2.setVisible(true);
			rpp3.setVisible(false);
		}
		if(cp.getNumPlants() == 3) {
			rpp3.setBounds( w/2+100, 80, 150, 150);
			rpp3.setVisible(true);
			rpp2.setVisible(true);
		}
		setResourceBounds();
		if(rPP!=null) {
			if(ppType.equals("coal")) {
				buy[0].setBounds(300, h/2+115, 100, 50);
				buy[0].setVisible(true);
				buy[1].setVisible(false);
				buy[2].setVisible(false);
				buy[3].setVisible(false);
			}
			else if(ppType.equals("garbage")) {
				buy[1].setBounds(475, h/2+115, 100, 50);
				buy[1].setVisible(true);
				buy[0].setVisible(false);
				buy[2].setVisible(false);
				buy[3].setVisible(false);
			}
			else if(ppType.equals("oil")) {
				buy[2].setBounds(650, h/2+115, 100, 50);
				buy[2].setVisible(true);
				buy[1].setVisible(false);
				buy[0].setVisible(false);
				buy[3].setVisible(false);
			}
			else if(ppType.equals("uranium")) {
				buy[3].setBounds(825, h/2+115, 100, 50);
				buy[3].setVisible(true);
				buy[1].setVisible(false);
				buy[2].setVisible(false);
				buy[0].setVisible(false);
			}
			else if(ppType.equals("hybrid")) {
				buy[0].setBounds(310, h/2+115, 100, 50);
				buy[2].setBounds(650, h/2+115, 100, 50);
				buy[0].setVisible(true);
				buy[2].setVisible(true);
				buy[1].setVisible(false);
				buy[3].setVisible(false);
			}
		}
	}
	private void makeResourceStuff() {
		marketText = new JLabel("Pick a Power Plant to buy Resources for");
		marketText.setForeground(Color.WHITE);
		marketText.setFont(f1.deriveFont(24f));
		marketText.setHorizontalAlignment(marketText.CENTER);
		
		rpp1 = new JButton();
		rpp1.setFocusPainted(false);
		rpp1.setOpaque(false);
		rpp1.setContentAreaFilled(false);
		//rpp1.setBorderPainted(false);
		rpp1.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ButtonModel m = (ButtonModel) e.getSource();
				if (m.isPressed()) {
					Player cp = gs.getCurrentPlayer();
					if(rPP!=null && !rPP.equals(cp.getPlants().get(0)))
						for(JTextField tf: buy) {
							tf.setVisible(false);
							tf.setText("");
						}
					rPP = cp.getPlants().get(0);
					ppType = rPP.getType();
					repaint();
				}
			}
		});
		rpp2 = new JButton();
		rpp2.setFocusPainted(false);
		rpp2.setOpaque(false);
		rpp2.setContentAreaFilled(false);
		//rpp2.setBorderPainted(false);
		rpp2.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ButtonModel m = (ButtonModel) e.getSource();
				if (m.isPressed()) {
					Player cp = gs.getCurrentPlayer();
					if(rPP!=null && !rPP.equals(cp.getPlants().get(1)))
						for(JTextField tf: buy) {
							tf.setVisible(false);
							tf.setText("");
						}
					try {
						rPP = cp.getPlants().get(1);
						ppType = rPP.getType();
					} catch(Exception e1) {}
					repaint();
				}
			}
		});
		rpp3 = new JButton();
		rpp3.setFocusPainted(false);
		rpp3.setOpaque(false);
		rpp3.setContentAreaFilled(false);
		//rpp3.setBorderPainted(false);
		rpp3.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ButtonModel m = (ButtonModel) e.getSource();
				if (m.isPressed()) {
					Player cp = gs.getCurrentPlayer();
					if(rPP!=null && !rPP.equals(cp.getPlants().get(2)))
						for(JTextField tf: buy) {
							tf.setVisible(false);
							tf.setText("");
						}
					try {
						rPP = cp.getPlants().get(2);
						ppType = rPP.getType();
					} catch(Exception e1) {}
					repaint();
				}
			}
		});
		
		nextPhase3 = new JButton("Next Turn");
		nextPhase3.setFont(f1.deriveFont(20f));
		nextPhase3.setForeground(Color.WHITE);
		nextPhase3.setFocusPainted(false);
		nextPhase3.setOpaque(false);
		nextPhase3.setContentAreaFilled(false);
		nextPhase3.setBorderPainted(false);
		nextPhase3.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ButtonModel m = (ButtonModel) e.getSource();
				if(m.isPressed()) {
					gs.nextTurn();
					for(JTextField tf: buy) {
						tf.setVisible(false);
						tf.setText("");
					}
					rPP = null;
					ppType = null;
					repaint();
				}
			}
		});
		
		checkPrice = new JButton("Check Prices");
		checkPrice.setFont(f1.deriveFont(20f));
		checkPrice.setForeground(Color.WHITE);
		checkPrice.setFocusPainted(false);
		checkPrice.setOpaque(false);
		checkPrice.setContentAreaFilled(false);
		checkPrice.setBorderPainted(false);
		checkPrice.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ButtonModel m = (ButtonModel) e.getSource();
				if (m.isPressed()) {
					if(rPP!=null) {
						int amt = 0, num = 0;
						TreeMap<String, Integer> resourceMarket = gs.getResourceMarket();
						String[] res = {"coal", "garbage", "oil", "uranium"};
						for(int i = 0; i < 4; i++) {
							JTextField tf = buy[i];
							try {
								num = Integer.parseInt(tf.getText());
								for(int j = 0; j < num; j++) {
									amt += gs.calcCost(ppType, resourceMarket.get(res[i])-j);
								}
							} catch (Exception e1) {}
						}
						marketText.setText("This would cost: " + amt + " elektros.");
					}
				}
			}
		});
		
		buyResource = new JButton("Buy Resources");
		buyResource.setFont(f1.deriveFont(20f));
		buyResource.setForeground(Color.WHITE);
		buyResource.setFocusPainted(false);
		buyResource.setOpaque(false);
		buyResource.setContentAreaFilled(false);
		buyResource.setBorderPainted(false);
		buyResource.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ButtonModel m = (ButtonModel) e.getSource();
				if (m.isPressed()) {
					if(rPP!=null) {
						int amt = 0, num = 0, tempNum = 0;
						TreeMap<String, Integer> resourceMarket = gs.getResourceMarket();
						String[] res = {"coal", "garbage", "oil", "uranium"};
						for(int i = 0; i < 4; i++) {
							JTextField tf = buy[i];
							try {
								num = Integer.parseInt(tf.getText());
								for(int j = 0; j < num; j++) {
									amt += gs.calcCost(ppType, resourceMarket.get(res[i])-j);
								}
								if(i == 0)
									tempNum = num;
							} catch (Exception e1) {}
						}
						if(amt > gs.getCurrentPlayer().getElektros()) {
							marketText.setText("You don't have enough elektros!");
						}
						else if(num > rPP.materialsTillFull()) {
							marketText.setText("The Power Plant can't hold this much!");
						}
						else {
							gs.getCurrentPlayer().buy(amt);
							
							if(ppType!=null && ppType.equals("hybrid")) {
								gs.getResourceMarket().put("coal", gs.getResourceMarket().get("coal")-tempNum);
								gs.getResourceMarket().put("oil", gs.getResourceMarket().get("oil")-num);
								rPP.addMaterialHybrid(tempNum, num);
							}
							else {
								gs.getResourceMarket().put(ppType, gs.getResourceMarket().get(ppType)-num);
								rPP.addMaterial(num);
							}
						}
						repaint();
					}
				}
			}
		});
		
		buy = new JTextField[4];
		for(int i = 0; i < 4; i++) {
			buy[i] = new JTextField("");
		}
	}
	private void addResourceStuff() {
		add(marketText);
		add(rpp1);
		add(rpp2);
		add(rpp3);
		add(nextPhase3);
		add(checkPrice);
		add(buyResource);
		for(JTextField tf: buy)
			add(tf);
		setResourceBounds();
	}
	private void removeResourceStuff() {
		remove(marketText);
		remove(rpp1);
		remove(rpp2);
		remove(rpp3);
		remove(nextPhase3);
		remove(checkPrice);
		remove(buyResource);
		for(JTextField tf: buy)
			remove(tf);
	}

	private void setResourceBounds() {
//		for(int i = 0; i < 8; i++) {
//			if(i%2 == 0)
//				rCostButtons[i].setBounds();
//		}
//		rpp1.setBounds( w/2-250, 80, 150, 150);
//		rpp2.setBounds( w/2-425, 80, 150, 150);
//		rpp2.setBounds( w/2-600, 80, 150, 150);
		marketText.setBounds(440, h/2-75, 400, 50);
		nextPhase3.setBounds(w-200, h-125, 150, 50);
		checkPrice.setBounds(w-355, h-125, 150, 50);
		buyResource.setBounds(w-510, h-125, 150, 50);
	}
	///resource buying end
	///cityBuilding start
	private void drawPhase4(Graphics g) {
		g.setFont(f1.deriveFont(30f));
		g.setColor(Color.WHITE);
		fillScreen(g, usaMap);
		g.drawImage(frame, 0, 0, w, h, null);
		drawPlayers(g, false);
		drawResourceMarket(g);
		g.drawImage(menuBg, w-200, h-125, 150, 50, null);
		g.drawImage(menuBg, w-200, h-180, 150, 50, null);
		g.drawImage(menuBg, w-200, h-235, 150, 50, null);
		if(!gs.isFirstRound() && startCity == null)
			cityText.setText("Pick one of your cities to start from");
		for(City c: gs.getCurrentPlayer().getCities()) {
			cityButtons.get(c).setBackground(Color.GREEN);
		}
		for(Player pl: gs.getPlayers()) {
			if(!pl.equals(gs.getCurrentPlayer()))
				for(City c: pl.getCities()) {
					cityButtons.get(c).setBackground(Color.RED);
				}
		}
		setCityBounds();
	}
	private void makeCityButtons() {
		cityText = new JLabel("Pick a city to buy");
		cityText.setForeground(Color.WHITE);
		cityText.setFont(f1.deriveFont(24f));
		cityText.setHorizontalAlignment(cityText.CENTER);
		
		buyCity = new JButton("Buy City");
		buyCity.setForeground(Color.WHITE);
		buyCity.setFocusPainted(false);
		buyCity.setBorderPainted(false);
		buyCity.setContentAreaFilled(false);
		buyCity.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ButtonModel m = (ButtonModel) e.getSource();
				if (m.isPressed()) {
					Player p = gs.getCurrentPlayer();
					if(gs.isFirstRound() && p.getCities().size() == 0) {
						if(currentCity != null)
							if(p.getElektros() >= currentCity.getCost()) {
								boolean canBuy = true;
								for(Player player: gs.getPlayers())
									if(player.getCities().contains(currentCity))
										canBuy = false;
								if(canBuy) {
									p.addCity(currentCity);
									p.buy(currentCity.getCost());
									currentCity.addOwner(p);
									currentCity = null;
									for(City c: gs.getCurrentPlayer().getCities()) {
										cityButtons.get(c).setBackground(Color.GREEN);
									}
									for(Player pl: gs.getPlayers()) {
										if(!pl.equals(gs.getCurrentPlayer()))
											for(City c: pl.getCities()) {
												cityButtons.get(c).setBackground(Color.RED);
											}
									}
									cityText.setText("Select a city to start from");
								}
								else
									cityText.setText("Someone else owns this city");
							}
							else
								cityText.setText("You don't have enough elektros");
						else
							cityText.setText("Select a city");
					}
					else {
						int pathCost = gs.getGraph().getShortestPathCost(startCity, destCity);
						if(pathCost != -1 && pathCost <= p.getElektros()) {
							if(p.getElektros() >= destCity.getCost()) {
								boolean canBuy = true;
								for(Player player: gs.getPlayers())
									if(player.getCities().contains(destCity)) {
										canBuy = false;
										cityText.setText("Someone else owns the city you want to buy");
										break;
									}
								if(canBuy) {
									p.addCity(currentCity);
									p.buy(currentCity.getCost());
									currentCity.addOwner(p);
									startCity = null;
									destCity = null;
									for(City c: gs.getCurrentPlayer().getCities()) {
										cityButtons.get(c).setBackground(Color.GREEN);
									}
									for(Player pl: gs.getPlayers()) {
										for(City c: pl.getCities()) {
											cityButtons.get(c).setBackground(Color.RED);
										}
									}
									if(gs.isWinner()) {
										win = true;
									}
								}
							}
							else
								cityText.setText("You don't have enough elektros");
						}
						else
							cityText.setText("You must select both a start and destination city");
					}
					repaint();
				}
			}
		});
		
		nextPhase4 = new JButton("Next Turn");
		nextPhase4.setFont(f1.deriveFont(20f));
		nextPhase4.setForeground(Color.WHITE);
		nextPhase4.setFocusPainted(false);
		nextPhase4.setOpaque(false);
		nextPhase4.setContentAreaFilled(false);
		nextPhase4.setBorderPainted(false);
		nextPhase4.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ButtonModel m = (ButtonModel) e.getSource();
				if(m.isPressed()) {
					if(gs.isFirstRound() && gs.getCurrentPlayer().getCities().size() == 0)
						cityText.setText("You must buy a city in the first round!");
					else {
						gs.nextTurn();
						currentCity = null;
						startCity = null;
						destCity = null;
					}
					repaint();
				}
			}
		});
		
		resetCities = new JButton("Reset Cities");
		resetCities.setForeground(Color.WHITE);
		resetCities.setFocusPainted(false);
		resetCities.setBorderPainted(false);
		resetCities.setContentAreaFilled(false);
		resetCities.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ButtonModel m = (ButtonModel) e.getSource();
				if (m.isPressed()) {
					if(gs.isFirstRound() && gs.getCurrentPlayer().getCities().size() == 0) {
						currentCity = null;
						cityText.setText("Reset selected cities");
					}
					else {
						startCity = null;
						destCity = null;
						cityText.setText("Reset selected cities");
					}
					repaint();
				}
			}
		});
		
		cityButtons = new HashMap<>();
		for(Entry<String, City> e: gs.getGraph().getCities().entrySet()) {
			JButton b = new JButton(/* e.getKey() */);
			b.setForeground(Color.WHITE);
			b.setBackground(Color.BLACK);
			final City c = e.getValue();
			b.getModel().addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					ButtonModel m = (ButtonModel) e.getSource();
					if (m.isPressed()) {
						if(gs.isFirstRound() && gs.getCurrentPlayer().getCities().size() == 0) {
							cityText.setText("This is " + c + ", it costs " + c.getCost() + " elektros");
							currentCity = c;
						}
						else {
							if(startCity == null) {
								if(gs.getCurrentPlayer().getCities().contains(c)) {
									startCity = c;
									cityText.setText("Start City set to " + c);
									
								}
								else
									cityText.setText("You don't own this city");
							}
							else {
								destCity = c;
								cityText.setText("Start city: " + startCity + " Destination City: " + c
										+ " Path Cost: " + gs.getGraph().getShortestPathCost(startCity, destCity));
							}
						}
						repaint();
					}
				}
			});
			cityButtons.put(e.getValue(), b);
		}
	}
	private void addCityButtons() {
		add(cityText);
		add(buyCity);
		add(resetCities);
		add(nextPhase4);
		for(Entry<City, JButton> e: cityButtons.entrySet()) {
			add(e.getValue());
		}
	}
	private void removeCityButtons() {
		for(Entry<City, JButton> e: cityButtons.entrySet()) {
			remove(e.getValue());
		}
		remove(buyCity);
		remove(cityText);
		remove(resetCities);
		remove(nextPhase4);
	}
	private void setCityBounds() {
		cityText.setBounds(390, h-75, 500, 50);
		nextPhase4.setBounds(w-200, h-125, 150, 50);
		buyCity.setBounds(w-200, h-180, 150, 50);
		resetCities.setBounds(w-200, h-235, 150, 50);
		for(Entry<City, JButton> e: cityButtons.entrySet()) {
			JButton b = e.getValue();
			b.setBounds((int)e.getKey().getPoint().getX(), (int)e.getKey().getPoint().getY(), 33, 33);
		}
	}
	///cityBuilding end
	///bureacracy start
	public void drawPhase5(Graphics g) {
		g.setFont(f1.deriveFont(24f));
		g.setColor(Color.WHITE);
		fillScreen(g, darkMap);
		g.drawImage(menuBg, w/2-300, h/2-200, 600, 400, null);
		ArrayList<Player> players = gs.getPlayers();
		for(int i = 0; i < 4; i++) {
			g.drawString(players.get(i) + " got " + (players.get(i).getElektros()-playertempelektros[i]) + " elektros."
					+ " They now have " + players.get(i).getElektros(), w/2-230, 250+i*50);
		}

		nextPhase5.setBounds(w-200, h-125, 100, 50);
		g.drawImage(menuBg, w-200, h-125, 100, 50, null);
	}
	public void setPlayerTempElektros(ArrayList<Player> players) {
		for(int i = 0; i < 4; i++)
			playertempelektros[i] = players.get(i).getElektros();
	}
	public void make5() {
		nextPhase5 = new JButton("Next Phase");
		nextPhase5.setForeground(Color.WHITE);
		nextPhase5.setFocusPainted(false);
		nextPhase5.setBorderPainted(false);
		nextPhase5.setContentAreaFilled(false);
		nextPhase5.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ButtonModel m = (ButtonModel) e.getSource();
				if (m.isPressed()) {
					gs.nextPhase();
					win = true;
					repaint();
				}
			}
		});
	}
	public void add5() {
		add(nextPhase5);
	}
	public void remove5() {
		remove(nextPhase5);
	}
	///bureaucracy end
	private void fillScreen(Graphics g, BufferedImage b) {
		g.drawImage(b, 38, 29, w-77, h-58, null);
	}
	
	private void drawPlayers(Graphics g, boolean low) {
		int height = h-150;
		if(low)
			height = h-50;
		Font font = f1.deriveFont(30f);
		g.setColor(Color.WHITE);
		g.setFont(font);
		ArrayList<Player> players = gs.getPlayers();
		for(int i = 0; i < 4; i++) {
			Player p = players.get(i);
			g.drawImage(houses.get(p.getColor()), 120 + 45*(i), height-27, 35, 35, null);
			g.drawString(Integer.toString(p.getNumber()), 130 + 45*(i), height);
			if(p.equals(gs.getCurrentPlayer()))
				g.drawImage(downArrow, 125 + 45*(i), height-45, 24, 15, null);
		}
		g.drawString("Order: ", 50, height);
		g.drawString("Elektros: " + gs.getCurrentPlayer().getElektros(), w-180, h-50);
	}
	private void drawResourceMarket(Graphics g) {
		Font font = f1.deriveFont(30f);
		g.setColor(Color.WHITE);
		g.setFont(font);
		TreeMap<String, Integer> resourceMarket = gs.getResourceMarket();
		g.drawString("Resource Market:", 50, h-100);
		g.setFont(font.deriveFont(14f));
		
		g.drawImage(resources.get("coal"), 50, h-90, 35, 35, null);
		g.drawString("Coal: " + resourceMarket.get("coal"), 51, h-40);
		g.drawImage(resources.get("garbage"), 115, h-90, 35, 35, null);
		g.drawString("Garbage: " + resourceMarket.get("garbage"), 110, h-40);
		g.drawImage(resources.get("oil"), 180, h-90, 35, 35, null);
		g.drawString("Oil: " + resourceMarket.get("oil"), 184, h-40);
		g.drawImage(resources.get("uranium"), 245, h-90, 35, 35, null);
		g.drawString("Uranium: " + resourceMarket.get("uranium"), 238, h-40);
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
			
			resources.put("coal", ImageIO.read(getClass().getResourceAsStream("images/resources/coal.png")));
			resources.put("garbage", ImageIO.read(getClass().getResourceAsStream("images/resources/garbage.png")));
			resources.put("oil", ImageIO.read(getClass().getResourceAsStream("images/resources/oil.png")));
			resources.put("uranium", ImageIO.read(getClass().getResourceAsStream("images/resources/uranium.png")));
			
			usaMap = ImageIO.read(getClass().getResourceAsStream("images/maps/USA_MAP_2.jpg"));
			darkMap = ImageIO.read(getClass().getResourceAsStream("images/maps/map3.jpg"));
			emptyMap = ImageIO.read(getClass().getResourceAsStream("images/maps/USA_MAP_3.jpg"));
			usaMap = emptyMap;
			downArrow = ImageIO.read(getClass().getResourceAsStream("images/ui/downArrow.png"));
			menuBg = ImageIO.read(getClass().getResourceAsStream("images/ui/menuBg.png"));
			for(int i = 3; i <= 40; i++) 
				plants.put(i, ImageIO.read(getClass().getResourceAsStream("images/powerplants/" + i + ".png")));
			
			for(int i = 42; i <= 50; i+=2) {
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