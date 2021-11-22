package edu.wm.cs.cs301.DavidNi.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import edu.wm.cs.cs301.DavidNi.R;

public class PlayAnimationActivity extends AppCompatActivity {
    // TODO set maze to be a Maze object in P7
    // maze generated in GeneratingActivity.
    Object maze = GeneratingActivity.maze;

    // Driver for traversing maze
    private String driver;
    // Robot sensor configuration
    private String robotConfig;

    // Speed of the animation, defaulted to 0
    private int speed = 0;
    // Starting energy of the robot, defaulted to 3500
    private int initialEnergy = 3500;
    // Current energy consumed by the robot
    private int energyConsumed;

    // ProgressBar displaying the robot's energy
    private ProgressBar energyProgress;
    // Toggle button for showing map (whole maze with solution and visible walls) during animation/automatic play
    private ToggleButton mapButton;
    // Images representing robot's sensors (green = operational and red = non-operational)
    // Flags representing robot's operational status (true if operational, false otherwise)
    // Forward sensor
    ImageView fSensor;
    boolean fSensorStatus = true;
    // Back sensor
    ImageView bSensor;
    boolean bSensorStatus = true;
    // Left sensor
    ImageView lSensor;
    boolean lSensorStatus = true;
    // Right sensor
    ImageView rSensor;
    boolean rSensorStatus = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_animation);

        // Get drive and robot configuration from GeneratingActivity
        Intent intent = getIntent();
        this.driver = intent.getStringExtra("Driver");
        this.robotConfig = intent.getStringExtra("robotConfig");
        // Log message that displays driver and robot configuration received from GeneratingActivity, for debugging purposes
        Log.v("PlayAnimationActivity","Received the following information from GeneratingActivity:\nDriver: " + driver + ", Robot Configuration: " + robotConfig);

        if (this.maze != null) {
            // Toast message displaying info received from GeneratingActivity, for debugging purposes
            Toast.makeText(getApplicationContext(), "Received Driver: " + this.driver + " ,Robot Configuration: " + this.robotConfig + " ,and non-null maze reference from GeneratingActivity", Toast.LENGTH_SHORT).show();
            // Log message to show that GeneratingActivity global maze reference exists, for debugging purposes
            Log.v("PlayAnimationActivity", "Global Maze Reference from GeneratingActivity not null");
        }

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
                Log.v("PlayAnimationActivity", "Zooming in on Map");
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

        // SeekBar for animation speed
        // TODO P7: figure out appropriate range for speed (currently 0-2) and to change animation speed
        SeekBar speedSeekBar = findViewById(R.id.speedBar);
        TextView speedText = findViewById(R.id.speedText);
        // SeekBar Listener
        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * This method listens to changes made to the SeekBar for animation speed.
             * Gets the desired speed of the animation from the SeekBar's progress value.
             * Updates text to show currently selected animation speed.
             * Prints Logcat verbose message for debugging purposes.
             * @param seekBar SeekBar that determines the size of the maze
             * @param progress Current progress level of the SeekBar, value is the size of the maze
             * @param fromUser True if progress change was initiated by the user
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String output = "Set Speed: " + progress;
                speedText.setText(output);
                // Toast message about the animation speed selected on the SeekBar, for debugging
                Toast.makeText(getApplicationContext(), "Speed now: " + progress, Toast.LENGTH_SHORT).show();
                // Log message about the animation speed selected on the SeekBar, for debugging
                Log.v("PlayAnimationActivity","Speed set to : " + progress);
                speed = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Progress bar representing robot's energy
        energyProgress = findViewById(R.id.robotEnergyBar);
        // TODO P7: Have energy bar update along with game instead of one time call (currently used for debugging P6)
        updateEnergyBar();

        // TODO Write code such that sensor images change color to reflect status
        // ImageViews representing operational status of robot's sensors
        this.fSensor = findViewById(R.id.fSensor);
        this.bSensor = findViewById(R.id.bSensor);
        this.lSensor = findViewById(R.id.leftSensor);
        this.rSensor = findViewById(R.id.rightSensor);

        // TODO P7 have sensors call updateSensorImages during game instead of using one time call (currently used for debugging P6)
        // Check if updateSensorImages correctly sets sensors to green if they are operational
        updateSensorImages();
        // Toast message about the animation speed selected on the SeekBar, for debugging
        Toast.makeText(getApplicationContext(), "Testing Sensor UI, with all Sensors Operational", Toast.LENGTH_SHORT).show();

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
                goToLosing(400,500,600);
            }
        });
    }

    /**
     * Helper method which updates UI to show sensor's status.
     * If a sensor is non-operation, its UI representation will be given a red tint.
     * If a sensor is operational, its UI representation will be given a green tint.
     */
    private void updateSensorImages() {
        Log.v("PlayAnimationActivity","Updating sensor UI color to represent operational status");
        // Set forward sensor color
        if (this.fSensorStatus) {
            this.fSensor.setColorFilter(Color.argb(255, 80, 220, 100)); // Green Tint
        }
        else {
            this.fSensor.setColorFilter(Color.argb(255, 255, 36, 0)); // Red Tint
        }
        // Set backwards sensor color
        if (this.bSensorStatus) {
            this.bSensor.setColorFilter(Color.argb(255, 80, 220, 100)); // Green Tint
        }
        else {
            this.bSensor.setColorFilter(Color.argb(255, 255, 36, 0)); // Red Tint
        }
        // Set left sensor color
        if (this.lSensorStatus) {
            this.lSensor.setColorFilter(Color.argb(255, 80, 220, 100)); // Green Tint
        }
        else {
            this.lSensor.setColorFilter(Color.argb(255, 255, 36, 0)); // Red Tint
        }
        // Set right sensor color
        if (this.rSensorStatus) {
            this.rSensor.setColorFilter(Color.argb(255, 80, 220, 100)); // Green Tint
        }
        else {
            this.rSensor.setColorFilter(Color.argb(255, 255, 36, 0)); // Red Tint
        }
    }

    /**
     * Helper method used to update the robot's energy bar.
     * Sets the robot's energy bar to represent the percentage of energy still left (current energy/initial energy).
     */
    private void updateEnergyBar() {
        // Current energy consumed by the robot, used hardcoded value of 1000 until P7
        // TODO P7: get energyConsumed from RobotDriver.getEnergyConsumption()
        this.energyConsumed = 1000;
        float percentage =  (((float)this.initialEnergy - this.energyConsumed)/(float)this.initialEnergy)* 100;
        this.energyProgress.setProgress((int)percentage);

        // Toast message of robot's energy bar changing, used for debugging
        Toast.makeText(getApplicationContext(), "Test Setting Robot's energy bar set to : " + (int)percentage + "%", Toast.LENGTH_SHORT).show();
        // Log message of robot's energy bar percentage changing, used for debugging
        Log.v("PlayAnimationActivity","Robot's energy bar set to : " + (int)percentage + "%");
    }

    /**
     * Helper method called robot loses the maze game.
     * Communicates robot's journey information (path length,shortest possible path, and energy consumption) to LosingActivity before starting it.
     * @param pathLength Path length travelled by robot during its journey.
     * @param shortestPath Shortest possible path (without jumping) to beat the maze.
     * @param energyConsumed Energy consumed by the robot during its journey.
     */
    private void goToLosing(int pathLength, int shortestPath,int energyConsumed) {
        // Toast that you won the game, for debugging purposes
        Toast.makeText(getApplicationContext(),"Going to LosingActivity",Toast.LENGTH_SHORT).show();
        // Log message to show you won the game, for debugging purposes
        Log.v("PlayAnimationActivity", "Going to LosingActivity");
        // Log message to show what journey information was sent to LosingActivity, for debugging purposes
        Log.v("PlayAnimationActivity", "Sent the following information to LosingActivity:\nPath length: " + pathLength + ", Shortest Path: "
                + shortestPath + ", Energy Consumption: " + energyConsumed);

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
        // Toast that you won the game, for debugging purposes
        Toast.makeText(getApplicationContext(),"Going to WinningActivity",Toast.LENGTH_SHORT).show();
        // Log message to show you won the game, for debugging purposes
        Log.v("PlayAnimationActivity", "Going to WinningActivity");
        // Log message to show what journey information was sent to WinningActivity, for debugging purposes
        Log.v("PlayAnimationActivity", "Sent the following information to WinningActivity:\nPath length: " + pathLength + ", Shortest Path: "
                + shortestPath + ", Energy Consumption: " + energyConsumed);


        // Send journey information to WinningActivity
        Intent intent = new Intent(this, WinningActivity.class);
        intent.putExtra("PathLength", pathLength);
        intent.putExtra("ShortestPath", shortestPath);
        intent.putExtra("energyConsumption", energyConsumed);
        startActivity(intent);
    }
}