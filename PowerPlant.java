import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class PowerPlant implements Comparable<PowerPlant> {
	
	private int num;
	private int power;
	private TreeMap<String, Integer> consumedMats;
	private TreeMap<String, Integer> mats;
	
	public PowerPlant(int num, int power, TreeMap<String, Integer> consumed) {
		this.num = num;
		this.power = power;
		consumedMats = consumed;
		mats = new TreeMap<String, Integer>();
	}
	
	public int getNum() {
		return num;
	}
	
	public int getPower() {
		return power;
	}
	
	public TreeMap<String, Integer> materialsTillFull()
	{
		TreeMap<String, Integer> toReturn = new TreeMap<String, Integer>();
		Set<String> set = consumedMats.keySet();
		Iterator<String> itr = set.iterator();
		while(itr.hasNext())
		{
			String resourceType = itr.next();
			int number = (consumedMats.get(resourceType) * 2) - mats.get(resourceType);
			toReturn.put(resourceType, number);
		}
		return toReturn;
	}
	
	public void addMaterial(TreeMap<String, Integer> materials)
	{
		Set<String> set = materials.keySet();
		Iterator<String> itr = set.iterator();
		while(itr.hasNext())
		{
			String resourceType = itr.next();
			mats.put(resourceType, mats.get(resourceType) + materials.get(resourceType));
		}
	}
	
	public boolean canPowerCity() 
	{
		Set<String> set = consumedMats.keySet();
		Iterator<String> itr = set.iterator();
		while(itr.hasNext())
		{
			String type = itr.next();
			if(consumedMats.get(type) > mats.get(type))
				return false;
		}
		return true;
	}
	
	public void consumeMats()
	{
		Set<String> set = mats.keySet();
		Iterator<String> itr = set.iterator();
		while(itr.hasNext())
		{
			String type = itr.next();
			mats.put(type, mats.get(type) - consumedMats.get(type));
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
