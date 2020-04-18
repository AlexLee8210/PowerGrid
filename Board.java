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
		marketPlants = new ArrayList<PowerPlant>();
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
		return players;
	}
	public ArrayList<PowerPlant> auction()
	{
		//TODO
		return new ArrayList<PowerPlant>();
	}
	public void buyResources(Player player, ArrayList<String> mats, PowerPlant plant)
	{
		//TODO
	}
	public boolean canBuyCity(Player player, City city, int step)
	{
		//TODO
		return false;
	} 
	public void reOrgMarketPlants()
	{
		//TODO
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
