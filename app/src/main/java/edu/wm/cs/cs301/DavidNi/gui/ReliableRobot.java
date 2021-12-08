/**
 * 
 */
package edu.wm.cs.cs301.DavidNi.gui;

import java.util.Arrays;

import edu.wm.cs.cs301.DavidNi.generation.CardinalDirection;
import edu.wm.cs.cs301.DavidNi.generation.Maze;
import edu.wm.cs.cs301.DavidNi.gui.Constants.UserInput;

/**
 * @author David Ni
 * JavaDoc comments modified from comments in Robot by Peter Kempter.
 */

/**
 * ReliableRobot
 * <p>
 * Responsibilities:
 * Move in maze.
 * Get info about surroundings.
 * Track energy use.
 * <p>
 * Collaborators:
 * DistanceSensor.
 * Controller.
 */

public class ReliableRobot implements Robot {
	
	//StatePlaying used by Robot
	protected StatePlaying controller = null;
	//Sensors for robot (front,back,left,right respectively)
	protected DistanceSensor fSensor = null;
	protected DistanceSensor bSensor = null;
	protected DistanceSensor lSensor = null;
	protected DistanceSensor rSensor = null;
	//Odometer counting distance (in cells) traveled by robot
	//Default starts at 0
	protected int odometer = 0;
	//Battery level of robot, starts at 3500
	protected float[] batteryLevel = {3500};
	//Robot "has stopped" status.
	//default starts as false.
	protected boolean stopped = false;
	
	/**
	 * Default constructor for ReliableRobot.
	 */
	public ReliableRobot() {
	}

	/**
	 * Provides the robot with a reference to the StatePlaying to cooperate with.
	 * The StatePlaying serves as the main source of information
	 * for the robot about the current position, the presence of walls, the reaching of an exit.
	 * @param statePlaying is the communication partner for robot
	 * @throws IllegalArgumentException if statePlaying is null or statePlaying has no maze reference.
	 */
	@Override
	public void setStatePlaying(StatePlaying statePlaying) {
		/*Throw exception if:
		 * No controller is given as a reference.
		 * Controller is not in playing state
		 * Controller doesn't have a maze.
		 */
		if(statePlaying == null || statePlaying.getMazeConfiguration() == null)
			throw new IllegalArgumentException();
		
		this.controller = statePlaying;
	}

	/**
	 * This method adds a distance sensor to the robot in a given direction.
	 * <p>
	 * A robot can have at most four sensors in total, and at most one for any direction.
	 * If a robot already has a sensor for the given mounted direction, adding another
	 * sensor will replace/overwrite the current one for that direction with the new one.
	 * @param sensor is the distance sensor to be added
	 * @param mountedDirection is the direction that it points to relative to the robot's forward direction
	 */
	@Override
	public void addDistanceSensor(DistanceSensor sensor, Direction mountedDirection) {
		
		switch (mountedDirection) {
		case FORWARD:
			sensor.setSensorDirection(mountedDirection);
			this.fSensor = sensor;
			break;
		case BACKWARD:
			sensor.setSensorDirection(mountedDirection);
			this.bSensor = sensor;
			break;
		case LEFT:
			sensor.setSensorDirection(mountedDirection);
			this.lSensor = sensor;
			break;
		case RIGHT:
			sensor.setSensorDirection(mountedDirection);
			this.rSensor = sensor;
			break;
		default:
			break;
		}
	}

	/**
	 * This method gets the robot's current (x,y) position.
	 * Asserts method call only occurs when robot has a controller reference.
	 * @return array of length 2, x = array[0], y = array[1]
	 * and ({@code 0 <= x < width, 0 <= y < height}) of the maze
	 * @throws Exception if position is outside of the maze
	 */
	@Override
	public int[] getCurrentPosition() throws Exception {
		//Make sure robot has a controller to communicate with.
		//Unable to get current position otherwise
		assert(controller != null);
		
		//Throw an exception if the robot's position is outside the maze.
		Maze maze = controller.getMazeConfiguration();
		if(this.controller.getCurrentPosition()[0] >= maze.getWidth() || this.controller.getCurrentPosition()[0] < 0
				|| this.controller.getCurrentPosition()[1] >= maze.getHeight() || this.controller.getCurrentPosition()[1] < 0)
			throw new Exception("Robot position outside of maze");
		
		return (this.controller.getCurrentPosition());
	}

	/**
	 * This method returns the robot's current direction.
	 * Asserts method call only occurs when robot has a controller reference.
	 * @return cardinal direction is the robot's current direction in absolute terms
	 */	
	@Override
	public CardinalDirection getCurrentDirection() {
		//Make sure robot has a controller to communicate with.
		//Unable to get direction otherwise
		assert(controller != null);
		
		return (this.controller.getCurrentDirection());
	}

