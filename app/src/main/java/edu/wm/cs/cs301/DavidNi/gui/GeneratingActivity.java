package edu.wm.cs.cs301.DavidNi.gui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import edu.wm.cs.cs301.DavidNi.R;

public class GeneratingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // Seed of the maze
    private int seed;
    // Size of the maze
    private int size;
    // Generation method of the maze
    private String generationMethod;
    // Flag to check if maze has rooms
    private boolean rooms;
    // Driver for traversing maze, default to null
    private String driver = null;
    // Robot sensor configuration, default Premium
    private String robotConfig = "Premium";

    // TODO set maze to be a Maze object in P7, currently set to string "testMaze" for P6
    // Global maze object that is generated in this activity.
    static Object maze = "testMaze";

    // Builder for pop messages
    AlertDialog.Builder popup;

    // Radio group for driver selection
    private RadioGroup driverGroup;
    // Text view of the progress bar
    private TextView progressText;
    // Progress bar for maze generation
    private ProgressBar mazeProgress;
    // Counter of the progress bar's status
    private int progressStatus = 0;
    // Handler to communicate background thread (which handles maze gen) with this (UI) thread
    final private Handler mazeGenHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating);

        // Get maze configuration from AMazeActivity
        Intent intent = getIntent();
        this.seed = intent.getIntExtra("Seed", 13);
        this.size = intent.getIntExtra("Size", 0);
        this.generationMethod = intent.getStringExtra("Algorithm");
        this.rooms = intent.getBooleanExtra("Rooms",true);

        // Toast message displaying maze configuration received from AMazeActivity, for debugging purposes
        Toast.makeText(getApplicationContext(), "Received maze configuration (" + this.seed + "," + this.size + "," + this.generationMethod+ "," + this.rooms + ") for generating", Toast.LENGTH_SHORT).show();
        // Log message that displays the maze configuration received from AMazeActivity, for debugging purposes
        Log.v("GeneratingActivity","Received the following information from AMazeActivity:\nSeed: " + this.seed + ", Size: " + this.size + ", Algorithm: " + this.generationMethod + ", Rooms: " + this.rooms);

        // Radio button for driver selection
        driverGroup = findViewById(R.id.driverRadio);

        // Spinner for robot configuration selection
        // Spinner for selecting robot configuration
        Spinner robotSpinner = findViewById(R.id.spinnerRobotConfig);
        // Populate spinner with all robot options
        ArrayAdapter<CharSequence> robotAdapter = ArrayAdapter.createFromResource(this,R.array.robots, android.R.layout.simple_spinner_item);
        robotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        robotSpinner.setAdapter(robotAdapter);
        robotSpinner.setOnItemSelectedListener(this);

        // Progress bar of maze generation
        mazeProgress = findViewById(R.id.genProgressBar);
        // Text displaying progress status
        progressText = findViewById(R.id.progressText);

        // Create a popup builder
        // Popup Dialog Box
        popup = new AlertDialog.Builder(GeneratingActivity.this);
        popup.setCancelable(true);
        popup.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            /**
             * This method closes the AlertDialog when the "Ok" button is pressed.
             * @param dialogInterface The dialog interface of the desired AlertDialog
             * @param i Position of the button that was clicked
             */
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        // Background Thread for P6 meant to simulate maze generation thread progress
        new Thread(new Runnable() {
            /**
             * This method runs a background thread simulating maze generation.
             * The thread stalls every 50 ms to simulate the maze loading.
             *
             * Sends popup warnings for the following issues:
             * A driver is selected before maze generation is complete.
             * A driver is not selected after maze generation is complete.
             * <P>
             *     If the maze is done generating, and a driver was selected,
             *     this method starts the corresponding "playing" activity.
             * </P>
             */
            @Override
            public void run() {
                // Simulate maze generation
                while (progressStatus < 100) {
                    progressStatus++;
                    // Stall the progress bar loading by 50 ms every 1% to simulate actual loading
                    android.os.SystemClock.sleep(50);

                    // Use handler to communicate back with UI thread
                    mazeGenHandler.post(new Runnable() {
                        /**
                         * This method uses a handler to communicate the maze generation's progress to the UI's progress bar.
                         * Sets the UI's progress bar to display progress of the maze generation.
                         * This method also displays a warning message, if a driver is selected while maze generation has not yet been completed.
                         */
                        @Override
                        public void run() {
                            String progress = "Building Maze: " + progressStatus + "%";
                            progressText.setText(progress);
                            mazeProgress.setProgress(progressStatus);
                        }
                    });
                }
                mazeGenHandler.post(new Runnable() {
                    /**
                     * This method uses a handler to communicate to the UI that maze generation has finished.
                     * If no driver had been selected when maze generation is completed,
                     * this method displays a reminder to select a driver.
                     * If a driver has been selected when maze generation is completed,
                     * this method starts the corresponding "playing" activity.
                     */
                    @Override
                    public void run() {
                        // If no driver has been selected, send out a warning.
                        if (driver == null) {
                            try{
                                popup.setTitle("Reminder: No Driver Selected");
                                popup.setMessage("Maze has finished generating.\nPlease select a driver to start the maze game.");
                                popup.show();
                            }
                            catch (Exception e){
                                Log.v("GeneratingActivity", "Maze Generation Thread Killed");
                            }
                        }
                        // If the maze is finished generating and a driver is selected,
                        // go to the corresponding playing activity.
                        else
                            startPlaying();
                    }
                });
            }
        }).start();
    }

    /**
     * This method is used to get the robot configuration selected in the corresponding Spinner (in GeneratingActivity).
     * Default robot configuration set to premium
     * Prints Logcat verbose message for debugging purposes.
     * @param adapterView The AdapterView where selection happened
     * @param view The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id The row id of the item selected
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
        ((TextView) adapterView.getChildAt(0)).setTextSize(18);
        // Set robot configuration to the selected value
        this.robotConfig = adapterView.getItemAtPosition(position).toString();
        // Toast selected robot configuration, for debugging
        Toast.makeText(adapterView.getContext(),"Selected Robot Configuration: " + this.robotConfig, Toast.LENGTH_SHORT).show();
        // Log message of robot configuration, for debugging
        Log.v("GeneratingActivity","Selected Robot Configuration: " + this.robotConfig);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /**
     * This method is called whenever a radio button in the "Drivers" radio group, is pressed.
     * Whenever a radio button is pressed, the maze's driver is set to the button's corresponding driver.
     * If the maze is not finished generating and a driver is selected,
     * send out a warning message for the use to wait.
     * If the maze is finished generating and a driver is selected,
     * this method starts the corresponding "playing" activity.
     * @param view The current view
     */
    public void checkButton(View view) {
        int radioID = driverGroup.getCheckedRadioButtonId();
        // Set the current driver to the radio button that was checked
        // Radio button representing current driver selected
        RadioButton driverButton = findViewById(radioID);
        // Set the selected driver based on the button selected
        this.driver = driverButton.getText().toString();

        // Toast selected driver, for debugging
        Toast.makeText(this,"Selected Driver: " + this.driver,Toast.LENGTH_SHORT).show();
        // Log message of selected driver, for debugging
        Log.v("GeneratingActivity","Selected Driver: " + this.driver);

        // Set the popup warning message
        this.popup.setTitle("Warning: Maze Still Generated");
        this.popup.setMessage("Please wait for the maze to finish generating, before selecting a driver.");
        // If a driver was selected before the maze was done generating, send out a warning
        // Make sure that no additional warnings are sent if popup is currently open
        if(this.progressStatus != 100) {
            this.popup.show();
        }

        // If the maze is finished generating and a driver is selected,
        // go to the corresponding playing activity.
        else {
            startPlaying();
        }
    }

    /**
     * Helper method that starts the corresponding playing activity (PlayManuallyActivity or PlayAnimationActivity)
     * based on the selected driver.
     * If going to PlayAnimationActivity, this method sends the selected driver and sensor configuration information beforehand.
     * The maze generated by GeneratingActivity is stored as a global variable, so that either playing activity can access it efficiently.
     */
    private void startPlaying() {
        if (this.driver.equals("Manual")) {
            // Toast to showing app is moving to PlayManuallyActivity, for debugging
            Toast.makeText(this,"Starting PlayManuallyActivity",Toast.LENGTH_SHORT).show();
            // Log message to show that app is moving to PlayManuallyActivity, for debugging.
            Log.v("GeneratingActivity", "Starting PlayManuallyActivity");

            // Start PlayManuallyActivity
            // Maze is referenced using global variable
            // Driver inferred to be Manual, and robotConfig is irrelevant for manual play
            Intent intent = new Intent(this, PlayManuallyActivity.class);
            startActivity(intent);
        }
        else{
            // Toast to show that app is moving to PlayAnimationActivity with given driver (Wizard or WallFollower), for debugging
            Toast.makeText(this,"Starting PlayAnimationActivity with Driver: " + driver,Toast.LENGTH_SHORT).show();
            // Log message to show that app is moving to PlayAnimationActivity with given driver (Wizard or WallFollower), for debugging
            Log.v("GeneratingActivity","Starting PlayAnimationActivity with Driver: " + driver);
            // Log message displaying maze, driver, and robotConfiguration sent PlayAnimationActivity, for debugging
            Log.v("GeneratingActivity","Sent the following information to PlayAnimationActivity:\nDriver: " + driver + ", Robot Configuration: " + robotConfig);

            // Send selected driver and robot configuration to PlayAnimationActivity (used for playing the maze game), then start PlayAnimationActivity
            Intent intent = new Intent(this, PlayAnimationActivity.class);
            intent.putExtra("Driver", this.driver);
            intent.putExtra("robotConfig", this.robotConfig);
            startActivity(intent);
        }
    }
}