import java.awt.Color;
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
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static java.lang.System.out;

public class PowerGridPanel extends JPanel implements MouseListener {
	
	private BufferedImage darkMap, startScreen, playerSelect, rowBG, menuBG;
	private BufferedImage blueRegion, brownRegion, greenRegion, purpleRegion, redRegion, yellowRegion;
	private int sw, sh;
	private JButton start, begin;
	private boolean startMenu, playerMenu, beginClicked, mapSelect;
	private int pickedMap;
	private JButton[] PButtons;
	private Player[] players;
	private NavigableMap<String, BufferedImage> houses;
	private LinkedHashMap<JButton, Boolean> regionBMap;
	private JButton[] regionB;
	
	public PowerGridPanel() {
		regionBMap = new LinkedHashMap<>();
		regionB  = new JButton[6];
		beginClicked = false;
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
			Font f1 = Font.createFont(Font.TRUETYPE_FONT, new File("src/fonts/FetteEngschrift.TTF")).deriveFont(12f);
		    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		    //register the font
		    ge.registerFont(f1);
		    
		    blueRegion = ImageIO.read(new File("src/images/maps/blueRegion.png"));
		    brownRegion = ImageIO.read(new File("src/images/maps/brownRegion.png"));
		    greenRegion = ImageIO.read(new File("src/images/maps/greenRegion.png"));
		    purpleRegion = ImageIO.read(new File("src/images/maps/purpleRegion.png"));
		    redRegion = ImageIO.read(new File("src/images/maps/redRegion.png"));
		    yellowRegion = ImageIO.read(new File("src/images/maps/yellowRegion.png"));
		    
			houses.put("black", ImageIO.read(new File("src/images/houses/black.png")));
			houses.put("blue", ImageIO.read(new File("src/images/houses/blue.png")));
			houses.put("green", ImageIO.read(new File("src/images/houses/green.png")));
			houses.put("purple", ImageIO.read(new File("src/images/houses/purple.png")));
			houses.put("red", ImageIO.read(new File("src/images/houses/red.png")));
			houses.put("yellow", ImageIO.read(new File("src/images/houses/yellow.png")));
			
			rowBG = ImageIO.read(new File("src/images/ui/rowBG.jpg"));
			menuBG = ImageIO.read(new File("src/images/ui/menuBG.png"));
			
			darkMap = ImageIO.read(new File("src/images/maps/map3.jpg"));
			startScreen = ImageIO.read(new File("src/images/ui/StartScreen.jpg"));
			playerSelect = ImageIO.read(new File("src/images/ui/PlayerScreen.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FontFormatException e1) {
			e1.printStackTrace();
		}
		
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		sw = ss.width;
		sh = ss.height;
		setSize(sw, sh);
		startMenu = true;
		playerMenu = false;
		pickedMap = 0;
		
		///
		start = new JButton("Start");
		setLayout(null);
		start.setBounds(sw/2-132, sh/2 - 80, 286, 140);
		start.setFocusPainted(false);
		start.setOpaque(false);
		start.setContentAreaFilled(false);
		start.setBorderPainted(false);
		try {
			BufferedImage startImage = ImageIO.read(new File("src/images/ui/start3.png"));
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
		begin = new JButton("Begin");
		setLayout(null);
		begin.setForeground(Color.WHITE);
		Font customF1 = new Font("f1", Font.PLAIN, 24);
		begin.setFont(customF1);
		begin.setBounds(sw - 262, sh - 135, 116, 70);
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
		        				beginClicked = true;
		        				repaint();
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
		        	r[0] = "West"; r[1] = "Northeast"; r[2] = "Southeast";
		        	r[3] = "Northwest"; r[4] = "South Central"; r[5] = "North Central";
		        	colors[0] = Color.CYAN; colors[1] = new Color(222,184,135); colors[2] = Color.GREEN;
		        	colors[3] = new Color(238,130,238); colors[4] = Color.RED; colors[5] = Color.YELLOW;
		        	for(int i = 0; i < 6; i++) {
		        		regionB[i] = new JButton();
		        	}
		        	regionBMap.put(regionB[0], false); regionBMap.put(regionB[1], false); regionBMap.put(regionB[2], false);
		        	regionBMap.put(regionB[3], false); regionBMap.put(regionB[4], false); regionBMap.put(regionB[5], false);
		        	for(int i = 0; i < 6; i++) {
		    			regionB[i].setBounds(100+186*i, sh/2 - 80, 150, 80);
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
		    			        	out.println(regionBMap.get(regionB[num]));
		    			        	repaint();
		    					}
		    				}
		    			});
		    			add(regionB[i]);
		    		}
		        	
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
			g.drawImage(menuBG, sw - 260, sh - 135, 116, 70, null);
			for(int i = 0; i < 4; i++) {
				
				g.drawImage(rowBG, sw/2-415, sh/2 - 160+100*i, 830, 80, null);
					
				add(PButtons[i]);
				add(begin);
				g.drawImage(houses.get(players[i].getColor()), sw/2+sw/4, sh/2-145+100*i, 50, 50, null);
				
				if(beginClicked && !mapSelect) {
					g.setFont(new Font("f1", Font.PLAIN, 14));
					g.setColor(Color.WHITE);
					g.drawString("Players must have different colors!", sw - 480, sh - 100);
				}
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
