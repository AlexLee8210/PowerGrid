import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static java.lang.System.out;

public class StartPanel extends JPanel implements MouseListener {
	
	private BufferedImage darkMap, startScreen, playerSelect, rowBG, menuBG, frame;
	private BufferedImage blueRegion, brownRegion, greenRegion, purpleRegion, redRegion, yellowRegion;
	private int sw, sh;
	private JButton start, begin, startGame;
	private boolean startMenu, playerMenu, mapSelect;
	private JButton[] PButtons;
	private Player[] players;
	private NavigableMap<String, BufferedImage> houses;
	private LinkedHashMap<JButton, Boolean> regionBMap;
	private JButton[] regionB;
	
	public StartPanel(int screenw, int screenh, JFrame f, PowerGridPanel pgPanel) {
		regionBMap = new LinkedHashMap<>();
		regionB  = new JButton[6];
		mapSelect = false;
		houses = new TreeMap<>();
		players = new Player[4];
		
		for(int i = 0; i < 4; i++)
			players[i] = new Player();
		players[0].setColor("red");
		players[1].setColor("green");
		players[2].setColor("blue");
		players[3].setColor("black");
		try {
			Font f1 = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("fonts/FetteEngschrift.TTF")).deriveFont(12f);
		    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		    //register the font
		    ge.registerFont(f1);
		    blueRegion = ImageIO.read(getClass().getResourceAsStream("images/maps/blueRegion.png"));
		    brownRegion = ImageIO.read(getClass().getResourceAsStream("images/maps/brownRegion.png"));
		    greenRegion = ImageIO.read(getClass().getResourceAsStream("images/maps/greenRegion.png"));
		    purpleRegion = ImageIO.read(getClass().getResourceAsStream("images/maps/purpleRegion.png"));
		    redRegion = ImageIO.read(getClass().getResourceAsStream("images/maps/redRegion.png"));
		    yellowRegion = ImageIO.read(getClass().getResourceAsStream("images/maps/yellowRegion.png"));
		    
			houses.put("black", ImageIO.read(getClass().getResourceAsStream("images/houses/black.png")));
			houses.put("blue", ImageIO.read(getClass().getResourceAsStream("images/houses/blue.png")));
			houses.put("green", ImageIO.read(getClass().getResourceAsStream("images/houses/green.png")));
			houses.put("purple", ImageIO.read(getClass().getResourceAsStream("images/houses/purple.png")));
			houses.put("red", ImageIO.read(getClass().getResourceAsStream("images/houses/red.png")));
			houses.put("yellow", ImageIO.read(getClass().getResourceAsStream("images/houses/yellow.png")));
			
			rowBG = ImageIO.read(getClass().getResourceAsStream("images/ui/rowBG.jpg"));
			menuBG = ImageIO.read(getClass().getResourceAsStream("images/ui/menuBG.png"));
			
			darkMap = ImageIO.read(getClass().getResourceAsStream("images/maps/map3.jpg"));
			startScreen = ImageIO.read(getClass().getResourceAsStream("images/ui/StartScreen.jpg"));
			playerSelect = ImageIO.read(getClass().getResourceAsStream("images/ui/PlayerScreen.jpg"));
			frame = ImageIO.read(getClass().getResourceAsStream("images/ui/frame.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FontFormatException e1) {
			e1.printStackTrace();
		}
		
		sw = screenw;
		sh = screenh;
		setSize(sw, sh);
		startMenu = true;
		playerMenu = false;
		
		///
		start = new JButton("Start");
		setLayout(null);
		start.setBounds(sw/2-132, sh/2 - 80, 286, 140);
		start.setFocusPainted(false);
		start.setOpaque(false);
		start.setContentAreaFilled(false);
		start.setBorderPainted(false);
		try {
			BufferedImage startImage = ImageIO.read(getClass().getResourceAsStream("images/ui/start3.png"));
			start.setIcon(new ImageIcon(startImage));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
		start.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ButtonModel m = (ButtonModel) e.getSource();
		        if(m.isPressed()) {
		        	startMenu = false;
		        	playerMenu = true;
		        	makePlayerButtons();
		        	try {
		        		remove(start);
		        	} catch(Exception ex) { }
		        	repaint();
				}
			}
		});
		add(start);
		///
		startGame = new JButton("Start");
		startGame.setForeground(Color.WHITE);
		Font customF1 = new Font("f1", Font.PLAIN, 24);
		startGame.setFont(customF1);
		startGame.setBounds(sw - 250, sh - 150, 150, 80);
		startGame.setFocusPainted(false);
		startGame.setOpaque(false);
		startGame.setContentAreaFilled(false);
		startGame.setBorderPainted(false);
//		try {
//			BufferedImage startImage = ImageIO.read(getClass().getResourceAsStream("images/ui/start3.png"));
//			startGame.setIcon(new ImageIcon(startImage));
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
	    
