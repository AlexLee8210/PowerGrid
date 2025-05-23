import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class City implements Comparable {
	
	private int cost;
	private String name;
	private String region;
	private LinkedHashMap<City, Integer> neighbors;
	private boolean isActive;
	private Player[] owners;
	private Point point;
	
	public City(String name) {
		this.name = name;
		neighbors = new LinkedHashMap<City, Integer>();
		owners = new Player[3];
		point = new Point();
		cost = 10;
	}
	public void setRegion(String r) {
		region = r;
	}
	public void setPoint(Point p) {
		point = p;
	}
	public Point getPoint() {
		return point;
	}
	public void addNeighbor(City dest, int weight) {
		neighbors.put(dest, weight);
	}
	public LinkedHashMap<City, Integer> getNeighbors() {
		return neighbors;
	}
	public boolean addOwner(Player p) {
		if(numOwners() == 3)
			return false;
		owners[numOwners()] = p;
		cost+=5;
		return true;
	}
	public int numOwners()
	{
		int num = 0;
		for(int i = 0; i < owners.length; i++)
			if(owners[i] != null) {
				num++;
				break;
			}
		return num;
	}
	public Player[] getOwners() {
		return owners;
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
