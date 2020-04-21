import java.util.ArrayList;
import java.util.Collections;

public class Player implements Comparable<Player> {
	
	private String color;
	private int elektros;
	private ArrayList<PowerPlant> plants;
	private ArrayList<City> citiesOwned;
	
	public Player() {
		elektros = 50;
		plants = new ArrayList<>();
		citiesOwned = new ArrayList<City>();
	}
	public void setColor(String c) {
		color = c;
	}
	public String getColor() {
		return color;
	}
	public int getElektros() {
		return elektros;
	}
	public void buy(int cost)
	{
		elektros -= cost;
	}
	public ArrayList<City> getCities() {
		return citiesOwned;
	}
	public void addCity(City c)
	{
		citiesOwned.add(c);
	}
	public void addPlant(PowerPlant p) {
		if(plants.size() < 4)
			plants.add(p);
		else {
			
		}
		Collections.sort(plants);
	}
	public int getNumPlants() {
		return plants.size();
	}
	public ArrayList<PowerPlant> getPlants(){
		return plants;
	}
	
	public PowerPlant getLargestPlant() {
		return plants.get(plants.size()-1);
	}
	
	public int compareTo(Player p) 
	{
		if(getCities() == null || p.getCities() == null)
		{
			if(getCities() != null)
				return 1;
			else if(p.getCities() != null)
				return -1;
			else if(p.getCities() == null)
				return getLargestPlant().compareTo(p.getLargestPlant());
		}
		if(getCities().size() < p.getCities().size())
			return -1;
		else if(getCities().size() > p.getCities().size())
			return 1;
		return getLargestPlant().compareTo(p.getLargestPlant());
	}
}
