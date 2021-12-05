package edu.wm.cs.cs301.DavidNi.generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class MazeBuilderBoruvka extends MazeBuilder implements Runnable{
	
	private HashMap<String,Integer> edgeWeights = new HashMap<String,Integer>();

	
	/**
	 * Constructor for MazeBuilderBoruvka.
	 * Generates a maze using Boruvka's algorithm.
	 */
	public MazeBuilderBoruvka() {
		super();
		System.out.println("MazeBuilderBoruvka uses Boruvka's algorithm to generate maze.");
	}
	
	
	/**
	 * This method loops through each cell of the maze and assigns a random weight to each internal edge (this mapping is stored in the dictionary).
	 * Note: Each internal edge has 2 different representations, but the assigned weights for both representations are the same.
	 * <p>
	 * Done by: 
	 * Iterate through each cell of the maze starting at (0,0).
	 * For each iteration generate a unique random weight (not used yet) using the provided seed
	 * For each nonborder wallboard with values (x,y,right), it is the same as (x+1,y,left) so give them the same weight
	 * For each nonborder wallboard with values (x,y,down), it is the same as (x,y+1, up) so give them the same weight
	 */
	private void populateEdgeWeights() {
		Integer weight = null;
		
		ArrayList<Integer> weights = new ArrayList<Integer>();
		
		//get all internal edges
		for(int x = 0; x < (width); x++) {
			for (int y = 0; y < (height);y++) {
				Wallboard wDown = new Wallboard(x,y,CardinalDirection.South);
				Wallboard wUp = new Wallboard(x,y+1,CardinalDirection.North);
				Wallboard wRight = new Wallboard(x,y,CardinalDirection.East);
				Wallboard wLeft = new Wallboard(x+1,y,CardinalDirection.West);
				boolean flag = true;
				
				while(flag) {
					weight = random.nextInt();
					if(!weights.contains(weight)) {
						weights.add(weight);
						flag = false;
					}
				}
				flag = true;
				if(!floorplan.isPartOfBorder(wDown)) {
					edgeWeights.put(wallboardToString(wDown), weight);
					edgeWeights.put(wallboardToString(wUp), weight);
				}
				
				while(flag) {
					weight = random.nextInt();
					if(!weights.contains(weight)) {
						weights.add(weight);
						flag = false;
					}
				}
				flag = true;
				if(!floorplan.isPartOfBorder(wRight)) {
					edgeWeights.put(wallboardToString(wRight), weight);
					edgeWeights.put(wallboardToString(wLeft), weight);
				}
			}
		}
		//Pseudocode
		/* Iterate through each cell of the maze starting at (0,0).
		 * For each iteration generate a random weight using the provided seed
		 * For each nonborder wallboard with values (x,y,right), it is the same as (x+1,y,left) so give them the same weight
		 * For each nonborder wallboard with values (x,y,down), it is the same as (x,y+1, up) so give them the same weight
		 */
	}
	
	/**
	 * This method returns a unique value for any internal wallboard.
	 * Assumes populateEdgeWeights has already been called before.
	 * Edge value is randomly chosen, but further method calls to the same wallboard will get you the same value.
	 * @param w any nonborder wallboard in the maze
	 * @return weight of the wallboard/edge
	 */
	public int getEdgeWeight(Wallboard w) {
		return (edgeWeights.get(wallboardToString(w)));
		
		//Pseudocode
		/* Assume populateEdgeWeights has been called already
		 * return the value stored in the dictionary corresponding to the given wallboard
		 */
	}
	
	/**
	 * This method generates pathways for a maze using Boruvka's algorithm.
	 * Boruvka's algorithm finds the minimum spanning tree for an undirected graph.
	 * Consider the maze's cells as nodes of the graph/spanning tree.
	 * Consider the internal wallboards between cells as the edges of the graph/spanning tree.
	 * Thus, every edge in the minimum spanning tree (from Boruvka's) can be removed from the original graph/maze to create pathways in the maze.
	 * Since this is a minimum spanning tree, the pathways of the maze must reach all cells and will generate a perfect maze when no rooms are present.
	 * <p>
	 * Done by:
	 * Populating all edges with unique weights.
	 * Create a list of individual components in the graph
	 * Each component in the list is a list of cells/nodes
	 * <p>
	 * Initialize all vertices as individual components
	 * While there are more than one components in the maze, apply the following to each component:
	 * <p>
	 * 		1.) Find the cheapest wallboard (edge) connecting this component to another (using helper object method)
	 * <p>
	 * 		2.) Delete the wallboard (equivalent to adding the adding the edge to the minimum spanning tree)
	 * <p>
	 * 		3.) Combine the components into one component (merge the lists into one)
	 */
	@Override
	protected void generatePathways() {
		edgeWeights = new HashMap<String,Integer>();
		System.gc();
		populateEdgeWeights();


		//list of components in maze
		//components are lists of int[], which are the (x,y) coordinates of cells
		ArrayList<ArrayList<String>> components = new ArrayList<ArrayList<String>>();
		
		//get all nonRoom cells and set them to individual components
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (!floorplan.isInRoom(x, y)) {
					ArrayList<String> component = new ArrayList<String>();
					component.add(coordToString(x,y));
					components.add(component);
				}
			}
		}
		
		//find all exits of room and treat them like complete components
		ArrayList<ArrayList<String>> rooms = findRooms(components);
		for (ArrayList<String> room: rooms) {
			//room does exist
			if(room != null)
				components.add(room);
		}
		
		// While there is more than one component in the maze:
		while(components.size() > 1) {
			// Container for holding newly merged components
			ArrayList<ArrayList<String>> edgesToRemove = new ArrayList<ArrayList<String>>();
			
			// Get all cheapest wallboards to remove from the current maze
			for (int i = 0; i < components.size(); i++) {
				edgesToRemove.add(cheapestEdge(components.get(i)));
			}
			
			// Remove all the cheapest wallboards
			for (int i = 0; i < edgesToRemove.size(); i++) {
				String currEdge = edgesToRemove.get(i).get(0);
				String neighbor = edgesToRemove.get(i).get(1);
				// Parse wall info so floorplan can use.
				String[] wallInfo = currEdge.split(",");
				CardinalDirection dir = null;
				switch (wallInfo[2]) {
					case "North":
						dir = CardinalDirection.North;
						break;
					case "South":
						dir = CardinalDirection.South;
						break;
					case "West":
						dir = CardinalDirection.West;
						break;
					case "East":
						dir = CardinalDirection.East;
						break;
					default:
						break;
				}
				components.get(i).add(neighbor);
				// If wall was not removed, merge of components and wall removal required
				if(floorplan.hasWall(Integer.parseInt(wallInfo[0]), Integer.parseInt(wallInfo[1]), dir))
				{
					//remove the wall
					floorplan.deleteWallboard(stringToWallboard(currEdge));
				}
			}
			
			// Merge all components together
			components = mergeComponentsList(components);
		}
		
		//Pseudocode
		/* Call populateEdgeWeights
		 * Create a list of individual components in the graph
		 * Each component in the list is a list of cells/nodes
		 * 
		 * Initialize all vertices as individual components
		 * While there are more than one components in the maze, apply the following to each component:
		 * 		1.) Find the cheapest wallboard (edge) connecting this component to another (using helper object method)
		 * 		2.) Delete the wallboard (equivalent to adding the adding the edge to the minimum spanning tree)
		 * 		3.) Combine the components into one component (merge the lists into one)
		 */
	}
	
	/**
	 * This method finds all exit cells of a room and returns them as list/component.
	 * @param components initial nonroom components (use referencing to remove all intial components that are also room exits,prevents overlapping nodes in graph)
	 * @return list of all room endpoints (by treating rooms as a single entity, only endpoints are needed for boruvka's)
	 */
	protected ArrayList<ArrayList<String>> findRooms(ArrayList<ArrayList<String>> components) {
		
		// all current room components under consideration
		// should be consist of all rooms after merging
		ArrayList<ArrayList<String>> rooms = new ArrayList<ArrayList<String>>();
		
		// find all room cells and consider them and their neighbors as one component
		for(int x = 0; x < width; x ++) {
			for(int y = 0; y < height; y ++) {
				ArrayList<String> roomCandidates = new ArrayList<String>();
				if(floorplan.isInRoom(x, y)) {
					roomCandidates.add(coordToString(x,y));
					if(floorplan.hasNoWall(x, y, CardinalDirection.North))
						roomCandidates.add(coordToString(x,y-1));
					if(floorplan.hasNoWall(x, y, CardinalDirection.South))
						roomCandidates.add(coordToString(x,y+1));
					if(floorplan.hasNoWall(x, y, CardinalDirection.East))
						roomCandidates.add(coordToString(x+1,y));
					if(floorplan.hasNoWall(x, y, CardinalDirection.West))
						roomCandidates.add(coordToString(x-1,y));
					rooms.add(roomCandidates);
				}
			}
		}
		
		rooms = mergeComponentsList(rooms);
		removeInRoomCells(rooms);
		// loop through components (only individual nonrooms at this point)
		// remove all components that share a value with room
		for(ArrayList<String> str : components) {
			for(ArrayList<String> room : rooms) {
				if(room.contains(str.get(0))) {
					components.remove(str);
				}
			}
		}
	
		return rooms;
	}
	
	/**
	 * This method remove all cells in a room component that are considered part of the room.
	 * This results in only the endpoints (nonroom cells that can enter the room) to be considered part of the component for Boruvka's
	 * @param rooms list of rooms
	 */
	private void removeInRoomCells (ArrayList<ArrayList<String>> rooms) {
		for(ArrayList<String> room : rooms) {
			for (String str: room) {
				if (floorplan.isInRoom(stringToCoord(str)[0], stringToCoord(str)[1])) {
				}
			}
		}
	}

	/**
	 * This method recieves a list of components and merges all components that share an element.
	 * Done through recusive calls.
	 * @param components list of components that should be merged
	 * @return list of merged components
	 */
	public ArrayList<ArrayList<String>> mergeComponentsList (ArrayList<ArrayList<String>> components) {
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		//flag to determine if component was merged successfully
		boolean flag = true;
		
		// For each component try to find a set in result that intersect with it
		// If one is found, merge them.
		// Otherwise, add the current component to result
		for (int i = 0; i < components.size(); i++) {
			flag = true;
			for(int j = 0; j < components.get(i).size(); j++) {
				for (int k = 0; k < result.size(); k++) {
					// Found intersection, so merge components
					if (result.get(k).contains(components.get(i).get(j))) {
						result.set(k, mergeComponents(components.get(i),result.get(k)));
						flag = false;
						break;
					}
				}
				if(!flag)
					break;
			}
			//no intersection found, so add current component to result
			if (flag) {
				result.add(components.get(i));
			}
		}
		
		// Recursively call the function until result's size is the same as the input's size (no merges were done)
		return result.size() == components.size() ? result: mergeComponentsList(result);
	}
	
	/**
	 * This method merges the two received components and returns their union (removes redundant elements).
	 * @param c1, first component to be combined
	 * @param c2, second component to be combined
	 * @return union of the two components
	 */
	private ArrayList<String> mergeComponents (ArrayList<String> c1, ArrayList<String> c2) {
		Set<String> strSet = new LinkedHashSet<>(c1);
		strSet.addAll(c2);
		ArrayList<String> union = new ArrayList<> (strSet);
		return union;
	}
	
	/**
	 * This method returns the cheapest edge from a given component, along with the cell that edge connects to.
	 * Does not account for ties, since edge weights are all unique
	 * @param component list of cells representing a connected component.
	 * @return ArrayList, with first index containing the cheapest edge and the second index containing the connected cell; both are in string form.
	 */
	private ArrayList<String> cheapestEdge (ArrayList<String> component) {
		int cheapestWeight = Integer.MAX_VALUE;
		String connectedCell = null;
		String cheapestEdge = null;
		String currWall = null;
		
		for(int i = 0; i < component.size();i ++) {
			int[] currCoord = stringToCoord(component.get(i));
			
			//cell to the right is not part of component
			if(!component.contains((currCoord[0]+1) + "," + currCoord[1])) {
				//wallboard to the right is not a border and exists
				currWall = currCoord[0] + "," + currCoord[1] + "," + "East";
				Wallboard w = stringToWallboard(currWall);
				if(floorplan.hasWall(currCoord[0], currCoord[1], CardinalDirection.East) && !floorplan.isPartOfBorder(w)) {
					if(getEdgeWeight(w) < cheapestWeight) {
						cheapestEdge = currWall;
						connectedCell = (currCoord[0]+1) + "," + currCoord[1];
						cheapestWeight = getEdgeWeight(w);
					}
				}
			}
			
			//cell to the left is not part of component
			if(!component.contains((currCoord[0]-1) + "," + currCoord[1])) {
				//wallboard to the left is not a border and exists
				currWall = currCoord[0] + "," + currCoord[1] + "," + "West";
				Wallboard w = stringToWallboard(currWall);
				if(floorplan.hasWall(currCoord[0], currCoord[1], CardinalDirection.West) && !floorplan.isPartOfBorder(w)) {
					if(getEdgeWeight(w)< cheapestWeight) {
						cheapestEdge = currWall;
						connectedCell = (currCoord[0]-1) + "," + currCoord[1];
						cheapestWeight = getEdgeWeight(w);
					}
				}
			}
			
			//cell to the top is not part of component
			if(!component.contains(currCoord[0] + "," + (currCoord[1]-1))) {
				//wallboard to the right is not a border and exists
				currWall = currCoord[0] + "," + currCoord[1] + "," + "North";
				Wallboard w = stringToWallboard(currWall);
				if(floorplan.hasWall(currCoord[0], currCoord[1], CardinalDirection.North) && !floorplan.isPartOfBorder(w)) {
					if(getEdgeWeight(w) < cheapestWeight) {
						cheapestEdge = currWall;
						connectedCell = currCoord[0] + "," + (currCoord[1]-1);
						cheapestWeight = getEdgeWeight(w);
					}
				}
			}
			
			//cell to the bottom is not part of component
			if(!component.contains(currCoord[0] + "," + (currCoord[1]+1))) {
				//wallboard to the right is not a border and exists
				currWall = currCoord[0] + "," + currCoord[1] + "," + "South";
				Wallboard w = stringToWallboard(currWall);
				if(floorplan.hasWall(currCoord[0], currCoord[1], CardinalDirection.South) && !floorplan.isPartOfBorder(w)) {
					if(getEdgeWeight(w) < cheapestWeight) {
						cheapestEdge = currWall;
						connectedCell = currCoord[0] + "," + (currCoord[1]+1);
						cheapestWeight = getEdgeWeight(w);
					}
				}
			}
		}
		ArrayList <String> output = new ArrayList<String>();
		output.add(cheapestEdge);
		output.add(connectedCell);
		return output;
	}
	
	
