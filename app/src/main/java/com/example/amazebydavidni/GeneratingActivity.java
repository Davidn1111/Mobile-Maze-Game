package com.example.amazebydavidni;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
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

    // Flag that determines if a previous warning was given.
    // Used to prevent multiple warnings from appearing for the same driver selection (during maze generation).
    private boolean warningGiven = false;

    // Radio group for driver selection
    private RadioGroup driverGroup;
    // Radio button representing current driver selected
    private RadioButton driverButton;
    // Spinner for selecting robot configuration
    private Spinner robotSpinner;

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
        robotSpinner = findViewById(R.id.spinnerRobotConfig);
        // Populate spinner with all robot options
        ArrayAdapter<CharSequence> robotAdapter = ArrayAdapter.createFromResource(this,R.array.robots, android.R.layout.simple_spinner_item);
        robotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        robotSpinner.setAdapter(robotAdapter);
        robotSpinner.setOnItemSelectedListener(this);

        // Progress bar of maze generation
        mazeProgress = findViewById(R.id.genProgressBar);
        // Text displaying progress status
        progressText = findViewById(R.id.progressText);

        // Background Thread for P6 meant to simulate maze generation thread progress
        new Thread(new Runnable() {
            // Flag to tell if popup is open (prevents popups from opening when one is already open)
            boolean popupOpen = false;

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
                // Popup Dialog Box
                AlertDialog.Builder popup = new AlertDialog.Builder(GeneratingActivity.this);
                popup.setCancelable(true);
                popup.setTitle("Warning: Maze Still Generated");
                popup.setMessage("Please wait for the maze to finish generating, before selecting a driver.");

                popup.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    /**
                     * This method closes the AlertDialog when the "Ok" button is pressed.
                     * @param dialogInterface The dialog interface of the desired AlertDialog
                     * @param i Position of the button that was clicked
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        popupOpen = false;
                    }
                });

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
                            String progress = "Maze generation: " + progressStatus + "%";
                            progressText.setText(progress);
                            mazeProgress.setProgress(progressStatus);
                            // If a driver was selected before the maze was done generating, send out a warning
                            // Make sure that no additional warnings are sent if popup is currently open
                            // or if a warning was previously given
                            if(driver != null && !popupOpen && !warningGiven) {
                                popup.show();
                                popupOpen = true;
                                warningGiven = true;
                            }
                        }
                    });
                }
                // Wait 1000 ms before sending a warning message
                android.os.SystemClock.sleep(1000);
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
                            popup.setTitle("Reminder: No Driver Selected");
                            popup.setMessage("Maze has finished generating. Please select a driver to start the maze game.");
                            popup.show();
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
     * If the maze is finished generating and a driver is selected,
     * this method starts the corresponding "playing" activity.
     * @param view The current view
     */
    public void checkButton(View view) {
        int radioID = driverGroup.getCheckedRadioButtonId();
        // Set the current driver to the radio button that was checked
        driverButton = findViewById(radioID);
        // Set the selected driver based on the button selected
        this.driver = driverButton.getText().toString();

        // Toast selected driver, for debugging
        Toast.makeText(this,"Selected Driver: " + this.driver,Toast.LENGTH_SHORT).show();
        // Log message of selected driver, for debugging
        Log.v("GeneratingActivity","Selected Driver: " + this.driver);

        // Reset warning given
        warningGiven = false;

        // If the maze is finished generating and a driver is selected,
        // go to the corresponding playing activity.
        if (progressStatus == 100)
            startPlaying();
    }

    /**
     * Helper method that starts the corresponding playing activity (PlayManuallyActivity or PlayAnimationActivity)
     * based on the selected driver.
     * Afterwards, this method passes the selected maze, driver, and robot configuration to the playing activity
     * for the maze game.
     */
    private void startPlaying() {
        if (this.driver.equals("Manual")) {
            // TODO add correct intents for PlayManuallyActivity (maze reference?)
            // Send maze configuration to GeneratingActivity using intent
            Intent intent = new Intent(this, PlayManuallyActivity.class);
            startActivity(intent);

            // Toast to showing app is moving to PlayManuallyActivity, for debugging
            Toast.makeText(this,"Starting PlayManuallyActivity",Toast.LENGTH_SHORT).show();
            // Log message to show that app is moving to PlayManuallyActivity, for debugging.
            Log.v("GeneratingActivity", "Starting PlayManuallyActivity");
        }
        else{
            //TODO add correct intents for PlayAnimationActivity (maze reference?)
            Intent intent = new Intent(this, PlayAnimationActivity.class);
            startActivity(intent);

            // Toast to show that app is moving to PlayAnimationActivity with given driver (Wizard or WallFollower), for debugging
            Toast.makeText(this,"Starting PlayAnimationActivity with driver: " + driver,Toast.LENGTH_SHORT).show();
            // Log message to show that app is moving to PlayAnimationActivity with given driver (Wizard or WallFollower), for debugging
            Log.v("GeneratingActivity","Starting PlayAnimationActivity with driver: " + driver);
        }
    }
}