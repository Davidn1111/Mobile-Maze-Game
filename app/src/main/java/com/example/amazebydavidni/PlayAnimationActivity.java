package com.example.amazebydavidni;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PlayAnimationActivity extends AppCompatActivity {
    // TODO get info about maze from GeneratingActivity
    // private Maze maze;
    // Driver for traversing maze
    private String driver;
    // Robot sensor configuration
    private String robotConfig;

    // Starting energy of the robot
    private int totalEnergy;
    // Current energy consumed by the robot
    private int energyConsumed;

    // Toggle button for showing map (whole maze with solution and visible walls) during animation/automatic play
    private ToggleButton mapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_animation);

        // TODO use intent listener to get maze from GeneratingActivity

        // Toggle button for showing map (whole maze with solution and visible walls) during animation/automatic play
        mapButton = findViewById(R.id.animMapButton);
        mapButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /**
             * This method shows the map whole maze with solution and visible walls) when "Show Maze" button toggled on.
             * If the button is togged off, the map is not shown.
             *
             * @param compoundButton Button view of the toggle button.
             * @param checked        True if the button is toggled on, False otherwise.
             */
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                // Show map of maze if button is toggled on
                if (mapButton.isChecked()) {
                    // Toast to show button was toggled on, for debugging
                    Toast.makeText(getApplicationContext(), "Showing Map of Maze", Toast.LENGTH_SHORT).show();
                    // Log message to show button was toggled on, for debugging
                    Log.v("PlayAnimationActivity", "Toggled Show Map Button: ON");
                }
                // Do not show map of maze if button is toggled off
                else {
                    // Toast to show button was toggled off, for debugging
                    Toast.makeText(getApplicationContext(), "No Longer Showing Map of Maze", Toast.LENGTH_SHORT).show();
                    // Log message to show button was toggled off, for debugging
                    Log.v("PlayAnimationActivity", "Toggled Show Map Button: OFF");
                }
            }
        });

        // ImageButton for zooming into the map
        ImageButton zoomInButton = findViewById(R.id.animZoomInButton);
        // Listener for leftButton
        zoomInButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This method listens if the "Zoom In" button was pressed.
             * If the "Zoom In" button was pressed, decrease the size of the map (zoom into it).
             *
             * @param view The view that was clicked
             */
            @Override
            public void onClick(View view) {
                // Toast to show Zoom In button was pressed, for debugging
                Toast.makeText(getApplicationContext(), "Zooming In", Toast.LENGTH_SHORT).show();
                // Log message to show Zoom In button was pressed, for debugging
                Log.v("PlayAnimationActivity", "Zooming in On Map");
            }
        });

        // ImageButton for zooming into the map
        ImageButton zoomOutButton = findViewById(R.id.animZoomOutButton);
        // Listener for leftButton
        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This method listens if the "Zoom Out" button was pressed.
             * If the "Zoom Out" button was pressed, increase the size of the map (zoom out on it).
             *
             * @param view The view that was clicked
             */
            @Override
            public void onClick(View view) {
                // Toast to show Zoom Out button was pressed, for debugging
                Toast.makeText(getApplicationContext(), "Zooming Out", Toast.LENGTH_SHORT).show();
                // Log message to show Zoom Out button was pressed, for debugging
                Log.v("PlayAnimationActivity", "Zooming out on Map");
            }
        });

        // TODO Figure out how to handle speed
        // TODO Get correct speed value from SeekBar (R.id.speedBar)

        // TODO Update progress bar to reflect robot's energy

        // TODO Write code such that sensor images change color to reflect status

        // ImageButton for going to the win activity. Placeholder for maze game.
        Button goToWin = findViewById(R.id.Go2Winning);
        // Listener for goToWin button
        goToWin.setOnClickListener(new View.OnClickListener() {
            /**
             * Method listens if the "Go2Winning" button (placeholder for the maze game) is pressed.
             * Communicates journey information to WinningActivity before starting it.
             * Journey information uses hardcoded:
             * path length = 100,
             * shortest path length = 200,
             * energy consumption = 300
             */
            @Override
            public void onClick(View view) {
                // Toast that you won the game, for debugging purposes
                Toast.makeText(getApplicationContext(),"Going to WinningActivity",Toast.LENGTH_SHORT).show();
                // Log message to show you won the game, for debugging purposes
                Log.v("PlayAnimationActivity", "Going to WinningActivity");
                goToWinning(100,200,300);
            }
        });

        // ImageButton for going to the win activity. Placeholder for maze game.
        Button goToLoss = findViewById(R.id.Go2Losing);
        // Listener for goToWin button
        goToLoss.setOnClickListener(new View.OnClickListener() {
            /**
             * Method listens if the "Go2Losing" button (placeholder for the maze game) is pressed.
             * Communicates journey information to LosingActivity before starting it.
             * Journey information uses hardcoded:
             * path length = 400,
             * shortest path length = 500,
             * energy consumption = 600
             */
            @Override
            public void onClick(View view) {
                // Toast that you won the game, for debugging purposes
                Toast.makeText(getApplicationContext(),"Going to LosingActivity",Toast.LENGTH_SHORT).show();
                // Log message to show you won the game, for debugging purposes
                Log.v("PlayAnimationActivity", "Going to LosingActivity");
                goToLosing(400,500,600);
            }
        });
    }

    /**
     * Helper method called robot loses the maze game.
     * Communicates robot's journey information (path length,shortest possible path, and energy consumption) to LosingActivity before starting it.
     * @param pathLength Path length travelled by robot during its journey.
     * @param shortestPath Shortest possible path (without jumping) to beat the maze.
     * @param energyConsumed Energy consumed by the robot during its journey.
     */
    private void goToLosing(int pathLength, int shortestPath,int energyConsumed) {
        // Send journey information to WinningActivity
        Intent intent = new Intent(this, LosingActivity.class);
        intent.putExtra("PathLength", pathLength);
        intent.putExtra("ShortestPath", shortestPath);
        intent.putExtra("energyConsumption", energyConsumed);
        startActivity(intent);
    }

    /**
     * Helper method called after robots beats the maze game.
     * Communicates robot's journey information (path length,shortest possible path, and energy consumption) to WinningActivity before starting it.
     * @param pathLength Path length travelled by robot during its journey.
     * @param shortestPath Shortest possible path (without jumping) to beat the maze.
     * @param energyConsumed Energy consumed by the robot during its journey.
     */
    private void goToWinning(int pathLength, int shortestPath,int energyConsumed) {
        // Send journey information to WinningActivity
        Intent intent = new Intent(this, WinningActivity.class);
        intent.putExtra("PathLength", pathLength);
        intent.putExtra("ShortestPath", shortestPath);
        intent.putExtra("energyConsumption", energyConsumed);
        startActivity(intent);
    }
}