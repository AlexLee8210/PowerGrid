import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class Board {
	private ArrayList<PowerPlant> deck;
	private ArrayList<PowerPlant> marketPlants;
	private TreeMap<String, Integer> resourceMarket;//used to be LinkedHashMap
	private Graph graph;
	private int step;
	public Board() throws IOException
	{
		deck = new ArrayList<PowerPlant>();
		readPlantFile();
		Collections.shuffle(deck);
		marketPlants = new ArrayList<PowerPlant>();//should have 8 plants
		resourceMarket = new TreeMap<String, Integer>();
		resourceMarket.put("coal", 24);
		resourceMarket.put("oil", 18);
		resourceMarket.put("garbage", 6);
		resourceMarket.put("uranium", 2);
		graph = new Graph();
		//havent seen the graph class yet so will edit later
		step = 1;
	}
	public void readPlantFile() throws IOException
	{
		Scanner sc = new Scanner(new File("PowerPlant.txt"));
		while(sc.hasNextLine())
		{
			String[] arr = sc.nextLine().split(" ");
			deck.add(new PowerPlant(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]), arr[3]));
		}
	}
	public ArrayList<PowerPlant> getDeck()
	{
		return deck;
	}
	public void updateStep(int newStep)
	{
		step = newStep;
	}
	public TreeMap<String, Integer> getResourceMarket() {
		return resourceMarket;
	}
	public ArrayList<PowerPlant> getMarketPlants() {
		return marketPlants;
	}
	public Graph getGraph() {
		return graph;
	}
	public void updateResourceMarket(Player[] players)
	{
		TreeMap<String, Integer> remain = new TreeMap<String, Integer>();
		remain.put("coal", 24);
		remain.put("oil", 24);
		remain.put("garbage", 24);
		remain.put("uranium", 12);
		for(Player p : players)
		{
			ArrayList<PowerPlant> plant = p.getPlants();
			for(int i = 0; i < plant.size(); i++)
			{
				if(plant.get(i).getType().equals("hybrid"))
				{
					remain.put("coal", remain.get("coal")-plant.get(i).getNumCoal());
					remain.put("oil", remain.get("oil")-plant.get(i).getNumOil());
				}
				else if(!plant.get(i).getType().equals("wind"))
				{
					remain.put(plant.get(i).getType(), remain.get(plant.get(i).getType())-plant.get(i).getMats());
				}
			}
		}
		Set<String> types = resourceMarket.keySet();
		Iterator<String> itr = types.iterator();
		while(itr.hasNext())
		{
			String type = itr.next();
			remain.put(type, remain.get(type) - resourceMarket.get(type));
		}
		if(step == 1)
		{
			updateResourceMarket2(remain, "coal", 5);
			updateResourceMarket2(remain, "oil", 3);
			updateResourceMarket2(remain, "garbage", 2);
			updateResourceMarket2(remain, "uranium", 1);
		}
		else if(step == 2)
		{
			updateResourceMarket2(remain, "coal", 6);
			updateResourceMarket2(remain, "oil", 4);
			updateResourceMarket2(remain, "garbage", 3);
			updateResourceMarket2(remain, "uranium", 2);
		}
		else if(step == 3)
		{
			updateResourceMarket2(remain, "coal", 4);
			updateResourceMarket2(remain, "oil", 5);
			updateResourceMarket2(remain, "garbage", 4);
			updateResourceMarket2(remain, "uranium", 2);
		}
		
	}
	public void updateResourceMarket2(TreeMap<String, Integer> remain, String type, int amt)
	{
		int toAdd = amt;
		if(remain.get(type) < amt)
			toAdd = remain.get(type);
		resourceMarket.put(type, resourceMarket.get(type) + toAdd);
	}
	public ArrayList<Player> determineOrder(ArrayList<Player> players)
	{
		Collections.sort(players);
		ArrayList<Player> temp = new ArrayList<Player>();
		for(int i = players.size() - 1; i > 0; i--)
			temp.add(players.get(i));
		return temp;
	}
	public int moneyBuy(PowerPlant p, Player play)
	{
		int cash = play.getElektros(), amt = resourceMarket.get(p.getType()), cost = 0, count = 0;
		while(cost < cash && cash > 0 && amt > 0)
		{
			if(p.getType().equals("uranium"))
				cost = calcCostUranium(amt);
			else
				cost = calcCost(amt);
			cash -= cost;
			if(cash <= 0)					
				break;
			count++;
		}
		return count;
	}
	public int calcCostUranium(int amt)
	{
		if(amt < 5)
			return 18 - (2*(amt));
		return 9 - (amt-4);

	}
	public int calcCost(int amt)
	{
		double amount = amt/3.0;
		double excess = amount%((int)amount);
		amt = amt/3;
		if(excess > 0)
			return 8 - amt;
		return 9-amt;
	}
	public void buyResources(Player player, int amt, PowerPlant plant)
	{
		for(int i = 0; i < amt; i++)
		{
			if(plant.getType().equals("uranium"))
				player.buy(calcCostUranium(resourceMarket.get("uranium")));
			else
				player.buy(calcCost(resourceMarket.get(plant.getType())));
			resourceMarket.put(plant.getType(), resourceMarket.get(plant.getType())-1);
		}
		plant.addMaterial(amt);
	}
	public void buyResourcesHybrid(Player player, int c, int o, PowerPlant p)
	{
		for(int i = 0; i < c; i++)
		{
			player.buy(calcCost(resourceMarket.get("coal")));
			resourceMarket.put("coal", resourceMarket.get("coal")-1);
		}
		for(int i = 0; i < o; i++)
		{
			player.buy(calcCost(resourceMarket.get("oil")));
			resourceMarket.put("oil", resourceMarket.get("oil")-1);
		}
		p.addMaterialHybrid(c, o);
	}
	public boolean canBuyCity(Player player, City city, int step)
	{
		//TODO
		return false;
	} 
	public void reOrgMarketPlants()
	{
		Collections.shuffle(marketPlants);
	}
	public Player buyPlant(Player player, int cost, int plantIndex)
	{
		player.buy(cost);
		player.addPlant(marketPlants.remove(plantIndex));
		return player;
	}
	public PowerPlant draw()
	{
		if(deck == null)
			return null;
		return deck.remove(deck.size()-1);
	}
	public Player getWinner(Player[] players)
	{
		//TODO
		return players[0];
	}
	public void setActiveRegions(ArrayList<String> regions)
	{
		//TODO needs Graph
	}
}
