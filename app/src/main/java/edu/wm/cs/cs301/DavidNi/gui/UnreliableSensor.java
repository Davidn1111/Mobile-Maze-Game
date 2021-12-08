/**
 * 
 */
package edu.wm.cs.cs301.DavidNi.gui;

import edu.wm.cs.cs301.DavidNi.generation.CardinalDirection;
import edu.wm.cs.cs301.DavidNi.generation.Floorplan;

/**
 * @author David Ni
 * JavaDoc comments from comments in DistanceSensor by Peter Kempter.
 */

/**
 * UnreliableSensor
 * <p>
 * Responsibilities:
 * Find distance to wall.
 * Fails and repairs itself.
 * <p>
 * Collaborators:
 * Maze.
 * Thread.
 */

public class UnreliableSensor extends ReliableSensor {
	//Sensor's operational status, defaulted to true.
	private boolean opStatus = true;
	//Sensor's failureAndRepairProcess.
	private failureAndRepairProcess sensorProcess;
	
	/**
	 * Private thread that represents the sensor's failure and repair process.
	 * @author David Ni
	 */
	private class failureAndRepairProcess extends Thread implements Runnable {	
		// Duration for being in a failed state. Considered constant.
		// Default to 2 seconds.
		int meanTimeToRepair = 2000;
		// Duration for being in an operational state. Considered constant.
		// Default to 4 seconds.
		int meanTimeBetweenFailures = 4000;
	
		/**
		 * Constructor for failureAndRepairProcess.
		 * @param meanTimeToRepair duration for being in a failed state. Is considered constant and fixed to 2 seconds.
		 * @param meanTimeBetweenFailures duration for being in an operational state. Is considered constant and fixed to 4 seconds.
		 */
		public failureAndRepairProcess(int meanTimeBetweenFailures,int meanTimeToRepair) {
			this.meanTimeToRepair = meanTimeToRepair;
			this.meanTimeBetweenFailures = meanTimeBetweenFailures;
		}

		/**
		 * This method runs the failure and repair thread.
		 * For as long as the thread is not terminated:
		 * The thread will wait the meanTimeBetweenFailures, before declaring that the sensor has failed.
		 * Then, the thread will wait the meanTimeToRepair, before declaring that the sensor has been fixed/is operational.
		 */
		public void run() {
			// Assert that meanTimeBetweenFailures and meanTimeToRepair are not negative.
			assert(meanTimeToRepair >= 0 && meanTimeBetweenFailures >= 0);
			
			// Continue to loop between breaking and fixing the sensor until thread is interrupted.
			while(true) {
				// Wait the given meanTimeBetweenFailures, before declaring that the sensor has failed.
				try {
					failureAndRepairProcess.sleep(meanTimeBetweenFailures);
				} catch (InterruptedException e) {
					opStatus = true;
					return;
				}
				// Sensor is now broken/fails
				opStatus = false;
				
				// Wait the given meanTimeToRepair, before declaring that the sensor has been fixed/is operational.
				try {
					failureAndRepairProcess.sleep(meanTimeToRepair);
				} catch (InterruptedException e) {
					opStatus = true;
					return;
				}
				// Sensor is now fixed/operational
				opStatus = true;
			}
		}
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
	 * the corresponding set methods with a parameterized constructor. 
	 * Confirmed using assert.
	 * <p>
	 * This method will only work when sensor is operational. 
	 * If it is not operational, the method will throw an UnsupportedOperationException.
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
		if (opStatus) {
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
		
		//If scanning called for an non-operational sensor, return -1 (impossible distance value).
		else
			throw new UnsupportedOperationException();
	}
	
	/**
	 * Method starts a concurrent, independent failure and repair
	 * process that makes the sensor fail and repair itself.
	 * This creates alternating time periods of up time and down time.
	 * Up time: The duration of a time period when the sensor is in 
	 * operational is characterized by a distribution
	 * whose mean value is given by parameter meanTimeBetweenFailures.
	 * Down time: The duration of a time period when the sensor is in repair
	 * and not operational is characterized by a distribution
	 * whose mean value is given by parameter meanTimeToRepair.
	 * 
	 * This an optional operation. If not implemented, the method
	 * throws an UnsupportedOperationException.
	 * 
	 * @param meanTimeBetweenFailures is the mean time in seconds, must be greater than zero
	 * @param meanTimeToRepair is the mean time in seconds, must be greater than zero
	 * @throws UnsupportedOperationException if method not supported
	 */
	@Override
	public void startFailureAndRepairProcess(int meanTimeBetweenFailures, int meanTimeToRepair) throws UnsupportedOperationException {
		sensorProcess = new failureAndRepairProcess(meanTimeBetweenFailures,meanTimeToRepair);
		sensorProcess.start();
	}
	
	/**
	 * This method stops a failure and repair process and
	 * leaves the sensor in an operational state.
	 * 
	 * It is complementary to starting a 
	 * failure and repair process. 
	 * 
	 * Intended use: If called after starting a process, this method
	 * will stop the process as soon as the sensor is operational.
	 * 
	 * If called with no running failure and repair process, 
	 * the method will return an UnsupportedOperationException.
	 * 
	 * This an optional operation. If not implemented, the method
	 * throws an UnsupportedOperationException.
	 * 
	 * @throws UnsupportedOperationException if method not supported
	 */
	@Override
	public void stopFailureAndRepairProcess() throws UnsupportedOperationException {
		// Terminate the sensor's process
		sensorProcess.interrupt();
		// Set the sensor to operational
		opStatus = true;
	}
	
	/**
	 * This method gets the sensor's operational status.
	 * Intended for testing and debugging purposes only.
	 * @return true if sensor is operational, false otherwise.
	 */
	public boolean getOpStatus() {
		return this.opStatus;
	}
	
	/**
	 * This method sets the sensor's operational status to a given status.
	 * Intended for testing and debugging purposes only.
	 * @param status new operational status of the sensor.
	 */
	public void setOpStatus(boolean status) {
		this.opStatus = status;
	}
}
