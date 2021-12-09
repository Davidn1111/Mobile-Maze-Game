/**
 * 
 */
package edu.wm.cs.cs301.DavidNi.gui;

import edu.wm.cs.cs301.DavidNi.generation.CardinalDirection;
import edu.wm.cs.cs301.DavidNi.generation.Floorplan;
import edu.wm.cs.cs301.DavidNi.generation.Maze;
import edu.wm.cs.cs301.DavidNi.gui.Robot.Direction;

/**
 * @author David Ni
 * JavaDoc comments modified from comments in DistanceSensor by Peter Kempter.
 */

/**
 * ReliableSensor
 * <p>
 * Responsibilities:
 * Find distance to wall.
 * <p>
 * Collaborators:
 * Maze (Floorplan)
 */

public class ReliableSensor implements DistanceSensor {
	
	//Maze referenced by sensor
	protected Maze maze;
	//Direction of sensor.
	protected Direction direction;
	
	/**
	 * Default constructor for ReliableSensor.
	 */
	public ReliableSensor() {
	}

	/**
	 * This method returns the distance to an obstacle (a wallboard) in the sensor's direction.
	 * Distance is measured in the number of cells towards that obstacle. 
	 * e.g. 0 if the current cell has a wallboard in this direction, 
	 * 1 if it is one step in this direction before directly facing a wallboard,
	 * Integer.MaxValue if one looks through the exit into eternity.
	 * <p>
	 * This method requires that the sensor has been given a reference
	 * to the current maze and a mountedDirection by calling 
	 * the corresponding set methods with a parameterized constructor. Confirmed using assert.
	 * 
	 * @param currentPosition is the current location as (x,y) coordinates
	 * @param currentDirection specifies the direction of the robot
	 * @param powersupply is an array of length 1, whose content is modified 
	 * to account for the power consumption for sensing
	 * @return number of steps towards obstacle if obstacle is visible 
	 * in a straight line of sight, Integer.MAX_VALUE otherwise.
	 * @throws Exception with message 
	 * PowerFailure if the power supply is insufficient for the operation
	 * @throws IllegalArgumentException if any parameter is null
	 * or if currentPosition is outside of legal range
	 * ({@code currentPosition[0] < 0 || currentPosition[0] >= width})
	 * ({@code currentPosition[1] < 0 || currentPosition[1] >= height}) 
	 * @throws IndexOutOfBoundsException if the powersupply is out of range
	 * ({@code powersupply < 0}) 
	 */
	@Override
	public int distanceToObstacle(int[] currentPosition, CardinalDirection currentDirection, float[] powersupply) throws Exception {
		//Throw exceptions:
		//Power supply is insufficient
		if (powersupply[0] < getEnergyConsumptionForSensing())
			throw new Exception("PowerFailure");
		//Any parameter is null
		if(currentPosition == null || currentDirection == null || powersupply == null || currentPosition[0] >= maze.getWidth() || currentPosition[1] >= maze.getHeight())
			throw new IllegalArgumentException();
		//Power supply is out of range (is negative)
		if(powersupply[0] < 0)
			throw new IndexOutOfBoundsException();
		
		//Make sure the current maze reference has been passed to the sensor.
		assert(this.maze != null);
		//Make sure the direction of the sensor has been previously set.
		assert(this.direction != null);
		
		int counter = 0;
		Floorplan floorplan = this.maze.getFloorplan();
		CardinalDirection sensorDir = getCardinalDir(currentDirection);
		int x = currentPosition[0];
		int y = currentPosition[1];
		
		while(floorplan.hasNoWall(x, y, sensorDir))
		{
			//if sensor finds the exit
			if (this.maze.getExitPosition()[0] == x && this.maze.getExitPosition()[1] == y && 
					//check if missing wallboard in question is the exit
					((y == 0 && sensorDir == CardinalDirection.North) ||
					(y == (maze.getHeight()-1) && sensorDir == CardinalDirection.South) ||
					(x == 0 && sensorDir == CardinalDirection.West) ||
					(x == (maze.getWidth()-1) && sensorDir == CardinalDirection.East))) {
				
				counter = Integer.MAX_VALUE;
				break;
			}
			
			//continue to scan for walls
			switch(sensorDir) {
			case North:
				y--;
				break;
			case South:
				y++;
				break;
			case East:
				x++;
				break;
			case West:
				x--;
				break;
			default:
				break;
			}
			counter++;
		}
		
		powersupply[0] -= getEnergyConsumptionForSensing();
		return (counter);
	}

	/**
	 * This method provides a maze reference for the sensor.
	 * Maze info is used to calculate distances.
	 * @param maze the maze for this game
	 * @throws IllegalArgumentException if parameter is null
	 * or if it does not contain a floor plan
	 */
	@Override
	public void setMaze(Maze maze) {
		this.maze = maze;
	}
	
	/**
	 * This method sets the direction of the sensor to a given direction.
	 * Directions are forward, backward, left, right; and are relative to the front of the robot.
	 * Angle of directions are multiples of 90 degrees.
	 * @param mountedDirection is the sensor's relative direction
	 * @throws IllegalArgumentException if parameter is null
	 */
	@Override
	public void setSensorDirection(Direction mountedDirection) {
		this.direction = mountedDirection;
	}

	/**
	 * This method returns the amount of energy the sensor uses for 
	 * calculating the distance to an obstacle exactly once.
	 * This amount is a fixed constant of 1 for a sensor.
	 * @return the amount of energy used for using the sensor once
	 */
	@Override
	public float getEnergyConsumptionForSensing() {
		return 1;
	}
	
	/**
	 * This method returns the cardinal direction of a sensor based on the current direction of the robot and the mounted direction of the sensor.
	 * Assumes that sensor has had mounted direction set by setSensorDirection().
	 * @param currentDirection the current direction of the robot
	 * @return the cardinal direction of the sensor
	 */
	public CardinalDirection getCardinalDir(CardinalDirection currentDirection) {
		switch(this.direction) {
		case FORWARD:
			return(currentDirection);
		case BACKWARD:
			return(currentDirection.oppositeDirection());
		case RIGHT:
			return(currentDirection.rotateClockwise());
		case LEFT:
			for(int i =0;i < 3; i++)
				currentDirection = currentDirection.rotateClockwise();
			return(currentDirection);
		default:
			return null;
		}
	}
	/**
	 * Used for P4 UnreliableSensor.
	 * Sufficient to throw UnsupportedOperationException (for now)
	 */
	@Override
	public void startFailureAndRepairProcess(int meanTimeBetweenFailures, int meanTimeToRepair) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Used for P4 UnreliableSensor.
	 * Sufficient to throw UnsupportedOperationException (for now)
	 */
	@Override
	public void stopFailureAndRepairProcess() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * This method gets the sensor's operational status.
	 * @return true if sensor is operational, false otherwise.
	 */
	@Override
	public boolean getStatus() {
		return true;
	}

}
