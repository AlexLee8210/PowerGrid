import java.util.ArrayList;

public class PowerPlant implements Comparable<PowerPlant> {
	
	private int num;
	private int power;
	private ArrayList<String> consumedMats;
	private ArrayList<String> mats;
	private boolean isHybrid;
	
	public PowerPlant(int num, int power, ArrayList<String> consumed, boolean isHybrid) {
		this.num = num;
		this.power = power;
		consumedMats = consumed;
		mats = new ArrayList<String>();
		this.isHybrid = isHybrid;
	}
	
	public boolean isHybrid()
	{
		return isHybrid;
	}
	
	public int getNum() {
		return num;
	}
	
	public int getPower() {
		return power;
	}
	
	public int materialsTillFull()
	{
		if(mats == null)
			return consumedMats.size() * 2;
		return consumedMats.size() * 2 - mats.size();
	}
	
	public void addMaterial(ArrayList<String> materials)
	{
		for(int i = 0; i < materials.size(); i++)
			mats.add(materials.get(i));
	}
	
	public boolean canPowerCity() 
	{
		if(mats == null)
			return false;
		else if(mats.size() - consumedMats.size() >= 0)
			return true;
		return false;
	}
	
	public void consumeMats()
	{
		int count = 0;
		while(count != consumedMats.size())
		{
			mats.remove(0);
			count++;
		}
	}
	
	public int compareTo(PowerPlant p) {
		if(num > p.getNum())
			return 1;
		else if(num < p.getNum())
			return -1;
		return 0;
	}
}
