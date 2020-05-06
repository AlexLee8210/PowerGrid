import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import static java.lang.System.out;

public class Board {
	private ArrayList<PowerPlant> deck;
	private ArrayList<PowerPlant> marketPlants;
	private TreeMap<String, Integer> resourceMarket;//used to be LinkedHashMap
	private Graph graph;
	private int step;
	private TreeMap<Integer, Integer> paymentTable;

	@SuppressWarnings("unchecked")
	public Board() {
		deck = new ArrayList<PowerPlant>();
		paymentTable = new TreeMap<Integer, Integer>();
		try {
			readPaymentFile();
			readPlantFile();
		} catch(Exception e) {
			e.printStackTrace();
		}
		Collections.sort(deck);
		marketPlants = new ArrayList<PowerPlant>();//should have 8 plants
		for(int i = 7; i >= 0; i--)
			marketPlants.add(deck.remove(i));
		Collections.sort(marketPlants);
		for(int i = deck.size()-1; i > 0; i--) {
			if(deck.get(i).getNum() == 13)
				deck.remove(i);
		}
		Collections.shuffle(deck);
		for(int i = 0; i < 4; i++)
			deck.remove(i);
		resourceMarket = new TreeMap<String, Integer>();
		resourceMarket.put("coal", 24);
		resourceMarket.put("oil", 18);
		resourceMarket.put("garbage", 6);
		resourceMarket.put("uranium", 2);
		graph = new Graph();
		//havent seen the graph class yet so will edit later
		step = 1;
		//out.println(marketPlants);
	}
	public void readPlantFile() throws IOException
	{
		InputStream is = getClass().getResourceAsStream("PowerPlant.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = "";
		while((line = br.readLine()) != null)
		{
			String[] arr = line.split(" ");
			deck.add(new PowerPlant(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]), arr[3]));
		}
	}
	public void readPaymentFile() throws IOException
	{
		InputStream is = getClass().getResourceAsStream("Payment.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = "";
		while((line = br.readLine()) != null)
		{
			String[] arr = line.split(" ");
			paymentTable.put(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
		}
	}
	public TreeMap<Integer, Integer> getPaymentTable()
	{
		return paymentTable;
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
	public void updateResourceMarket(ArrayList<Player> players)
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
	@SuppressWarnings("unchecked")
	public ArrayList<Player> determineOrder(ArrayList<Player> players)
	{
		Collections.sort(players);
		//Collections.reverse(players);
		return players;
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
	public ArrayList<City> cityList()
	{
		HashMap<String, City> cities = graph.getCities();
		Set<String> citySet = cities.keySet();
		ArrayList<City> city = new ArrayList<City>();
		Iterator<String> itr = citySet.iterator();
		while(itr.hasNext())
		{
			city.add(cities.get(itr.next()));
		}
		return city;
	}
	public boolean canBuyCity(Player player, City city)//checks if player has enough money, if city has too many owners, and if player already has the city
	{
		ArrayList<City> playerCity = player.getCities();
		for(int i = 0; i < playerCity.size(); i++)
		{
			if(playerCity.get(i).getName().equals(city.getName()))
				return false;
		}
		if(city.numOwners() == step) //step 1 = 1 owner, step 2 = 2 owners, etc
			return false;
		int cost = Integer.MAX_VALUE;
		for(City c : playerCity)
		{
			if(cost > graph.getShortestPathCost(c, city))
				cost = graph.getShortestPathCost(c, city);
		}
		cost += 10 + 5 * city.numOwners();
		if(player.getElektros() < cost)
			return false;
		return true;
	} 
	public ArrayList<City> canConnect(Player player)
	{
		ArrayList<City> toReturn = cityList();
		for(int i = toReturn.size()-1; i > 0; i--)
		{
			if(!canBuyCity(player, toReturn.get(i)))
				toReturn.remove(i);
		}
		return toReturn;
	}
	public void buyCity(City city, Player player)
	{
		int cost = Integer.MAX_VALUE;
		Set<String> citySet = graph.getCities().keySet();
		Iterator<String> itr = citySet.iterator();
		while(itr.hasNext())
		{
			String next = itr.next();
			if(city.getName().equals(next))
			{
				graph.getCities().get(next).addOwner(player);
				ArrayList<City> playerCities = player.getCities();
				for(City c : playerCities)
				{
					if(cost > graph.getShortestPathCost(c, city))
						cost = graph.getShortestPathCost(c, city);
				}
				cost += 10 + 5 * city.numOwners();
				player.buy(cost);
				player.addCity(graph.getCities().get(next));
			}
		}
	}
	public int checkPowerPlant(Player player, ArrayList<Player> players)
	{
		int numCity = player.getCities().size();
		int count = 0;
		for(int i = marketPlants.size()-1; i > 0; i--)
		{
			if(numCity > marketPlants.get(i).getNum())
			{
				marketPlants.remove(i);
				count++;
			}
		}
		boolean sstep = false;
		for(int i = 0; i < count; i++)
		{
			PowerPlant plant = draw(players);
			if(plant == null)
			{
				return step;
			}
			else if(plant.getNum() == 99)
			{
				plant = draw(players);
				if(plant == null)
					return step;
				sstep = true;
			}
			marketPlants.add(plant);
		}
		reOrgMarketPlants();
		if(sstep == true)
			return 3;
		return step;
	}
	public int removeLowestAndReplace(ArrayList<Player> players)
	{
		int toReturn = step;
		marketPlants.remove(0);
		PowerPlant plant = draw(players);
		if(plant == null)
			return step;
		else if(plant.getNum() == 99)
			toReturn = 3;
		marketPlants.add(plant);
		reOrgMarketPlants();
		return toReturn;
	}
	public int removeHighestAndReplace(ArrayList<Player> players)
	{
		int toReturn = step;
		deck.add(0, marketPlants.remove(marketPlants.size()-1));
		PowerPlant plant = draw(players);
		if(plant == null)
			return step;
		else if(plant.getNum() == 99)
			toReturn = 3;
		marketPlants.add(plant);
		reOrgMarketPlants();
		return toReturn;
	}
	
	public void removeMarketPlant(PowerPlant p) {
		marketPlants.remove(p);
		marketPlants.add(deck.remove(0));
		Collections.sort(marketPlants);
	}
	public void reOrgMarketPlants()
	{
		Collections.sort(marketPlants);
	}
	public Player buyPlant(Player player, int cost, int plantIndex)
	{
		player.buy(cost);
		player.addPlant(marketPlants.remove(plantIndex));
		return player;
	}
	public PowerPlant draw(ArrayList<Player> players)
	{
		if(deck == null)
			return null;
		PowerPlant plant = deck.remove(deck.size()-1);
		int leadingPlayer = Integer.MIN_VALUE;
		for(int i = 0; i < players.size(); i++)
		{
			if(players.get(i).getCities().size() > leadingPlayer)
				leadingPlayer = players.get(i).getCities().size();
		}
		while(plant.getNum() < leadingPlayer)
		{
			plant = deck.remove(deck.size()-1);
		}
		return plant;
	}
	public void step3()
	{
		reOrgMarketPlants();
		marketPlants.remove(0);
		marketPlants.remove(marketPlants.size()-1);
	}
	public int canSupply(Player player)
	{
		int toReturn = player.getCities().size();
		int canPower = 0;
		for(PowerPlant p : player.getPlants())
		{
			if(p.canPowerCity())
				canPower += p.getNum();
		}
		if(canPower < toReturn)
			toReturn = canPower;
		return toReturn;
	}
	public void bureacracy(Player player, int num)
	{
		for(int i = 0; i < player.getPlants().size(); i++)
			player.getPlants().get(i).consumeMats();
		if(num > 20)
			num = 20;
		player.addElektros(paymentTable.get(num));
	}
	public Player getWinner(ArrayList<Player> players)//possibly faulty
	{
		int index = -1, numSupply = Integer.MIN_VALUE;
		for(int i = 0; i < players.size(); i++)
		{
			if(canSupply(players.get(i)) > numSupply)
			{
				index = i;
				numSupply = canSupply(players.get(i));
			}
		}
		return players.get(index);
	}
	public void setActiveRegions(ArrayList<String> regions)
	{
		//TODO needs Graph
	}
}