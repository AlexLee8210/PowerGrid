import java.util.ArrayList;

public class PowerPlant implements Comparable {
	
	private int num;
	private int power;
	private int consumedMats;
	private int mats;
	private int numCoal;//Only for hybrid
	private int numOil;//Only for hybrid
	private String type;
	
	public PowerPlant(int num, int power, int consumed, String type) {
		this.num = num;
		this.power = power;
		consumedMats = consumed;
		mats = 0;
		this.type = type;
		numCoal = 0;
		numOil = 0;
	}
	
	public String getType()
	{
		return type;
	}
	
	public int getNum() {
		return num;
	}
	
	public int getMats()
	{
		return mats;
	}
	
	public int getNumCoal() {
		return numCoal;
	}
	
	public int getNumOil() {
		return numOil;
	}
	
	public int getPower() {
		return power;
	}
	
	public int materialsTillFull()
	{
		if(getType().equals("hybrid"))
		{
			return consumedMats * 2 - (numCoal+numOil);
		}
		return consumedMats * 2 - mats;
	}
	
	public void addMaterial(int amt)
	{
		mats += amt;
	}
	
	public void addMaterialHybrid(int coal, int oil)
	{
		numCoal += coal;
		numOil += oil;
	}
	
	public boolean canPowerCity() 
	{
		if(getType().equals("hybrid"))
			return (numCoal+numOil) - consumedMats >= 0;
		return (mats - consumedMats >= 0);
	}
	
	public boolean isFull()
	{
		if(type.equals("hybrid"))
			return (numCoal+numOil) == (consumedMats*2);
		else if(type.equals("wind"))
			return true;
		return mats == (consumedMats*2);
	}
	
	
	public void consumeMats()
	{
		if(getType().equals("hybrid"))
		{
			if(numOil >= consumedMats) {
				numOil -= consumedMats;
			}	
			else if(numCoal >= consumedMats) {
				numCoal -= consumedMats;
			}	
			else if(numCoal > numOil) {
				numCoal -= (consumedMats-1);
				numOil -= 1;
			}
			else
			{
				numOil -= (consumedMats-1);
				numCoal -= 1;
			}	
		}
		else if(!getType().equals("wind"))
			mats -= consumedMats;
	}
	
	public int compareTo(Object o) {
		PowerPlant p = (PowerPlant) o;
		return num - p.getNum();
	}
	public boolean equals(Object o) {
		PowerPlant p = (PowerPlant) o;
		return num == p.getNum();
	}
	public String toString() // for testing
	{
		return "" + num/* getNum() + " " + getPower() + " " + consumedMats + " " + getType() */;
	}
}
