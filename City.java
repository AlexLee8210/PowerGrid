import java.util.ArrayList;
import java.util.LinkedHashMap;

public class City implements Comparable {
	
	private int cost;
	private String name;
	private String region;
	private LinkedHashMap<City, Integer> neighbors;
	private Player[] owners;
	private boolean isActive;
	private int priority;
	
	public City(String name) {
		this.name = name;
		neighbors = new LinkedHashMap<City, Integer>();
		priority = Integer.MAX_VALUE;
		owners = new Player[3];
	}
	
	public City(String name, String region) {
		this.name = name;
		this.region = region;
		neighbors = new LinkedHashMap<City, Integer>();
		owners = new Player[3];
	}
	
	public boolean addOwner(Player player)
	{
		if(numOwners() == 3)
			return false;
		owners[numOwners()] = player;
		return true;
	}
	
	public int numOwners()
	{
		int num = 0;
		for(int i = 0; i < owners.length; i++)
			if(owners[i] != null)
				num++;
		return num;
	}
	
	public void addNeighbor(City dest, int weight) {
		neighbors.put(dest, weight);
	}
	public LinkedHashMap<City, Integer> getNeighbors() {
		return neighbors;
	}
	
	public int getCost() {
		return cost;
	}
	public String getName() {
		return name;
	}
	public String getRegion() {
		return region;
	}
	public boolean isActive() {
		return isActive;
	}
	public void updatePriority(int p) {
		priority = p;
	}
	public int compareTo(Object o) {
		City c = (City) o;
		if(neighbors.containsKey(c))
			return neighbors.get(c);
		
		return -1;
	}
	
	public boolean equals(Object o) {
		City c = (City) o;
		if(c.getName().equals(name))
			return true;
		return false;
	}
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	public String toString() {
		return name;
	}
}
