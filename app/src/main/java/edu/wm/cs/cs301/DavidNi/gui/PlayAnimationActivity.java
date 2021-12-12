package edu.wm.cs.cs301.DavidNi.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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
    // MazePanel in Activity used to display maze game
    private MazePanel panel;
    // StatePlaying corresponding to this activity
    private StatePlaying statePlaying;
    // Shortest path out of maze.
    private int shortestPath;

    // Driver for traversing maze
    private RobotDriver driver;
    // Robot used to play animation
    private Robot robot;

    // Handler to communicate background thread (which handles playing animation) with this (UI) thread
    final private Handler animationHandler = new Handler();
    // private instance of "play animation" thread
    private animationThread animation;
    // Flag to tell animation thread to stop
    private boolean flag = true;
    // Flag to tell when animation is paused
    private boolean paused = false;

    // Speed of the animation, defaulted to 0 delay
    private int speed = 0;

    // ProgressBar displaying the robot's energy
    private ProgressBar energyProgress;
    // Toggle button for showing map (whole maze with solution and visible walls) during animation/automatic play
    private ToggleButton mapButton;
    // Images representing robot's sensors (green = operational and red = non-operational)
    // Forward sensor
    private ImageView fSensor;
    // Back sensor
    private ImageView bSensor;
    // Left sensor
    private ImageView lSensor;
    // Right sensor
    private ImageView rSensor;

    // Media player for playing background music
    MediaPlayer musicPlayer;

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
        panel = findViewById(R.id.animationMaze);

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

        // Image views corresponding to sensors, updated based on robot status
        this.fSensor = findViewById(R.id.fSensor);
        this.bSensor = findViewById(R.id.bSensor);
        this.lSensor = findViewById(R.id.leftSensor);
        this.rSensor = findViewById(R.id.rightSensor);

        //Start animation
        animation = new animationThread();
        animation.start();

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
             * @param checked True if the button is toggled on, False otherwise.
             */
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                // Show map of maze if button is toggled on
                if (mapButton.isChecked()) {
                    // Log message to show button was toggled on, for debugging
                    Log.v("PlayAnimationActivity", "Toggled Show Map Button: ON");
                }
                // Do not show map of maze if button is toggled off
                else {
                    // Log message to show button was toggled off, for debugging
                    Log.v("PlayAnimationActivity", "Toggled Show Map Button: OFF");
                }
                // Toggle the map with each click
                statePlaying.keyDown(UserInput.TOGGLEFULLMAP);
                statePlaying.keyDown(UserInput.TOGGLESOLUTION);
                statePlaying.keyDown(UserInput.TOGGLELOCALMAP);
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

                // Stop the animation
                flag = false;
                // Kill the handler
                animationHandler.removeCallbacks(animation);
                // Kill all sensor threads
                stopSensors(robot);
                // Stop the music
                stopMusic();

                // Return to title
                Intent intent = new Intent(getApplicationContext(), AMazeActivity.class);
                startActivity(intent);
            }
        });

        // Start/Stop button
        Button stopButton = findViewById(R.id.stopButton);
        // Listener for stop button
        stopButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This method listens if the "Start/Stop" button was pressed.
             * If the "Start/Stop" button was pressed and the animation is paused,
             * reverse the value of the "paused" tag.
             * The animation stalls with a while loop as long as the paused tag is true.
             * @param view The view that was clicked
             */
            @Override
            public void onClick(View view) {
                // Reverse the value of the paused tag.
                paused = !paused;

                // Change text on button
                if (paused){
                    stopButton.setText("Start");
                    // Log message that animation was paused
                    Log.v("PlayAnimationActivity","Stopping Animation");
                }
                else {
                    stopButton.setText("Stop");
                    // Log message that animation started again
                    Log.v("PlayAnimationActivity","Starting Animation");
                }
            }
        });

        // SeekBar for animation speed
        SeekBar speedSeekBar = findViewById(R.id.speedBar);
        TextView speedText = findViewById(R.id.speedText);
        // SeekBar Listener
        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * This method listens to changes made to the SeekBar for animation speed.
             * Gets the desired speed of the animation from the SeekBar's progress value.
             * Speed = milliseconds delay between each driver step.
             * Slow = 200 millisecond delay, Normal = 100 millisecond delay, Fast = no delay.
             * Updates text to show currently selected animation speed.
             * Prints Logcat verbose message for debugging purposes.
             * @param seekBar SeekBar that determines the size of the maze
             * @param progress Current progress level of the SeekBar, value is the size of the maze
             * @param fromUser True if progress change was initiated by the user
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String output;
                switch(progress){
                    case 0:
                        output = "Speed: Slow";
                        break;
                    case 1:
                        output = "Speed: Normal";
                        break;
                    default:
                        output = "Speed: Fast";
                        break;
                }
                speedText.setText(output);
                // Log message about the animation speed selected on the SeekBar, for debugging
                Log.v("PlayAnimationActivity",output);
                // Convert seekBar progress to desired delay (default speed = 0)
                speed = (2-progress)*100;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /**
     * Private thread class that automatically plays the maze game,
     * using maze and robot driver specified in GeneratingActivity.
     *
     * @author David Ni
     */
    private class animationThread extends Thread implements Runnable{
        // Flag to check if robot has stopped or not
        // Used to determine when to stop animation and move to losing state
        boolean robotStopped = false;
        /**
         * This method runs the animationThread by driving the robot one
         * step closer to exit, until the robot reaches the exit or stops (lack of energy,crash,etc).
         * This method then opens the appropriate activity based on robot's journey (winning or losing).
         */
        @Override
        public void run() {
            // Start music
            playMusic();

            // Start robot sensor processes
            startSensors(robot);
            // Set up robot and driver for maze game
            robot.setStatePlaying(statePlaying);
            driver.setMaze(maze);
            driver.setRobot(robot);
            // Progress bar representing robot's energy
            energyProgress = findViewById(R.id.robotEnergyBar);

            // Loop that drives robot one step closer to exit,
            while (!robot.isAtExit() && !robotStopped && flag) {

                // Loop that stalls run method while game is paused
                while(paused){
                }

                // Handler used to update graphics after each step
                animationHandler.post(new Runnable() {
                    /**
                     * This method uses a Handler to update the MazePanel's graphic, after
                     * a step is taken by the robot.
                     * Responsible for updating energy bar and sensor visuals.
                     */
                    @Override
                    public void run() {
                        updateSensorImages();
                        updateEnergyBar();
                    }
                });

                // Drive one step forward
                try {
                    driver.drive1Step2Exit();
                }
                catch (Exception e){
                    robotStopped = true;
                }

                // Set delay between each step based on animation speed set by user
                try {
                    animationThread.sleep(speed);
                }
                catch (InterruptedException e) {
                    return;
                }
            }
            // Stop the music because automated play is finished
            stopMusic();

            // If robot has not stopped try to go through exit
            if (robot.isAtExit() && !robotStopped && flag) {
                try {
                    driver.stepOutExit();
                } catch (Exception e) {
                    robotStopped = true;
                }
            }

            // If robot has stopped go to losing screen
            if(robotStopped && flag) {
                // Kill all sensor threads
                stopSensors(robot);
                // Go to losing screen
                goToLosing();
            }
        }
    }

    /**
     * Helper method which updates UI to show sensor's status.
     * If a sensor is non-operation, its UI representation will be given a red tint.
     * If a sensor is operational, its UI representation will be given a green tint.
     */
    private void updateSensorImages() {
        for (Direction dir : Direction.values()) {
            ImageView currSensor;
            // Set currSensor based on direction
            switch(dir){
                case LEFT:
                    currSensor = lSensor;
                    break;
                case RIGHT:
                    currSensor = rSensor;
                    break;
                case FORWARD:
                    currSensor = fSensor;
                    break;
                case BACKWARD:
                    currSensor = bSensor;
                    break;
                default:
                    currSensor = null;
                    break;
            }

            if (robot.getSensorStatus(dir)) {
                currSensor.setColorFilter(Color.argb(255, 80, 220, 100)); // Green Tint
            }
            else
                currSensor.setColorFilter(Color.argb(255, 255, 36, 0)); // Red Tint
        }

        Log.v("PlayAnimationActivity","Updating sensor UI color to represent operational status");
    }

    /**
     * Helper method used to update the robot's energy bar.
     * Sets the robot's energy bar to represent the percentage of energy still left (current energy/initial energy).
     */
    private void updateEnergyBar() {
        float energyConsumed = this.driver.getEnergyConsumption();
        // Initial energy of robot always 3500
        float initialEnergy = 3500;
        // Get percentage to display energy
        float percentage =  ((initialEnergy - energyConsumed)/initialEnergy)* 100;
        // Due to rounding, display 1% if percentage < 1 (for aesthetics only)
        if (percentage < 1 && percentage != 0)
            percentage = 1;

        // Display energy on energy bar
        this.energyProgress.setProgress((int)percentage);

        // Log message of robot's energy bar percentage changing, used for debugging
        Log.v("PlayAnimationActivity","Robot's energy bar set to : " + (int)percentage + "%");
    }

    /**
     * Helper method called robot loses the maze game.
     * Communicates robot's journey information (path length,shortest possible path, and energy consumption) to LosingActivity before starting it.
     */
    private void goToLosing() {
        int energyConsumption = (int) driver.getEnergyConsumption();
        int pathLength = driver.getPathLength();

        // Log message to show you lost the game, for debugging purposes
        Log.v("PlayAnimationActivity", "Going to LosingActivity");
        // Log message to show what journey information was sent to LosingActivity, for debugging purposes
        Log.v("PlayAnimationActivity", "Sent the following information to LosingActivity:\nPath length: " + pathLength + ", Shortest Path: "
                + shortestPath + ", Energy Consumption: " + energyConsumption);

        // Send journey information to LosingActivity
        Intent intent = new Intent(this, LosingActivity.class);
        intent.putExtra("PathLength", pathLength);
        intent.putExtra("ShortestPath", shortestPath);
        intent.putExtra("energyConsumption", energyConsumption);
        startActivity(intent);
    }

    /**
     * Helper method called after beating the maze game by statePlaying.
     * Communicates journey information (path length,shortest possible path, and energy consumption) to WinningActivity before starting it.
     */
    @Override
    public void goToWinning() {
        int energyConsumption = (int) driver.getEnergyConsumption();
        int pathLength = driver.getPathLength();

        // Log message to show you won the game, for debugging purposes
        Log.v("PlayAnimationActivity", "Going to WinningActivity");
        // Log message to show what journey information was sent to WinningActivity, for debugging purposes
        Log.v("PlayAnimationActivity", "Sent the following information to WinningActivity:\nPath length: " + pathLength + ", Shortest Path: "
                + shortestPath + ", Energy Consumption: " + energyConsumption);

        // Send journey information to WinningActivity
        Intent intent = new Intent(this, WinningActivity.class);
        intent.putExtra("PathLength", pathLength);
        intent.putExtra("ShortestPath", shortestPath);
        intent.putExtra("energyConsumption", energyConsumption);
        startActivity(intent);
    }

    /**
     * This method sets PlayAnimationActivity's driver to the driver specified in GeneratingActivity.
     * @param Driver string specifying the type of robot driver used in PlayAnimationActivity
     */
    private void setDriver(String Driver) {
        switch(Driver) {
            case "Wall-Follower":
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
     * Each Failure and Repair process has sensor operational for 4 seconds and then broken for 2 seconds.
     * 1.3 second delay between each Failure and Repair process starting.
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

    /**
     * This method stops all Failure and Repair processes for a given robot.
     * @param robot robot that will have all its sensor processes started
     */
    private void stopSensors(Robot robot){
        for (Direction dir : Direction.values()) {
            try {
                robot.stopFailureAndRepairProcess(dir);
            }
            catch (Exception e) {
            }
        }
    }

    /**
     * This method initializes the MediaPlayer to start playing music (looped)
     */
    public void playMusic(){
        if (musicPlayer == null) {
            musicPlayer = MediaPlayer.create(this,R.raw.song);
            musicPlayer.setLooping(true);
        }
        musicPlayer.start();
    }

    /**
     * This method releases the MediaPlayer to stop music from playing.
     */
    public void stopMusic(){
        if (musicPlayer != null) {
            musicPlayer.release();
            musicPlayer = null;
        }
    }
}