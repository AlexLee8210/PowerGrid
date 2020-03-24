import java.util.ArrayList;
import java.util.TreeMap;

public class City implements Comparable<City> {
	
	private int cost;
	private String name;
	private String region;
	private Player[] owners;
	private TreeMap<Integer, ArrayList<City>> neighbors;
	private boolean isActive;
	
	
	public City(String name) {
		owners = new Player[3];
		this.name = name;
		neighbors = new TreeMap<Integer, ArrayList<City>>();
	}
	
	public City(String name, String region) {
		owners = new Player[3];
		this.name = name;
		this.region = region;
		neighbors = new TreeMap<Integer, ArrayList<City>>();
	}
	
	public void addNeighbor(City dest, int weight) {
		ArrayList<City> temp;
		if(neighbors.containsKey(weight))
			temp = neighbors.get(weight);	
		else
			temp = new ArrayList<>();
		temp.add(dest);
		neighbors.put(weight, temp);
	}
	public TreeMap<Integer, ArrayList<City>> getNeighbors() {
		return neighbors;
	}
	
	public boolean isFull() {
		return owners[2] != null;
	}
	public int getCost() {
		return cost;
	}
	public void buyCity(Player p) {
		for(int i = 0; i < 2; i++)
			if(owners[i] == null)
				owners[i] = p;
	}
	public String getName() {
		return name;
	}
	public String getRegion() {
		return region;
	}
	public Player[] getOwners() {
		return owners;
	}
	public boolean isActive() {
		return isActive;
	}
	
	public int compareTo(City c) {
		return name.compareTo(c.getName());
	}
}
