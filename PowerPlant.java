import java.util.ArrayList;

public class PowerPlant implements Comparable<PowerPlant> {
	
	private int num;
	private int power;
	private int consumedMats;
	private int mats;
	private String type;
	
	public PowerPlant(int num, int power, int consumed, String type) {
		this.num = num;
		this.power = power;
		consumedMats = consumed;
		mats = 0;
		this.type = type;
	}
	
	public String getType()
	{
		return type;
	}
	
	public int getNum() {
		return num;
	}
	
	public int getPower() {
		return power;
	}
	
	public int materialsTillFull()
	{
		return consumedMats * 2 - mats;
	}
	
	public void addMaterial(int amt)
	{
		mats += amt;
	}
	
	public boolean canPowerCity() 
	{
		if(mats - consumedMats >= 0)
			return true;
		return false;
	}
	
	public void consumeMats()
	{
		mats -= consumedMats;
	}
	
	public int compareTo(PowerPlant p) {
		if(num > p.getNum())
			return 1;
		else if(num < p.getNum())
			return -1;
		return 0;
	}
}
