/**
 * 
 */
package edu.wm.cs.cs301.DavidNi.generation;

/**
 * @author David
 * 
 * Implementation for stub order based on notes from office hours with Peter Kempter
 * Comments for getSkillLevel, getBuilder, isPerfect, getSeed, deliver, and updateProgress from Peter Kempter's Order class
 */
public class StubOrder implements Order {
	
	private int skillLevel;
	private Builder builder;
	private boolean perfect;
	private int seed;
	private Maze reference;
	private int progress;
	
	/**
	 * Constructor for stub order object
	 * @param skillLevel is skill level/size of the maze
	 * @param builder is type of MazeBuilder used to construct the maze
	 * @param perfect is true if maze is perfect
	 * @param seed is the seed used for random number generation
	 * reference is the reference Maze that will be returned to be used for testing
	 */
	public StubOrder(int skillLevel, Builder builder, boolean perfect,int seed) {
		this.skillLevel = skillLevel;
		this.builder = builder;
		this.perfect = perfect;
		this.seed = seed;
		this.reference = null;
	}
	
	/**
	 * Gives the required skill level, range of values 0,1,2,...,15.
	 * @return the skill level or size of maze to be generated in response to an order
	 */
	@Override
	public int getSkillLevel() {
		return this.skillLevel;
	}

	/** 
	 * Gives the requested builder algorithm, possible values 
	 * are listed in the Builder enum type.
	 * @return the builder algorithm that is expected to be used for building the maze
	 */
	@Override
	public Builder getBuilder() {
		return this.builder;
	}

	/**
	 * Describes if the ordered maze should be perfect, i.e. there are 
	 * no loops and no isolated areas, which also implies that 
	 * there are no rooms as rooms can imply loops
	 * @return true if a perfect maze is wanted, false otherwise
	 */
	@Override
	public boolean isPerfect() {
		return this.perfect;
	}

	/**
	 * Gives the seed that is used for the random number generator
	 * used during the maze generation.
	 * @return the current setting for the seed value of the random number generator
	 */
	@Override
	public int getSeed() {
		return this.seed;
	}

	/**
	 * Delivers the produced maze. 
	 * This method is called by the factory to provide the 
	 * resulting maze as a MazeConfiguration.
	 * It is a call back function that is called some time
	 * later in response to a client's call of the order method.
	 * @param mazeConfig is the maze that is delivered in response to an order
	 */
	@Override
	public void deliver(Maze mazeConfig) {
		this.reference = mazeConfig;
	}

	/**
	 * Provides an update on the progress being made on 
	 * the maze production. This method is called occasionally
	 * during production, there is no guarantee on particular values.
	 * Percentage will be delivered in monotonously increasing order,
	 * the last call is with a value of 100 after delivery of product.
	 * @param percentage of job completion
	 */
	@Override
	public void updateProgress(int percentage) {
		this.progress = percentage;
	}

	/**
	 * Method to get progress of maze generation.
	 * Used in GeneratingActivity to update progress bar.
	 */
	public int getProgress() {
		return this.progress;
	}
	
	/**
	 * Method gets maze reference made by factory
	 * @return reference Maze that is delivered by the factory
	 */
	public Maze getMazeReference() {
		return reference;
	}

}
