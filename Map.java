import java.util.HashMap;

public class Map {

    static class Graph {
    	
    	private City root;

        Graph() {
        	root = null;
        }
        
        public void addCity(String c1, String c2, int weight) {
            City src = new City(c1);
            City dest = new City(c2);
            src.addNeighbor(dest, weight);
            dest.addNeighbor(src, weight);
            if(root != null)
        		root = src;
        }
        
    }
}