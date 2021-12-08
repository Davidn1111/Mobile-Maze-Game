/**
 * 
 */
package edu.wm.cs.cs301.DavidNi.gui;


/**
 * @author David Ni
 * JavaDoc comments from comments in Robot by Peter Kempter.
 */

/**
 * UnreliableRobot
 * <p>
 * Responsibilities:
 * Move in maze.
 * Get info about surroundings.
 * Start and stop failure and repair process for sensors.
 * <p>
 * Collaborators:
 * DistanceSensor.
 * Controller.
 * Thread.
 */

public class UnreliableRobot extends ReliableRobot {

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
	 * @param direction the direction the sensor is mounted on the robot
	 * @param meanTimeBetweenFailures is the mean time in seconds, must be greater than zero
	 * @param meanTimeToRepair is the mean time in seconds, must be greater than zero
	 * @throws UnsupportedOperationException if method not supported
	 */
	@Override
	public void startFailureAndRepairProcess(Direction direction, int meanTimeBetweenFailures, int meanTimeToRepair) throws UnsupportedOperationException {
		switch(direction) {
		case FORWARD:
			this.fSensor.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		case BACKWARD:
			this.bSensor.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		case LEFT:
			this.lSensor.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		case RIGHT:
			this.rSensor.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		}
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
	 * @param direction the direction the sensor is mounted on the robot
	 * @throws UnsupportedOperationException if method not supported
	 */
	public void stopFailureAndRepairProcess(Direction direction) throws UnsupportedOperationException {
		switch(direction) {
		case FORWARD:
			this.fSensor.stopFailureAndRepairProcess();
			break;
		case BACKWARD:
			this.bSensor.stopFailureAndRepairProcess();
			break;
		case LEFT:
			this.lSensor.stopFailureAndRepairProcess();
			break;
		case RIGHT:
			this.rSensor.stopFailureAndRepairProcess();
			break;
		}
	}
}
