//import java.io.*;
//import java.util.*;
//import static java.lang.System.out;
//
//public class GraphRunner {
//	public static void main(String[] args) throws IOException {
//		Scanner input = new Scanner(new File("src/cities.txt"));
//		Graph graph = new Graph();
//		while(input.hasNext()) {
//			input.nextLine();
//			String region = input.nextLine();
//			for(int i = 0; i < 7; i++) {
//				String c1 = input.nextLine();
//				int num = Integer.parseInt(input.nextLine());
//				for(int j = 0; j < num; j++) {
//					
//					StringTokenizer st = new StringTokenizer(input.nextLine(), ";");
//					String c2 = st.nextToken();
//					int weight = Integer.parseInt(st.nextToken());
//					//out.println(c1 + "-" + c2);
//					graph.addCity(c1, c2, weight);
//				}
//			}
//		}
//		HashMap<City, ArrayList<Object>> result = graph.getShortestPathAll(graph.get("Seattle"));
//		ArrayList<City> result2 = graph.getShortestPath(graph.get("Seattle"), graph.get("Houston"));
//		out.println(result);
//		out.println(result2);
//		out.println(graph.getShortestPathCost(graph.get("Seattle"), graph.get("Houston")));
////		HashMap<String, City> cities = graph.cities;
////		Iterator<City> iter = cities.get("Seattle").getNeighbors().keySet().iterator();
////		out.println(cities.get("Seattle").getNeighbors());
////		out.println(iter.next().getNeighbors());
////		out.println(cities.get("Seattle").getNeighbors().get(cities.get("Portland")));
//	} 
//}
