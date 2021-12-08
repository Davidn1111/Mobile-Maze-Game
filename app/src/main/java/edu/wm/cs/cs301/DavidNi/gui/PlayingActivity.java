package edu.wm.cs.cs301.DavidNi.gui;

public interface PlayingActivity {
    /**
     * Helper method called after beating the maze game by statePlaying.
     * Communicates journey information to WinningActivity before starting it.
     */
    void goToWinning();
}