/*
 * Methods for turning wallboards and coordinates into Strings.
 * Used for easier hashmapping and references in ArrayLists.
 */
	
	/**
	 * Method turns wallboards into string representation for hashmapping.
	 * @param w any Wallboard
	 * @return string representing wallboard (in form "x,y,dir")
	 */
	private String wallboardToString(Wallboard w) {
		return(w.getX() + "," + w.getY() + "," + w.getDirection());
	}
	
	/**
	 * Method that turns string representation of wallboards back into Wallboard objects (for use with floorplan).
	 * @param w string representing wallboard (in form "x,y,dir")
	 * @return Wallboard(x,y,dir)
	 */
	private Wallboard stringToWallboard(String w) {
		String[] temp = w.split(",");
		CardinalDirection dir = null;
		switch (temp[2]) {
			case "North":
				dir = CardinalDirection.North;
				break;
			case "South":
				dir = CardinalDirection.South;
				break;
			case "West":
				dir = CardinalDirection.West;
				break;
			case "East":
				dir = CardinalDirection.East;
				break;
			default:
				break;
		}
		
		Wallboard result = new Wallboard(Integer.parseInt(temp[0]),Integer.parseInt(temp[1]),dir);
		return result;
	}
	
	/**
	 * Method turns coordinates into string representation.
	 * @param x x coordinates
	 * @param y y coordiantes
	 * @returns coordinates in string form: "x,y"
	 */
	private String coordToString (int x, int y) {
		return (x + "," + y);
	}
	
	/**
	 * Method turns string representation of coordinates back into array of integers.
	 * @param coord coordinates in string form: "x,y"
	 * @return integer array representing (x,y)
	 */
	private int[] stringToCoord (String coord) {
		String[] temp = coord.split(",");
		int[] result = {Integer.parseInt(temp[0]), Integer.parseInt(temp[1])};
		return result;
	}
}
