import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class GameState {

	private Board board;
	private JFrame frame;
	private PowerGridPanel panel;
	private int phase, turn, step;
	private ArrayList<Player> players;

	public GameState() {

		players = new ArrayList<Player>();
		for(int i = 0; i < 4; i++)
			players.add(new Player());
		turn = 0;
		step = 1;
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
		phase = (phase + 1) % 4;
		//if else for each phase?
	}

	public void nextTurn() {
		turn = (turn+1) % 3;
	}
	
	public void setTurn(int turn) {
		this.turn = turn;
	}
	
	public void nextTurnReverse() {
		if(turn - 1 < 0)
			turn = 3;
		else
			turn--;
	}

	public void determineOrder() {
		setPlayers(board.determineOrder(players));
	}

	public void setPlayers(ArrayList<Player> players)
	{
		this.players = players;
	}
	
	public void auction() {//not sure how to handle exception for first round: players HAVE to choose power plant, and player order redetermined after choosing
		ArrayList<Integer> bought = new ArrayList<Integer>();
		for(int i = 0; i < 4; i++)
			bought.add(i);
		setTurn(0);
		boolean didPass = false;
		int plantsBought = 0, powerPlant = -1, currentBid = 0;
		while(bought != null)
		{
			for(int i = 0; i < 4; i++)
			{
				int maxBid = players.get(turn).getElektros();
				//players.get(turn), board.getMarketPlants(), and maxBid should be used by Graphics 
				//so that player can choose the power plant to buy and the cost
				didPass = false; //panel should return if player passed or not
				if(!didPass)
				{
					powerPlant = -1; //panel should return the powerplant the player chose (should be a number from 0-3)
					//0 being the leftmost powerplant, 3 is rightmost
					currentBid = 0; //panel should return the bid on the powerplant chosen (use maxBid to cap the bid)
				}
				else if(didPass)
				{
					bought.remove(turn);
				}
				else if(bought.size() == 1)
				{
					players.set(turn, board.buyPlant(players.get(turn), currentBid, powerPlant));
					reOrgPlant();
					plantsBought++;
				}
				else 
				{
					boolean isBought = false;
					ArrayList<Integer> temp = bought;
					while(!isBought)
					{
						nextTurn();
						maxBid = players.get(turn).getElektros();
						if(maxBid > currentBid)
						{
							didPass = false; //panel should return if player passed or not
							if(didPass)
							{
								temp.remove(turn);
							}
							else if(temp.size() == 1)
							{
								players.set(turn, board.buyPlant(players.get(turn), currentBid, powerPlant));
								reOrgPlant();
								plantsBought++;
							}
							else
							{
								currentBid = 0; //panel should return new bid (use currentBid and maxBid for range)
							}
						}
					}
				}
				setTurn(i);
				nextTurn();
			}
		}
		if(plantsBought == 0) {
			board.getMarketPlants().remove(0);
			reOrgPlant();
		}
		setTurn(0);
		for(int i = 0; i < players.size(); i++)
		{
			if(players.get(turn).getNumPlants() > 3)
			{
				int plantIndex = -1; //panel should return index of powerplant(from player's hand) to remove
				ArrayList<String> materials = players.get(turn).removePlant(plantIndex);
				if(needsRedistribute(materials))
					redistribute(materials);
			}
			nextTurn();
		}
		setTurn(0);
	}
	
	public boolean needsRedistribute(ArrayList<String> materials)
	{
		if(materials == null)
			return false;
		ArrayList<PowerPlant> plants = players.get(turn).getPlants();
		for(PowerPlant p : plants)
		{
			if(needsRedistribute(materials, p))
				return true;
		}
		return false;
	}
	
	public boolean needsRedistribute(ArrayList<String> materials, PowerPlant p)
	{
		if(!p.isFull())
		{
			if(p.getType().equals("hybrid") && (materials.contains("coal") || materials.contains("oil")))
				return true;
			else if(!p.getType().equals("wind") && materials.contains(p.getType()))
				return true;
		}
		return false;
	}
	
	public void redistribute(ArrayList<String> materials)
	{
		int types = 0;
		if(materials.contains("coal") && materials.contains("oil"))
		{
			ArrayList<String> temp = new ArrayList<String>();
			temp.add("coal");
			if(!needsRedistribute(temp))
			{
				for(int i = materials.size()-1; i > 0; i--)
					if(materials.get(i).equals("coal"))
						materials.remove(i);
			}
			else
			{
				types++;
			}
			temp.remove("oil");
			temp.add(("oil"));
			if(!needsRedistribute(temp))
			{
				for(int i = materials.size()-1; i > 0; i--)
					if(materials.get(i).equals("oil"))
						materials.remove(i);
			}
			else
			{
				types++;
			}
		}
		else
			types = 1;
		for(PowerPlant p : players.get(turn).getPlants())
		{
			
			if(needsRedistribute(materials, p))
			{
				int matsTillFull = p.materialsTillFull(); //panel uses this to ensure the player doesn't add too much to the plant
				if(p.getType().equals("hybrid"))
				{
					int coal = 0, oil = 0; // panel uses this for the number of coal and oil are needed to be redistributed
					for(int i = 0; i < materials.size(); i++)
					{
						if(materials.get(i).equals("coal"))
							coal++;
						else if(materials.get(i).equals("oil"))
							oil++;
					}
					int numCoal = 0; //panel returns how many coal the player wishes to add
					int numOil = 0; //panel returns how many oil the player wishes to add
					p.addMaterialHybrid(numCoal, numOil);
					for(int i = materials.size()-1; i > 0; i--)
					{
						if(numCoal > 0 && materials.get(i).equals("coal"))
						{
							numCoal--;
							materials.remove(i);
						}
						else if(numOil > 0 && materials.get(i).equals("coal"))
						{
							numOil--;
							materials.remove(i);
						}
					}
				}
				else
				{
					int mats = 0;//panel uses this for number of material are needed to be redistributed
					if(types == 2)
					{
						for(int i = 0; i < materials.size(); i++)
							if(materials.get(i).equals(p.getType()))
								mats++;
					}
					else
						mats = materials.size(); 
					int numMats = 0;//panel return how many materials the player wishes to add
					p.addMaterial(numMats);
					for(int i = materials.size()-1; i > 0; i--)
					{
						if(numMats > 0 && materials.get(i).equals(p.getType()))
							materials.remove(i);
							
					}
				}
			}
		}
	}
	
	public void reOrgPlant()
	{
		PowerPlant newPlant = board.draw();
		if(newPlant.getNum() == 99)
		{
			setStep(3);
			newPlant = board.draw();
		}	
		board.getMarketPlants().add(newPlant);
		board.reOrgMarketPlants();
	}

	public void buyCities() {

	}

	public void buyResources() {
		
	}

	public void endRound() {

	}

	public void setStep(int step) {
		this.step = step;
		board.updateStep(step);
	}

	public String[] chosenAreas() {
		return null;
	}
}
