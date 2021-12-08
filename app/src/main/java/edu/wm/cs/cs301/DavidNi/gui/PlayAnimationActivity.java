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
import android.widget.ToggleButton;

import edu.wm.cs.cs301.DavidNi.R;
import edu.wm.cs.cs301.DavidNi.generation.Maze;
import edu.wm.cs.cs301.DavidNi.gui.Constants.UserInput;
import edu.wm.cs.cs301.DavidNi.gui.Robot.Direction;

public class PlayAnimationActivity extends AppCompatActivity implements PlayingActivity{
    // Maze generated in GeneratingActivity.
    private Maze maze;
    // StatePlaying corresponding to this activity
    private StatePlaying statePlaying;
    // Shortest path out of maze.
    private int shortestPath;

    // Driver for traversing maze
    private RobotDriver driver;
    // Robot used to play animation
    private Robot robot;

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

        // Get the generated maze
        maze = Singleton.getInstance().getMaze();
        Singleton.getInstance().releaseMaze();

        // Get the shortest path out of maze (without jumping)
        shortestPath = maze.getDistanceToExit(maze.getStartingPosition()[0], maze.getStartingPosition()[1]);

        // Panel that statePlaying draws
        // MazePanel in Activity used to display maze game
        MazePanel panel = findViewById(R.id.animationMaze);

        // Initialize StatePlaying to play maze game.
        statePlaying = new StatePlaying(this);
        statePlaying.setMazeConfiguration(maze);
        statePlaying.start(panel);

        // Robot for traversing maze
        this.robot = new UnreliableRobot();

        // Intent for getting robot and driver configurations from GeneratingActivity
        Intent intent = getIntent();
        // Set driver based on given intent
        setDriver(intent.getStringExtra("Driver"));
        // Set robot configuration based on given intent
        setSensors(this.robot, intent.getStringExtra("robotConfig"));
        // Start robot sensor processes
        startSensors(this.robot);

        // Log message that displays driver and robot configuration received from GeneratingActivity, for debugging purposes
        Log.v("PlayAnimationActivity","Received the following information from GeneratingActivity:\nDriver: " + intent.getStringExtra("Driver") + ", Robot Configuration: " + intent.getStringExtra("robotConfig"));
        if (maze != null) {
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
                    // Log message to show button was toggled on, for debugging
                    Log.v("PlayAnimationActivity", "Toggled Show Map Button: ON");
                    statePlaying.keyDown(UserInput.TOGGLEFULLMAP);
                    statePlaying.keyDown(UserInput.TOGGLESOLUTION);
                    statePlaying.keyDown(UserInput.TOGGLELOCALMAP);
                }
                // Do not show map of maze if button is toggled off
                else {
                    // Log message to show button was toggled off, for debugging
                    Log.v("PlayAnimationActivity", "Toggled Show Map Button: OFF");
                    statePlaying.keyDown(UserInput.TOGGLEFULLMAP);
                    statePlaying.keyDown(UserInput.TOGGLESOLUTION);
                    statePlaying.keyDown(UserInput.TOGGLELOCALMAP);
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
                // Log message to show Zoom In button was pressed, for debugging
                Log.v("PlayAnimationActivity", "Zooming in on Map");
                statePlaying.keyDown(UserInput.ZOOMIN);
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
                // Log message to show Zoom Out button was pressed, for debugging
                Log.v("PlayAnimationActivity", "Zooming out on Map");
                statePlaying.keyDown(UserInput.ZOOMOUT);
            }
        });

        // Back button to title
        Button back = findViewById(R.id.backButton);
        // Listener for back button
        back.setOnClickListener(new View.OnClickListener() {
            /**
             * This method listens if the "Back" button was pressed.
             * If the "Back" button was pressed return to the title screen.
             * @param view The view that was clicked
             */
            @Override
            public void onClick(View view) {
                // Log message for returning to title
                Log.v("PlayAnimationActivity","Returning to Title");

                // Return to title
                Intent intent = new Intent(getApplicationContext(), AMazeActivity.class);
                startActivity(intent);
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
        // TODO call goToLosing when robot runs out of energy
        // Refactor code so that journey info comes from robot

        // Log message to show you lost the game, for debugging purposes
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
     * Helper method called after beating the maze game by statePlaying.
     * Communicates journey information (path length,shortest possible path, and energy consumption) to WinningActivity before starting it.
     */
    @Override
    public void goToWinning() {
        //TODO get path length from robot
        int pathLength = 0;

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

    /**
     * This method sets PlayAnimationActivity's driver to the driver specified in GeneratingActivity.
     * @param Driver string specifying the type of robot driver used in PlayAnimationActivity
     */
    private void setDriver(String Driver) {
        switch(Driver) {
            case "WallFollower":
                this.driver = new WallFollower();
                break;
            case "Wizard":
                this.driver = new Wizard();
                break;
            default:
                this.driver = null;
                break;
        }
    }

    /**
     * This method sets the sensors of the given robot based on the given robot configuration.
     * Robot configuration is expected to be provided by GeneratingActivity.
     * 4 types of robot configurations: Premium (all sensors reliable), Mediocre (front and back sensors reliable), SoSo (left and right sensors reliable),
     * and Shaky (all unreliable sensors).
     * Default sensor configuration is Premium.
     * @param robot robot that will have its sensors set
     * @param robotConfig Desired robot configuration
     */
    private void setSensors(Robot robot, String robotConfig){
        switch(robotConfig) {
            // Front and back sensors reliable, unreliable left and right sensors
            case "Mediocre":
                for (Direction dir : Direction.values()) {
                    DistanceSensor sensor;
                    if (dir == Direction.FORWARD || dir == Direction.BACKWARD)
                        sensor = new ReliableSensor();
                    else
                        sensor = new UnreliableSensor();
                    sensor.setMaze(this.maze);
                    robot.addDistanceSensor(sensor, dir);
                }
                break;
            // Left and right sensors reliable, unreliable front and back sensors
            case "SoSo":
                for (Direction dir : Direction.values()) {
                    DistanceSensor sensor;
                    if (dir == Direction.LEFT || dir == Direction.RIGHT)
                        sensor = new ReliableSensor();
                    else
                        sensor = new UnreliableSensor();
                    sensor.setMaze(this.maze);
                    robot.addDistanceSensor(sensor, dir);
                }
                break;
            // All sensors unreliable
            case "Shaky":
                for (Direction dir : Direction.values()) {
                    DistanceSensor sensor = new UnreliableSensor();
                    sensor.setMaze(this.maze);
                    robot.addDistanceSensor(sensor, dir);
                }
                break;
            // Default robot configuration is premium (all reliable sensors)
            default:
                for (Direction dir : Direction.values()) {
                    DistanceSensor sensor = new ReliableSensor();
                    sensor.setMaze(this.maze);
                    robot.addDistanceSensor(sensor, dir);
                }
                break;
        }
    }

    /**
     * This method starts all Failure and Repair processes for a given robot.
     * @param robot robot that will have all its sensor processes started
     */
    private void startSensors(Robot robot){
        for (Direction dir : Direction.values()) {
            try {
                // Sensor repair process (4 sec operational, 2 sec broken)
                robot.startFailureAndRepairProcess(dir, 4000, 2000);
                // Wait 1.3 seconds before starting next Failure and Repair Process.
                Thread.sleep(1300);
            }
            catch (Exception e) {
            }
        }
    }
}