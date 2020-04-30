import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class GameState {

	private Board board;
	private JFrame frame;
	private PowerGridPanel pgPanel;
	private StartPanel startPanel;
	private int phase, turn, step;
	private ArrayList<Player> players;

	public GameState() {
		phase = 1;
		players = new ArrayList<Player>();
		for(int i = 0; i < 4; i++)
			players.add(new Player());
		turn = 0;
		step = 1;
		frame = new JFrame("Power Grid");
		pgPanel = new PowerGridPanel();
		startPanel = new StartPanel(1280, 720, frame, pgPanel);
		frame.setSize(1305, 745);
		BufferedImage img = null;
		try {
			img = ImageIO.read(getClass().getResourceAsStream("images/ui/factory.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame.setIconImage(img);
		frame.add(startPanel);
		frame.setVisible(true);
	}
	
	public void run() {
		
	}
	
	public void nextPhase() {
		phase = (phase + 1) % 4;
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
		boolean step3 = false;
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
					step3 = reOrgPlant();
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
								step3 = reOrgPlant();
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
			step3 = reOrgPlant();
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
		if(step == 3) //NEW
			step3 = false;
		else if(step3)
		{
			step3();
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
	
	public boolean reOrgPlant()
	{
		boolean toReturn = false;
		PowerPlant newPlant = board.draw(players);
		if(newPlant == null)
			return false;
		else if(newPlant.getNum() == 99)
		{
			toReturn = true;
		}	
		board.getMarketPlants().add(newPlant);
		board.reOrgMarketPlants();
		return toReturn;
	}

	public void buyCities() {
		setTurn(3);
		boolean step3 = false;
		for(int i = 0; i < 4; i++)
		{
			boolean clickedNext = false;
			while(true)
			{
				clickedNext = false; //panel checks if player clicks next(doesnt buy a city)
				if(clickedNext)
					break;
				ArrayList<City> canConnect = board.canConnect(players.get(turn));
				City city = new City("urmum"); //panel returns the city that the player chooses
				//use canConnect for the cities that the player can connect to
				int numOwners = city.numOwners();
				//put player's house on the city, use numOwners for which place to put it on
				board.buyCity(city, players.get(turn));
				if(board.checkPowerPlant(players.get(turn), players) == 3);
					step3 = true;
				if(step == 3) //NEW
					step3 = false;
			}
		}
		setTurn(0);
		boolean step2 = false;
		if(step >= 2)
			step2 = true;
		else if(step == 1)
			step2 = false;
		if(!step2)
		{
			for(int i = 0; i < players.size(); i++)
			{
				if(players.get(i).getCities().size() >= 7)
				{
					setStep(2);
					step2 = true;
				}
			}
			if(step2)
			{
				if(board.removeLowestAndReplace(players) == 3);
				step3 = true;
			}
		}
		for(int i = 0; i < players.size(); i++)
		{
			if(players.get(i).getCities().size() >= 17)
				endGame();
		}
		if(step3)
		{
			step3();
		}	
		setTurn(0);
	}

	public void buyResources() {
		setTurn(3);
		for(int i = 0; i < 4; i++)
		{
			boolean clickedNext = false; 
			while(true)
			{
				clickedNext = false; //panel checks if player clicks next(doesn't buy materials again)
				if(clickedNext)
					break; //this means it only breaks out of the while loop right?
				int powerPlant = -1; //panel returns index of the chosen power plant
				PowerPlant p = players.get(turn).getPlants().get(powerPlant);
				if(!p.getType().equals("wind"))
				{
					if((p.getType().equals("hybrid") && (board.getResourceMarket().get("coal") > 0 || board.getResourceMarket().get("oil") > 0)) || (!p.getType().equals("hybrid") && board.getResourceMarket().get(p.getType()) > 0))
					{
						int maxBuy = p.materialsTillFull();
						if(p.getType().equals("hybrid") && (board.getResourceMarket().get("coal") + board.getResourceMarket().get("oil")) < maxBuy)
							maxBuy = board.getResourceMarket().get("coal") + board.getResourceMarket().get("oil");
						else if(board.getResourceMarket().get(p.getType()) < maxBuy)
							maxBuy = board.getResourceMarket().get(p.getType());
						if(!p.getType().equals("hybrid") && board.moneyBuy(p, players.get(turn)) < maxBuy)
							maxBuy = board.moneyBuy(p, players.get(turn));
						if(p.getType().equals("hybrid"))
						{
							int numCoal = -1; //panel returns number of coal bought
							int numOil = -1; //panel returns number of oil bought
							//use maxBuy for the maximum number of both to be bought
							//NOTE: I did not calculate if the player would have enough money
							board.buyResourcesHybrid(players.get(turn), numCoal, numOil, p);
						}
						else
						{
							int boughtAmt = -1; //panel returns number of resources bought
							//use maxBuy for the maximum able to buy
							board.buyResources(players.get(turn), boughtAmt, p);
						}
						
					}
				}
				//panel goes back to allow player to choose another plant or move onto the next player
			}
			nextTurnReverse();
		}
		setTurn(0);
	}

	public void endRound() {
		setTurn(0);
		boolean step3 = false;
		for(int i = 0; i < players.size(); i++)
		{
			int canSupply = board.canSupply(players.get(turn));
			int numSupplied = -1; //panel returns how many cities player chooses to supply
			board.bureacracy(players.get(turn), numSupplied);
			board.updateResourceMarket(players);
			if(step != 3 && board.removeHighestAndReplace(players) == 3)
				step3 = true;
			else if(step == 3) {
				step3 = false;
				board.removeLowestAndReplace(players);
			}
				
		}
		if(step3)
			step3();
		setTurn(0);
	}
	
	public void endGame() {
		Player winner = board.getWinner(players);
		//panel displays win screen 
	}
	
	public void step3()
	{
		board.step3();
		setStep(3);
		Collections.shuffle(board.getDeck());//work?
	}

	public void setStep(int step) {
		this.step = step;
		board.updateStep(step);
	}

	public String[] chosenAreas() {
		return null;
	}
}
