package com.example.amazebydavidni;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PlayManuallyActivity extends AppCompatActivity {
    // TODO get info about maze from GeneratingActivity
    // private Maze maze;

    // Length of the path taken by the player
    private int pathLength = 0;
    // Toggle button to show solution
    private ToggleButton solutionButton;
    // Toggle button to show map of maze
    private ToggleButton mazeButton;
    // Toggle button to show currently visible walls of the maze
    private ToggleButton wallsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_manually);

        // Toggle button for showing solution
        solutionButton=findViewById(R.id.manualSolutionButton);
        solutionButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /**
             * This method shows the solution of the maze if the "Show Solution" Button is toggled on.
             * If the button is togged off, the solution of the maze is not shown.
             * @param compoundButton Button view of the toggle button.
             * @param checked True if the button is toggled on, False otherwise.
             */
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                // Show solution if button is toggled on
                if(solutionButton.isChecked()){
                    // Toast to show solution button was toggled on, for debugging
                    Toast.makeText(getApplicationContext(),"Toggled Solution Button: ON",Toast.LENGTH_SHORT).show();
                    // Log message to show solution button was toggled on, for debugging
                    Log.v("PlayManuallyActivity", "Toggled Solution Button: ON");
                }
                // Do not show solution if button is toggled off
                else{
                    // Toast to show solution button was toggled off, for debugging
                    Toast.makeText(getApplicationContext(),"Toggled Solution Button: OFF",Toast.LENGTH_SHORT).show();
                    // Log message to show solution button was toggled off, for debugging
                    Log.v("PlayManuallyActivity", "Toggled Solution Button: OFF");
                }
            }
        });

        // Toggle button for showing map of maze
        mazeButton=findViewById(R.id.mazeButton);
        mazeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /**
             * This method shows the map of the maze if the "Show Maze" Button is toggled on.
             * If the button is togged off, the map of the maze is not shown.
             * @param compoundButton Button view of the toggle button.
             * @param checked True if the button is toggled on, False otherwise.
             */
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                // Show map of maze if button is toggled on
                if(mazeButton.isChecked()){
                    // Toast to show maze button was toggled on, for debugging
                    Toast.makeText(getApplicationContext(),"Toggled Show Maze Button: ON",Toast.LENGTH_SHORT).show();
                    // Log message to show maze button was toggled on, for debugging
                    Log.v("PlayManuallyActivity", "Toggled Show Maze Button: ON");
                }
                // Do not show map of maze if button is toggled off
                else{
                    // Toast to show maze button was toggled off, for debugging
                    Toast.makeText(getApplicationContext(),"Toggled Show Maze Button: OFF",Toast.LENGTH_SHORT).show();
                    // Log message to show maze button was toggled off, for debugging
                    Log.v("PlayManuallyActivity", "Toggled Show Maze Button: OFF");
                }
            }
        });

        // Toggle button for showing visible walls of the maze
        wallsButton=findViewById(R.id.wallButton);
        wallsButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /**
             * This method shows the currently visible walls of the maze if the "Show Walls" Button is toggled on.
             * If the button is togged off, the visible walls of the maze are not shown.
             * @param compoundButton Button view of the toggle button.
             * @param checked True if the button is toggled on, False otherwise.
             */
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                // Show map of maze if button is toggled on
                if(wallsButton.isChecked()){
                    // Toast to show walls button was toggled on, for debugging
                    Toast.makeText(getApplicationContext(),"Toggled Show Walls Button: ON",Toast.LENGTH_SHORT).show();
                    // Log message to show walls button was toggled on, for debugging
                    Log.v("PlayManuallyActivity", "Toggled Show Walls Button: ON");
                }
                // Do not show map of maze if button is toggled off
                else{
                    // Toast to show walls button was toggled off, for debugging
                    Toast.makeText(getApplicationContext(),"Toggled Show Walls Button: OFF",Toast.LENGTH_SHORT).show();
                    // Log message to show walls button was toggled off, for debugging
                    Log.v("PlayManuallyActivity", "Toggled Show Walls Button: OFF");
                }
            }
        });
    }
}