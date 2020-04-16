import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

public class Board {
	private ArrayList<PowerPlant> deck;
	private ArrayList<PowerPlant> marketPlants;
	private LinkedHashMap<String, Integer> resourceMarket;
	private Graph graph;
	private int step;
	public Board()
	{
		deck = new ArrayList<PowerPlant>();
		// deck will be read in from text file
		marketPlants = new ArrayList<PowerPlant>();
		resourceMarket = new LinkedHashMap<String, Integer>();
		graph = new Graph();
		//havent seen the graph class yet so will edit later
		step = 1;
	}
	public ArrayList<PowerPlant> getDeck()
	{
		return deck;
	}
	public LinkedHashMap<String, Integer> getResourceMarket() {
		return resourceMarket;
	}
	public ArrayList<PowerPlant> getMarketPlants() {
		return marketPlants;
	}
	public Graph getGraph() {
		return graph;
	}
	public void updateResourceMarket()
	{
		//TODO i think this needs a text file
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
