import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import static java.lang.System.out;

public class Graph {
	
	public HashMap<String, City> cities;
	
	public Graph() {
		cities = new HashMap<>();
	}
	public void addCity(String c1, String c2, int weight) {
		City source = new City(c1);
		City destination = new City(c2);
		
		if(cities.containsKey(c1))
			source = cities.get(c1);
		else
			cities.put(c1, source);
		if(cities.containsKey(c2))
			destination = cities.get(c2);
		else
			cities.put(c2, destination);
		
		source.addNeighbor(destination, weight);
    }
	public City get(String name) {
		return cities.get(name);
	}
	
	private HashMap<String, Object> makeSPT(City src) {
		HashMap<City, Integer> totalCosts = new HashMap<>();
		HashMap<City, City> prevNodes = new HashMap<>();
		LinkedHashMap<City, Integer> minPQ = new LinkedHashMap<>();
		ArrayList<City> visited = new ArrayList<>();
		
		totalCosts.put(src, 0);
		minPQ.put(src, 0);
		visited.add(src);
		prevNodes.put(src, src);
		for(Entry<String, City> e: cities.entrySet()) {
			City c = e.getValue();
			if(!c.equals(src))
				totalCosts.put(c, Integer.MAX_VALUE);
		}
		
		while(!minPQ.isEmpty()) {
			City currentCity = minPQ.keySet().iterator().next();
			minPQ.remove(currentCity);
			HashMap<City, Integer> neighbors = currentCity.getNeighbors(); //neighbors of the city we are at
			for(City neighbor: neighbors.keySet())  {
				if(!visited.contains(neighbor)) {
					int path = totalCosts.get(currentCity) + neighbors.get(neighbor);
					if(path < totalCosts.get(neighbor)) {
						totalCosts.put(neighbor, path);
						prevNodes.put(neighbor, currentCity);
						minPQ.put(neighbor, path);
						minPQ = sortByValue(minPQ);
					}
				}
			}
			visited.add(currentCity);
		}
		HashMap<String, Object> maps = new HashMap<>();
		maps.put("totalCosts", totalCosts);
		maps.put("prevNodes", prevNodes);
		return maps;
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<City, ArrayList<Object>> getShortestPathAll(City src) { //returns shortest path to all cities
		HashMap<String, Object> res = makeSPT(src);
		HashMap<City, Integer> totalCosts = (HashMap<City, Integer>) res.get("totalCosts");
		HashMap<City, City> prevNodes = (HashMap<City, City>) res.get("prevNodes");
		HashMap<City, ArrayList<Object>> result = new HashMap<>();
		for(City c: prevNodes.keySet()) {
			ArrayList<Object> temp = new ArrayList<>();
			temp.add(prevNodes.get(c));
			temp.add(totalCosts.get(c));
			result.put(c, temp);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<City> getShortestPath(City src, City dest) { //returns shortest path from src to dest
		HashMap<String, Object> res = makeSPT(src);
		HashMap<City, City> prevNodes = (HashMap<City, City>) res.get("prevNodes");
		
		ArrayList<City> result = new ArrayList<>();
		City cc = dest;
		result.add(cc);
		while(!cc.equals(src)) {
			result.add(prevNodes.get(cc));
			cc = prevNodes.get(cc);
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	public int getShortestPathCost(City src, City dest) { //returns shortest path from src to dest
		HashMap<String, Object> res = makeSPT(src);
		HashMap<City, Integer> totalCosts = (HashMap<City, Integer>) res.get("totalCosts");
		
		return totalCosts.get(dest);
	}
	
	private LinkedHashMap<City, Integer> sortByValue(LinkedHashMap<City, Integer> hm) { 
		//I used a hashmap instead of a min priority queue so this is the sorting part (sorted by value not key)
        ArrayList<Map.Entry<City, Integer> > list = new ArrayList<Map.Entry<City, Integer>>(hm.entrySet()); 
        Collections.sort(list, new Comparator<Map.Entry<City, Integer> >() { 
            public int compare(Map.Entry<City, Integer> o1, Map.Entry<City, Integer> o2) { 
                return (o1.getValue()).compareTo(o2.getValue()); 
            } 
        }); 
        LinkedHashMap<City, Integer> temp = new LinkedHashMap<>(); 
        for (Map.Entry<City, Integer> aa : list)
            temp.put(aa.getKey(), aa.getValue());
        return temp; 
    } 
}
