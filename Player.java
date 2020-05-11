import java.util.ArrayList;
import java.util.Collections;

public class Player implements Comparable  {
	
	private String color;
	private int elektros;
	private ArrayList<PowerPlant> plants;
	private ArrayList<City> citiesOwned;
	private int number;
	
	public Player(int num) {
		elektros = 50;
		plants = new ArrayList<>();
		citiesOwned = new ArrayList<City>();
		number = num;
	}
	public int getNumber() {
		return number;
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
	public void addElektros(int amt) {
		elektros += amt;
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
		plants.add(p);
		Collections.sort(plants);
	}
	public int getNumPlants() {
		return plants.size();
	}
	public ArrayList<PowerPlant> getPlants(){
		return plants;
	}
	public ArrayList<String> removePlant(int index)
	{
		PowerPlant removed = plants.remove(index);
		ArrayList<String> toReturn = new ArrayList<String>();
		if(removed.getType().equals("hybrid"))
		{
			for(int i = 0; i < removed.getNumCoal(); i++)
				toReturn.add("coal");
			for(int i = 0; i < removed.getNumOil(); i++)
				toReturn.add("oil");
		}
		else if(!removed.getType().equals("wind"))
		{
			for(int i = 0; i < removed.getMats(); i++)
				toReturn.add(removed.getType());
		}
		return toReturn;
	}
	
	public PowerPlant getLargestPlant() {
		//System.out.println(plants.get(plants.size()-1) + " " + number);
		return plants.get(plants.size()-1);
	}
	public int hashCode() {
		return number;
	}
	public int compareTo(Object o) 
	{
		Player p = (Player) o;
		if(getCities().size() < p.getCities().size())
			return -1;
		else if(getCities().size() > p.getCities().size())
			return 1;
		if(plants.size() == 0) {
			if(p.getPlants().size() == 0) {
				System.out.println("gey1");
				return 0;
			}
			else {
				System.out.println("gey2");
				return -1;
			}
		}
		else if(p.getPlants().size() == 0) {
			if(plants.size() == 0) {
				System.out.println("gey3");
				return 0;
			}
			else {
				System.out.println("gey4");
				return 1;
			}
		}
		return getLargestPlant().compareTo(p.getLargestPlant());
	}
	public boolean equals(Object o) {
		Player p = (Player) o;
		return number == p.getNumber();
	}
	public String toString() {
		return "Player " + number;
	}
}
