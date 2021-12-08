package edu.wm.cs.cs301.DavidNi.gui;

import edu.wm.cs.cs301.DavidNi.generation.Maze;

public class Singleton {
    // Static reference of instance for this Singleton type
    private static Singleton instance;
    // Maze for maze game
    private static Maze maze = null;

    /**
     * Static method to create instance of Singleton class
     */
    public static Singleton getInstance() {
        if(instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    /**
     * Method to set maze configuration for use in all activity
     * @param mazeConfig shared maze configuration
     */
    public void setMaze(Maze mazeConfig){
        Singleton.maze = mazeConfig;
    }

    /**
     * Method to return shared maze configuration
     * @return Maze generated in generatingActivity
     */
    public Maze getMaze(){
        return Singleton.maze;
    }

    /**
     * Method to set static maze reference to null and then remove from memory using garbage collection.
     * Intended to be called once a playing activity obtains a reference to maze generated in GeneratingActivity.
     */
    public void releaseMaze(){
        Singleton.maze = null;
        System.gc();
    }
}
