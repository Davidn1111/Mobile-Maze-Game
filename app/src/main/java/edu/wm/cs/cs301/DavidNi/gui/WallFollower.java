/**
 * 
 */
package edu.wm.cs.cs301.DavidNi.gui;

import edu.wm.cs.cs301.DavidNi.gui.Robot.Direction;
import edu.wm.cs.cs301.DavidNi.gui.Robot.Turn;

/**
 * @author David Ni
 * JavaDoc comments modified from comments in RobotDriver by Peter Kempter.
 */

/**
 * WallFollower
 * <p>
 * Responsibilities:
 * Control robot.
 * Run algorithm to get to exit.
 * <p>
 * Collaborators:
 * Robot.
 * Maze (Floorplan).
 */

public class WallFollower extends Wizard {
	
	/**
	 * Default constructor for WallFollower.
	 */
	public WallFollower() {	
	}

	/**
	 * This method drives the robot to the maze exit using the wall follower algorithm following the left hand rule.
	 * Asserts that a robot and maze reference have been given beforehand.
	 * @return true if driver successfully reaches the exit, false otherwise
	 * @throws Exception thrown if robot stopped due to some problem, e.g. lack of energy
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		//Assert that the wizard has a maze and robot reference before driving to exit.
		assert(this.robot != null);
		assert(this.maze != null);
		
		//Handle Corner Cases
		if(robot.isInsideRoom()) {
			
			// Check if robot spawns in middle of a room.
			// Flag to check if robot has a wall next to it (Is in the middle of a room if this is false)
			boolean hasWall = false;
			for (Direction dir : Direction.values()) {
				try {
					hasWall = (robot.distanceToObstacle(dir) == 0);
					if (hasWall)
						break;
				}
				catch (Exception e) {
					hasWall = (handleFailedSensor(dir) == 0);
					if (hasWall)
						break;
				}
			}

			
			// If robot is in the middle of a room move it up until it is next to a wall
			if(!hasWall) {
				int distance = -1;
				try {
					distance = robot.distanceToObstacle(Direction.FORWARD);
				}
				catch (Exception e) {
					distance = handleFailedSensor(Direction.FORWARD);
				}
				// If robot is looking at the exit move it to the exit cell
				if(distance == Integer.MAX_VALUE) {
					while(!robot.isAtExit()) {
						robot.move(1);
						if(robot.hasStopped())
							throw new Exception("Robot has stopped");
					}
				}
				// Else move robot up until it touches a wall
				else {
					robot.move(distance);
				}
			}
			
			// If the robot has no left wall to follow in a room, rotate it so that it does.
			// This covers room corners.
			while(true) {
				int leftReading;
				try {
					leftReading = robot.distanceToObstacle(Direction.LEFT);
				}
				catch (Exception e) {
					leftReading = handleFailedSensor(Direction.LEFT);
				}
				if(leftReading == 0)
					break;
				robot.rotate(Turn.RIGHT);
			}
		}
		// Check that robot did not stop after escaping corner cases.
		if(robot.hasStopped())
			throw new Exception("Robot has stopped");
		
		// Drive one step closer to the exit.
		while(!robot.isAtExit() && !this.robot.hasStopped()) {
			drive1Step2Exit();
		}
		// If at exit cell, rotate to exit and drive out.
		if(robot.isAtExit())
		{
			return(stepOutExit());
		}
		else
			return false;
	}

	/**
	 * This method drives the robot one step to the maze exit using the wall follower algorithm (left hand rule).
	 * <p>
	 * For each step of the wall follower algorithm, the robot will follow the wall on its left-hand side until it reaches an obstacle or no longer has a left wall.
	 * If the robot cannot move forward, the robot rotates 90 degrees clockwise.
	 * If the robot has no left wall, the robot will rotate 90 degrees CCW, and steps forward.
	 * <p>
	 * If the forward or left side sensor fails, two strategies are implemented:
	 * <p>
	 * 1) Substitute a failed sensor at runtime with a working sensor.
	 * <p>
	 * 2) If no working sensors are available, wait for the failed sensor to be repaired.
	 * @returns true if robot was successful in following one step of left hand rule algorithm, throws exception otherwise.
	 * @throws Exception thrown if robot stopped due to some problem, e.g. lack of energy
	 */
	@Override
	public boolean drive1Step2Exit() throws Exception {
		int leftReading;
		int forwardReading;
		
		try {
			leftReading = robot.distanceToObstacle(Direction.LEFT);
		}
		catch (Exception e) {
			leftReading = handleFailedSensor(Direction.LEFT);
		}
		
		if(leftReading != 0) {
			robot.rotate(Turn.LEFT);
			robot.move(1);
			if(robot.hasStopped())
				throw new Exception("Robot has stopped");
			return true;
		}
		
		try {
			forwardReading = robot.distanceToObstacle(Direction.FORWARD);
		}
		catch (Exception e) {
			forwardReading = handleFailedSensor(Direction.FORWARD);
		}
		
		if (forwardReading != 0) {
			robot.move(1);
			if(robot.hasStopped())
				throw new Exception("Robot has stopped");
			return true;
		}
		
		else {
			robot.rotate(Turn.RIGHT);
			if(robot.hasStopped())
				throw new Exception("Robot has stopped");
			return true;
		}
	}

}
