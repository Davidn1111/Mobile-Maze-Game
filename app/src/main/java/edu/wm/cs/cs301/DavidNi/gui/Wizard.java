/**
 * 
 */
package edu.wm.cs.cs301.DavidNi.gui;


import edu.wm.cs.cs301.DavidNi.generation.CardinalDirection;
import edu.wm.cs.cs301.DavidNi.generation.Maze;
import edu.wm.cs.cs301.DavidNi.gui.Robot.Direction;
import edu.wm.cs.cs301.DavidNi.gui.Robot.Turn;

/**
 * @author David Ni
 * JavaDoc comments modified from comments in RobotDriver by Peter Kempter.
 */

/**
 * Wizard
 * <p>
 * Responsibilities:
 * Control robot.
 * Run algorithm to get to exit.
 * <p>
 * Collaborators:
 * Robot.
 * Maze.
 */

public class Wizard implements RobotDriver {
	//Robot to control.
	protected Robot robot = null;
	//Maze reference.
	protected Maze maze = null;
	
	/**
	 * Default constructor for Wizard.
	 */
	public Wizard() {	
	}
	
	/**
	 * Assigns a robot platform to the driver. 
	 * The driver uses a robot to perform, this method provides it with this necessary information.
	 * @param r robot to operate
	 */
	@Override
	public void setRobot(Robot r) {
		this.robot = r;
	}

	/**
	 * Provides the robot driver with the maze information.
	 * Only some drivers such as the wizard rely on this information to find the exit.
	 * @param maze represents the maze, must be non-null and a fully functional maze object.
	 */
	@Override
	public void setMaze(Maze maze) {
		this.maze = maze;
	}

	/**
	 * This method drives the robot to the maze exit using the wizard algorithm.
	 * The wizard algorithm relies on the Maze object to get the neighboring cell closest to the exit.
	 * It then move the robot accordingly until it reaches the exit or stops (due to crashing, lack of energy, etc).
	 * Asserts that a robot and maze reference have been given beforehand.
	 * @return true if driver successfully reaches the exit, false otherwise
	 * @throws Exception thrown if robot stopped due to some problem, e.g. lack of energy
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		//Assert that the wizard has a maze and robot reference before driving to exit.
		assert(this.robot != null);
		assert(this.maze != null);
		
		//Drive one step closer to the exit.
		while(!robot.isAtExit() && !this.robot.hasStopped()) {
			drive1Step2Exit();
		}
		//If at exit cell, rotate to exit and drive out.
		if(robot.isAtExit())
		{
			return(stepOutExit());
		}
		
		else
			return false;
	}

	/**
	 * This method drives the robot one step to the maze exit, using the wizard algorithm.
	 * For each step, the wizard algorithm moves the robot to the neighbor that is closest to the exit.
	 * The wizard algorithm relies on the Maze object to find the neighboring cell closest to the exit.
	 * However, if the wizard can save energy by jumping to a neighboring cell (behind a wall) it will do that instead.
	 * <p>
	 * Asserts that wizard only drives one step towards the exit if it has a robot and maze reference.
	 * Asserts that each step gets the robot one step closer to the exit (this must always be true by the nature of the wizard algorithm).
	 * <p>
	 * If wizard crashes robot into a wall, robot's move function will assert an error
	 * @return true if it moved the robot to an adjacent cell, false otherwise
	 * @throws Exception thrown if robot stopped due to some problem, e.g. lack of energy
	 */
	@Override
	public boolean drive1Step2Exit() throws Exception {
		//Assert that wizard was previously given robot and maze reference to use (cannot work otherwise)
		assert(robot != null);
		assert(maze != null);

		//x and y coordinates of the robot's current position.
		int x = robot.getCurrentPosition()[0];
		int y = robot.getCurrentPosition()[1];
		//x and y coordinates of the robot's closest neighbor (the desired cell for robot to move to)
		int desiredX = maze.getNeighborCloserToExit(x, y)[0];
		int desiredY = maze.getNeighborCloserToExit(x, y)[1];
		//Direction robot should be facing to get to the desired cell
		CardinalDirection desiredDirection = null;

		//Find the desired direction for robot
		//Desired position is to the west of robot
		if(desiredX - x == -1)
			desiredDirection = CardinalDirection.West;
			//Desired position is to the east of robot
		else if(desiredX - x == 1)
			desiredDirection = CardinalDirection.East;
			//desired position is to the north of robot
		else if(desiredY - y == -1)
			desiredDirection = CardinalDirection.North;
			//desired position is to the south of robot
		else if(desiredY - y == 1)
			desiredDirection = CardinalDirection.South;

		//Move the robot to the desired cell
		//If robot is facing the desired cell
		if(desiredDirection == robot.getCurrentDirection())
			//move forward
			robot.move(1);
			//If robot is facing the opposite direction of the desired cell
		else if(desiredDirection == robot.getCurrentDirection().oppositeDirection()) {
			//Turn robot around and move one
			robot.rotate(Turn.AROUND);
			robot.move(1);
		}
		//If desired cell is to the right (90 degrees clockwise from front) of the robot
		else if(desiredDirection == robot.getCurrentDirection().rotateClockwise()) {
			robot.rotate(Turn.RIGHT);
			robot.move(1);
		}
		//If desired cell is to the left (90 degrees clockwise from front) of the robot
		else if(desiredDirection == robot.getCurrentDirection().rotateClockwise().oppositeDirection()) {
			robot.rotate(Turn.LEFT);
			robot.move(1);
		}

		//Assert robot is closer to the exit after moving.
		//Since the wizard is a cheater, every move it makes should always get closer to the exit
		if(!robot.hasStopped()) {
			//Robot's distance from exit before moving
			int previousDistance = maze.getDistanceToExit(x, y);
			//Robot's distance from exit after moving
			int currentDistance = maze.getDistanceToExit(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1]);
			assert(currentDistance < previousDistance);
		}

