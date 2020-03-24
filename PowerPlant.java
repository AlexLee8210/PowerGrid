
public class PowerPlant implements Comparable<PowerPlant> {
	
	private int num;
	private int power;
	
	public PowerPlant(int num, int power) {
		this.num = num;
		this.power = power;
	}
	
	public int getNum() {
		return num;
	}
	public int getPower() {
		return power;
	}
	
	public int compareTo(PowerPlant p) {
		if(num > p.getNum())
			return 1;
		else if(num < p.getNum())
			return -1;
		return 0;
	}
}