		startGame.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ButtonModel m = (ButtonModel) e.getSource();
		        if(m.isPressed()) {
		        	int x = 0;
		        	for(int i = 0; i < 6; i++) {
		        		if(regionBMap.get(regionB[i]))
		        			x++;
		        	}
		        	if(x == 4) {
		        		if((regionBMap.get(regionB[4]) == false && false == regionBMap.get(regionB[5])) || 
		        				(regionBMap.get(regionB[2]) == false && false == regionBMap.get(regionB[5]))) {
		        			Font customF2 = new Font("f1", Font.PLAIN, 16);
		        			startGame.setFont(customF2);
		        			startGame.setText("<html>Regions must<br/>be connected!</html>");
		        		}
		        		else {
		        			Container pane = f.getContentPane();
		                    pane.remove(StartPanel.this);
		                    pane.add(pgPanel);
		                    pane.revalidate();
		        		}
		        	}
		        	else {
		        		Font customF2 = new Font("f1", Font.PLAIN, 14);
		        		startGame.setFont(customF2);
		        		startGame.setText("Select 4 regions!");
		        	}
				}
			}
		});
		///
		begin = new JButton("Begin");
		begin.setForeground(Color.WHITE);
		begin.setFont(customF1);
		begin.setBounds(sw - 250, sh - 136, 150, 70);
		begin.setFocusPainted(false);
		begin.setOpaque(false);
		begin.setContentAreaFilled(false);
		begin.setBorderPainted(false);
	    
		begin.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ButtonModel m = (ButtonModel) e.getSource();
		        if(m.isPressed()) {
		        	/// check if >1 select same color
		        	ArrayList<String> temp = new ArrayList<>();
		        	for(int i = 0; i < 4; i++)
		        		temp.add(players[i].getColor());
		        	
		        	for(int i = 0; i < 4; i++) {
		        		ArrayList<String> temp2 = new ArrayList<>(temp);
		        		temp2.remove(players[i].getColor());
		        		for(int j = 0; j < 4; j++)
		        			if(j!=i && temp2.contains(players[i].getColor())) {
		        				Font cf2 = new Font("f1", Font.PLAIN, 12);
		        				begin.setFont(cf2);
		        				begin.setText("<html><center>Players must<br/>have different colors!</center></html>");
		        				return;
		        			}
		        	}
		        	mapSelect = true;
		        	playerMenu = false;
		        	mapSelect = true;
		        	///
		        	
		        	try {
		        		for(int i = 0; i < 4; i++)
		        			remove(PButtons[i]);
		        		remove(begin);
		        	} catch(Exception ex) { }
		        	
		        	
		        	
		        	Color[] colors = new Color[6];
		        	String[] r = new String[6];
		        	r[0] = "Southwest"; r[1] = "Northeast"; r[2] = "Southeast";
		        	r[3] = "Northwest"; r[4] = "South"; r[5] = "Midwest";
		        	colors[0] = Color.CYAN; colors[1] = new Color(222,184,135); colors[2] = Color.GREEN;
		        	colors[3] = new Color(238,130,238); colors[4] = Color.RED; colors[5] = Color.YELLOW;
		        	for(int i = 0; i < 6; i++) {
		        		regionB[i] = new JButton();
		        	}
		        	regionBMap.put(regionB[0], false); regionBMap.put(regionB[1], false); regionBMap.put(regionB[2], false);
		        	regionBMap.put(regionB[3], false); regionBMap.put(regionB[4], false); regionBMap.put(regionB[5], false);
		        	
		        	regionB[0].setBounds(220, 345, 150, 80);
		        	regionB[1].setBounds(1040, 205, 150, 80);
		        	regionB[2].setBounds(940, 460, 150, 80);
		        	regionB[3].setBounds(235, 115, 150, 80);
		        	regionB[4].setBounds(620, 430, 150, 80);
		        	regionB[5].setBounds(680, 125, 150, 80);
		        	
		        	for(int i = 0; i < 6; i++) {
		        		//regionB[i].setBounds(100+186*i, sh/2 - 80, 150, 80);
		    			regionB[i].setFocusPainted(false);
		    			regionB[i].setVerticalTextPosition(JButton.CENTER);
		    			Font customF1 = new Font("f1", Font.PLAIN, 16);
		    			regionB[i].setFont(customF1);
		    			regionB[i].setForeground(Color.BLACK);
		    			regionB[i].setText(r[i]);
		    			regionB[i].setBackground(colors[i]);
		    			final int num = i;
		    			regionB[i].getModel().addChangeListener(new ChangeListener() {
		    				public void stateChanged(ChangeEvent e) {
		    					ButtonModel m = (ButtonModel) e.getSource();
		    					
		    			        if(m.isPressed()) {
		    			        	boolean temp = !regionBMap.get(regionB[num]);
		    			        	regionBMap.put(regionB[num], temp);
		    			        	repaint();
		    					}
		    				}
		    			});
		    			add(regionB[i]);
		    		}
		        	add(startGame);
		        	repaint();
				}
			}
		});
		
		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//g.drawImage(map, 0, 0, sw, sh, null);
		if(startMenu)
			g.drawImage(startScreen, 0, 0, sw, sh, null);
		else if(playerMenu) {
			g.drawImage(playerSelect, 0, 0, sw, sh, null);
			g.drawImage(menuBG, sw - 250, sh - 136, 150, 70, null);
			for(int i = 0; i < 4; i++) {
				
				g.drawImage(rowBG, sw/2-415, sh/2 - 160+100*i, 830, 80, null);
					
				add(PButtons[i]);
				add(begin);
				g.drawImage(houses.get(players[i].getColor()), sw/2+sw/4, sh/2-145+100*i, 50, 50, null);
			}
		}
		else if(mapSelect) {
			g.drawImage(darkMap, 0, 0, sw, sh, null);
			if(regionBMap.get(regionB[0]))
				g.drawImage(blueRegion, 0, 0, sw, sh, null);
			if(regionBMap.get(regionB[1]))
				g.drawImage(brownRegion, 0, 0, sw, sh, null);
			if(regionBMap.get(regionB[2]))
				g.drawImage(greenRegion, 0, 0, sw, sh, null);
			if(regionBMap.get(regionB[3]))
				g.drawImage(purpleRegion, 0, 0, sw, sh, null);
			if(regionBMap.get(regionB[4]))
				g.drawImage(redRegion, 0, 0, sw, sh, null);
			if(regionBMap.get(regionB[5]))
				g.drawImage(yellowRegion, 0, 0, sw, sh, null);
			g.drawImage(frame, 0, 0, sw, sh, null);
			g.drawImage(menuBG, sw - 250, sh - 150, 150, 80, null);
		}
	}
	
	private void makePlayerButtons() {
		PButtons = new JButton[4];
		for(int i = 0; i < 4; i++) {
			PButtons[i] = new JButton("  Player " + (i+1));
			PButtons[i].setBounds(sw/2-415, sh/2 - 160+100*i, 830, 80);
			PButtons[i].setFocusPainted(false);
			PButtons[i].setOpaque(false);
			PButtons[i].setContentAreaFilled(false);
			PButtons[i].setBorderPainted(false);
			PButtons[i].setHorizontalAlignment(SwingConstants.LEFT);
			PButtons[i].setVerticalTextPosition(JButton.CENTER);
			Font customF1 = new Font("f1", Font.PLAIN, 20);
			PButtons[i].setFont(customF1);
			PButtons[i].setForeground(Color.WHITE);
			PButtons[i].setText("  Player " + (i+1));
			final int num = i;
			PButtons[i].getModel().addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					ButtonModel m = (ButtonModel) e.getSource();
					
			        if(m.isPressed()) {
			        	for(String c: houses.keySet()) {
			        		if(c.equals(players[num].getColor())) {
			        			try {
			        				Entry<String, BufferedImage> next = houses.higherEntry(c); // next
			        				players[num].setColor(next.getKey());
			        			} catch(Exception e1) {
			        				players[num].setColor("black");
			        			}
			        			break;
			        		}
			        	}
			        	repaint();
					}
				}
			});
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

	}
	

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