	/**
	 * This method returns the robot's current battery level.
	 * <p>
	 * If battery level reaches 0, then robot stops to function and hasStopped() is true.
	 * @return current battery level, if robot is operational. 
	 */
	@Override
	public float getBatteryLevel() {
		return this.batteryLevel[0];
	}

	/**
	 * This method sets the robot's battery level to a given value.
	 * @param level is the current battery level
	 * @throws IllegalArgumentException if level is negative 
	 */
	@Override
	public void setBatteryLevel(float level) {
		//Throw exception if battery is set to negative value
		//Considered an illegal argument
		if(level < 0)
			throw new IllegalArgumentException();
		
		this.batteryLevel[0] = level;
	}

	/**
	 * This method returns the energy consumption for a full 360 degree rotation.
	 * Quarter turns cost 3 energy, so full rotation costs 12 energy.
	 * @return energy for a full rotation
	 */
	@Override
	public float getEnergyForFullRotation() {
		return 12;
	}

	/**
	 * This method returns energy for taking a single step (cell) forwards.
	 * A single step costs 6 energy.
	 * @return energy for a single step forward
	 */
	@Override
	public float getEnergyForStepForward() {
		return 6;
	}

	/** 
	 * This method returns the distance traveled by the robot (stored in odometer).
	 * <p>
	 * The odometer reading gives the path length if its setting is 0 at the start of the game.
	 * The counter can be reset to 0 with resetOdomoter().
	 * @return the distance traveled measured in single-cell steps forward
	 */
	@Override
	public int getOdometerReading() {
		return this.odometer;
	}

	/**
	 * This method resets odometer counter to zero.
	 */
	@Override
	public void resetOdometer() {
		this.odometer = 0;
	}

	/**
	 * This method turns the robot in place, in a given direction.
	 * Asserts that rotating occurs only when the robot has a controller.
	 * Rotate only if robot has not stopped.
	 * <p>
	 * Can turn left 90 degree, right 90 degrees, or around (180 degrees).
	 * If robot runs out of energy, it stops.
	 * @param turn is the direction to turn and relative to current forward direction. 
	 */
	@Override
	public void rotate(Turn turn) {
		//Make sure robot has a controller reference; cannot rotate otherwise.
		assert(this.controller != null);
		//Make sure robot has not stopped or is not out of energy.
		//Robot should not receive anymore movement commands after stopping, since game should be considered over at this point.
		if(!hasStopped()){
			switch (turn) {
			case LEFT:
				//Make sure controller has enough power for left turn
				//If not, stop the robot.
				if(getBatteryLevel() < (getEnergyForFullRotation()/4))
					stopped = true;
				else {
					//Update power for left turn
					this.batteryLevel[0] = this.batteryLevel[0] - (getEnergyForFullRotation()/4);
					//Code base implementation of UserInput.Left results in a counterclockwise turn (unintuitive)
					//Instead we have robot's left turn move in counterclockwise direction (this is done by UserInput.Right)
					controller.keyDown(UserInput.RIGHT);
				}
				break;
	
			case RIGHT:
				//Make sure controller has enough power for right turn
				//If not, stop the robot.
				if(getBatteryLevel() < (getEnergyForFullRotation()/4))
					stopped = true;
				else {
					//Update power for right turn
					this.batteryLevel[0] = this.batteryLevel[0] - (getEnergyForFullRotation()/4);
					//Code base implementation of UserInput.Right results in a counterclockwise turn (unintuitive)
					//Instead we have robot's right turn move in clockwise direction (this is done by UserInput.Left)
					controller.keyDown(UserInput.LEFT);
				}
				break;
	
			case AROUND:
				//Make sure controller has enough power to turn around
				//If not, stop the robot.
				if(getBatteryLevel() < (getEnergyForFullRotation()/2))
					stopped = true;
				else {
					//Update power to turn around
					this.batteryLevel[0] = this.batteryLevel[0] - (getEnergyForFullRotation()/2);
					//Turn around, same as turning counterclockwise twice
					controller.keyDown(UserInput.RIGHT);
					controller.keyDown(UserInput.RIGHT);
				}
				break;
			}
		}
	}