		//Throw exception if robot has stopped (due to lack of energy, crashing, etc)
		if(robot.hasStopped())
			throw new Exception("Robot has stopped");

		//Return if wizard successfully moved robot to desired cell
		return (robot.getCurrentPosition()[0] == desiredX && robot.getCurrentPosition()[1] == desiredY);
	}

	/**
	 * This method returns the total energy consumption of the robot's journey. 
	 * This is used as a measure of efficiency for a robot driver.
	 * Assumes robot battery starts at 3500 and is not set to a new value at any point.
	 * @return the total energy consumption of the journey
	 */
	@Override
	public float getEnergyConsumption() {
		return 3500 - this.robot.getBatteryLevel();
	}

	/**
	 * This method returns the total length of the robot's journey (number of cells traversed). 
	 * Being at the initial position counts as length 0. 
	 * Assumes robot's odometer is not reset in the middle of wizard algorithm.
	 * This is used as a measure of efficiency for a robot driver.
	 * @return the total length of the journey in number of cells traversed
	 */
	@Override
	public int getPathLength() {
		return this.robot.getOdometerReading();
	}

	/**
	 * This helper method handles sensor scanning if the sensor in the given direction doesn't work.
	 * Initially, the method will try to find a working replacement sensor by rotating to the left 
	 * and checking if the "new" sensor (in the given direction) works.
	 * If such a sensor is found, it will be used as a replacement 
	 * before rotating back to its original position.
	 * <p>
	 * If the robot completes a full rotation and does not find a replacement sensor, 
	 * the robot will wait until the desired sensor becomes operational.
	 * @param dir direction of the failed sensor
	 * @return the distance to an obstacle in the original failed sensor's direction.
	 * @throws Exception thrown if robot stopped due to some problem, e.g. lack of energy 
	 */
	protected int handleFailedSensor(Direction dir) throws Exception {
		// Counter for how many rotations made by the robot
		int counter = 0;
		// Distance scanned by sensor (default to negative value)
		int distance = -1;
		// Current sensor direction facing the original cardinal direction.
		Direction currDir = dir;
		// Rotate the robot left and see if replacement sensor has been found.
		for (int i = 0; i < 3; i++) {
			counter++;
			robot.rotate(Turn.LEFT);
			currDir = dirLeftTurn(currDir);
			try {
				distance = robot.distanceToObstacle(currDir);
				break;
			}
			catch(Exception e) {
			}
		}
		// If replacement sensor was found, rotate the robot back to original position.
		if (distance >= 0) {
			for(int j = 0; j < counter;j++) {
				robot.rotate(Turn.RIGHT);
			}
			if(robot.hasStopped())
				throw new Exception("Robot has stopped");
			// Return the distance reading from replacement sensor.
			return distance;
		}
		// No replacement sensor was found
		else {
			// Return robot to original position.
			robot.rotate(Turn.LEFT);
			// Wait for the original sensor to become operational again.
			while(true) {
				try {
					distance = robot.distanceToObstacle(dir);
					break;
				}
				catch (Exception e) {
				}
			}
			if(robot.hasStopped())
				throw new Exception("Robot has stopped");
			// Return the distance reading from replacement sensor.
			return distance;
		}
	}

	/**
	 * This method returns the corresponding direction to the given direction after the robot rotates left.
	 * @param dir original direction
	 * @return Corresponding direction after the robot rotates left
	 */
	protected Direction dirLeftTurn(Direction dir) {
		switch(dir) {
		case FORWARD:
			return Direction.RIGHT;
		case BACKWARD:
			return Direction.LEFT;
		case LEFT:
			return Direction.FORWARD;
		case RIGHT:
			return Direction.BACKWARD;
		default:
			return null;
		}
	}

	/**
	 * Given that the robot is at the exit cell, this helper method rotates and moves the robot accordingly to step out of the exit.
	 * Intended to be used by drive2Exit() if robot successfully reaches the exit cell. 
	 * @return true if robot could step out of the maze, false otherwise.
	 * @throws Exception thrown if robot stopped due to some problem, e.g. lack of energy
	 */
	@Override
	public boolean stepOutExit() throws Exception {
		// Direction of exit relative to robot's forward position
		Direction exitDirection = null;
		// Assume robot is implemented with sensors in all four directions.
		for (Direction dir : Direction.values()) {
			// Stop the sensor's failure and repair process
			try {
			this.robot.stopFailureAndRepairProcess(dir);
			}
			catch (Exception e) {
			}
			// Check if sensor sees the exit.
			if (this.robot.canSeeThroughTheExitIntoEternity(dir)) {
				exitDirection = dir;
				if(robot.hasStopped())
					throw new Exception("Robot has stopped");
				break;
			}
			// Throw an exception if robot runs out of power after a scan
			if(robot.hasStopped())
				throw new Exception("Robot has stopped");
		}

		// Flag to check if robot was able to make final step outside of maze
		boolean finalStep = false;
		
		// Rotate robot to face exit and take one step to leave the maze
		switch(exitDirection) {
		case FORWARD:
			finalStep = (robot.getBatteryLevel() >= robot.getEnergyForStepForward());
			robot.move(1);
			break;
		case BACKWARD:
			robot.rotate(Turn.AROUND);
			if(robot.hasStopped())
				throw new Exception("Robot has stopped");
			finalStep = (robot.getBatteryLevel() >= robot.getEnergyForStepForward());
			robot.move(1);
			break;
		case LEFT:
			robot.rotate(Turn.LEFT);
			if(robot.hasStopped())
				throw new Exception("Robot has stopped");
			finalStep = (robot.getBatteryLevel() >= robot.getEnergyForStepForward());
			robot.move(1);
			break;
		case RIGHT:
			robot.rotate(Turn.RIGHT);
			if(robot.hasStopped())
				throw new Exception("Robot has stopped");
			finalStep = (robot.getBatteryLevel() >= robot.getEnergyForStepForward());
			robot.move(1);
			break;
		}
		return finalStep;
	}

}