	/**
	 * This method moves the robot forward a given number of cells.
	 * <p>
	 * If the robot runs out of energy somewhere on its way, it stops.
	 * <p>
	 * If the robot hits an obstacle like a wall, it remains at the position in front 
	 * of the obstacle and also hasStopped() == true as this is not supposed to happen.
	 * <p>
	 * Asserts that the robot is not crashing into a wall.
	 * If this occurs, robot and maze do not share a consistent view of wall locations.
	 * @param distance is the number of cells to move in the robot's current forward direction 
	 * @throws IllegalArgumentException if distance not positive
	 */
	@Override
	public void move(int distance) {
		
		//Make sure robot has a controller reference; cannot move forward otherwise
		assert(this.controller != null);
		
		//If robot is expected to move a negative distance throw an exception.
		//Considered an illegal argument.
		if(distance < 0)
			throw new IllegalArgumentException();
		
		//Move one step forward at a time until desired distance is achieved.
		for (int i = 0; i < distance; i++)
		{
			//Make sure robot does not continue to move if it has stopped already.
			if(!hasStopped()) {
				try {
					moveOneStep();
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Helper method for move() that moves the robot one step forward.
	 * If the robot crashes into a wall, stop the robot.
	 * Assert that the robot is not crashing into a wall (This should not occur and implies issue with driver logic).
	 * @throws Exception if position is outside of the maze.
	 */
	private void moveOneStep() throws Exception {
		Maze maze = this.controller.getMazeConfiguration();
		//If robot crashes into a wall or doesn't have enough energy to walk forward, stop the robot
		if(getBatteryLevel() < (getEnergyForStepForward()) || maze.hasWall(getCurrentPosition()[0], getCurrentPosition()[1], getCurrentDirection())) {
			this.stopped = true;
			//Assert that robot is not crashing into a wall.
			assert(!maze.hasWall(getCurrentPosition()[0], getCurrentPosition()[1], getCurrentDirection()));
		}
		else {
			//Update battery
			this.batteryLevel[0] = this.batteryLevel[0] - getEnergyForStepForward();
			//Update odometer
			odometer++;
			//Move robot forward one cell
			this.controller.keyDown(UserInput.UP);
		}
	}
	
	/**
	 * This method moves the robot forward even if there is a wall in front of it.
	 * In this sense, the robot jumps over the wall if necessary.
	 * The distance is always 1 step and the direction is always forward.
	 * If the robot runs out of energy somewhere on its way, it stops.
	 * Assert's that the robot is not jumping outside the maze (this should never happen/driver logic error)
	 * <p>
	 * If the robot tries to jump over an exterior wall,  
	 * it remains at its current location and direction,
	 * hasStopped() == true as this is not supposed to happen.
	 */
	@Override
	public void jump() {
		
		//Make sure robot has a controller reference; cannot rotate otherwise.
		assert(this.controller != null);
		//Make sure robot has not stopped or is not out of energy.
		//Robot should not receive anymore movement commands after stopping, since game should be considered over at this point.
		
		//Jump only if robot has not stopped.
		if(!hasStopped()) {
			Maze maze = this.controller.getMazeConfiguration();
			
			//Check to see if robot is about to jump over an exterior wall
			int x = this.controller.getCurrentPosition()[0];
			int y = this.controller.getCurrentPosition()[1];
			switch(getCurrentDirection()) {
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
			//Stop the robot if it is going to jump over an exterior wall or doesn't have enough energy to make the jump
			if(x >= maze.getWidth() || x < 0 || y >= maze.getHeight() || y < 0 || getBatteryLevel() < 40) {
				this.stopped = true;
				//Make sure that robot did not stop due to attempting to jump over an exterior wall (Error with driver logic if this ever occurs)
				assert(x < maze.getWidth() && x >= 0 && y < maze.getHeight() && y >= 0);
			}
			else {
				//Update battery
				this.batteryLevel[0] = this.batteryLevel[0] - 40;
				//Update odometer
				odometer++;
				//Move robot forward one cell
				this.controller.keyDown(UserInput.JUMP);
			}
		}
	}

	/**
	 * This method determines if the current position is the cell containing the exit.
	 * It is not guaranteed that the robot is facing the exit in a forward direction.
	 * @return true if robot is at the exit, false otherwise
	 */
	@Override
	public boolean isAtExit() {
		int[] currPosition = null;
		try {
			currPosition = getCurrentPosition();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (Arrays.equals(currPosition, controller.getMazeConfiguration().getExitPosition()));
	}

	/**
	 * This method determines if current position is inside a room. 
	 * @return true if robot is inside a room, false otherwise
	 */	
	@Override
	public boolean isInsideRoom() {
		int[] currPosition = null;
		try {
			currPosition = getCurrentPosition();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (this.controller.getMazeConfiguration().isInRoom(currPosition[0], currPosition[1]));
	}

	/**
	 * This method determines if the robot has stopped due to any reason (lack of energy, hitting obstacle, etc).
	 * Once a robot is has stopped, it cannot rotate or move anymore.
	 * @return true if the robot has stopped, false otherwise
	 */
	@Override
	public boolean hasStopped() {
		return (this.stopped || getBatteryLevel() <= 0);
	}

	/**
	 * This method returns the distance to an obstacle (a wall) in the given direction using the corresponding sensor.
	 * The direction is relative to the robot's current forward direction.
	 * Distance is measured in the number of cells towards that obstacle
	 * (Integer.MaxValue if one looks through the exit into eternity).
	 * <p>
	 * Asserts that distance sensing occurs only when the robot has a controller.
	 * Will return a negative distance if used after robot has stopped.
	 * This will cause an error if used for move(), and implies error in driver logic.
	 * @param direction specifies the direction of interest
	 * @return number of steps towards obstacle if obstacle is visible 
	 * in a straight line of sight, Integer.MAX_VALUE otherwise
	 * @throws UnsupportedOperationException if robot has no sensor in this direction
	 * or the sensor exists but is currently not operational
	 */
	@Override
	public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {
		
		//getCurrentPositon and getCurrentDirection assert that the controller exists.
		//Make sure robot has not stopped before scanning for distance
		if(!hasStopped()) {
			
			//Use distanceToObstacle from corresponding sensor.
			//Throw exception if robot doesn't have sensor in given direction.
			switch(direction) {
			case FORWARD:
				if(fSensor == null)
					throw new UnsupportedOperationException();
				else {
					try {
						//Stop the robot if not enough energy for sensing
						if(this.batteryLevel[0] < fSensor.getEnergyConsumptionForSensing())
							this.stopped = true;
						else
							return(fSensor.distanceToObstacle(getCurrentPosition(), getCurrentDirection(), batteryLevel));
					} 
					catch (Exception e) {
						if (e instanceof UnsupportedOperationException)
							throw new UnsupportedOperationException();
						else
							e.printStackTrace();
					}
				}
				break;
				
			case BACKWARD:
				if(bSensor == null)
					throw new UnsupportedOperationException();
				else {
					try {
						//Stop the robot if not enough energy for sensing
						if(this.batteryLevel[0] < bSensor.getEnergyConsumptionForSensing())
							this.stopped = true;
						else
							return(bSensor.distanceToObstacle(getCurrentPosition(), getCurrentDirection(), batteryLevel));
					} 
					catch (Exception e) {
						if (e instanceof UnsupportedOperationException)
							throw new UnsupportedOperationException();
						else
							e.printStackTrace();
					}
				}
				break;
				
			case LEFT:
				if(lSensor == null)
					throw new UnsupportedOperationException();
				else {
					try {
						//Stop the robot if not enough energy for sensing
						if(this.batteryLevel[0] < lSensor.getEnergyConsumptionForSensing())
							this.stopped = true;
						else
							return(lSensor.distanceToObstacle(getCurrentPosition(), getCurrentDirection(), batteryLevel));
					} 
					catch (Exception e) {
						if (e instanceof UnsupportedOperationException)
							throw new UnsupportedOperationException();
						else
							e.printStackTrace();
					}
				}
				break;
				
			case RIGHT:
				if(rSensor == null)
					throw new UnsupportedOperationException();
				else {
					try {
						//Stop the robot if not enough energy for sensing
						if(this.batteryLevel[0] < rSensor.getEnergyConsumptionForSensing())
							this.stopped = true;
						else
							return(rSensor.distanceToObstacle(getCurrentPosition(), getCurrentDirection(), batteryLevel));
					} 
					catch (Exception e) {
						if (e instanceof UnsupportedOperationException)
							throw new UnsupportedOperationException();
						else
							e.printStackTrace();
					}
				}
				break;
				
			default:
				break;
			}
		}
		//Method will only return -1 if an invalid direction is passed or sensor is used after robot has stopped (should never occur)
		return -1;
	}

	/**
	 * This method determines if a sensor can see the exit in the given direction.
	 * The direction is relative to the robot's current forward direction.
	 * Uses the distanceToObstacle method and returns true, if it returns Integer.MaxValue.
	 * @param direction is the direction of the sensor
	 * @return true if the exit of the maze is visible in a straight line of sight
	 * @throws UnsupportedOperationException if robot has no sensor in this direction
	 * or the sensor exists but is currently not operational
	 */
	@Override
	public boolean canSeeThroughTheExitIntoEternity(Direction direction) throws UnsupportedOperationException {
		return (Integer.MAX_VALUE == distanceToObstacle(direction));
	}

	/**
	 * Used for P4 UnreliableSensor.
	 * Sufficient to throw UnsupportedOperationException (for now)
	 */
	@Override
	public void startFailureAndRepairProcess(Direction direction, int meanTimeBetweenFailures, int meanTimeToRepair) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Used for P4 UnreliableSensor.
	 * Sufficient to throw UnsupportedOperationException (for now)
	 */
	@Override
	public void stopFailureAndRepairProcess(Direction direction) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

}
