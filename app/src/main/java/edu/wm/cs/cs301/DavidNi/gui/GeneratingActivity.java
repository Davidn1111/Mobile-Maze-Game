package edu.wm.cs.cs301.DavidNi.gui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Handler;

import edu.wm.cs.cs301.DavidNi.R;
import edu.wm.cs.cs301.DavidNi.generation.MazeFactory;
import edu.wm.cs.cs301.DavidNi.generation.Order.Builder;
import edu.wm.cs.cs301.DavidNi.generation.StubOrder;

public class GeneratingActivity extends AppCompatActivity {
    // Seed of the maze
    private int seed;
    // Size of the maze
    private int size;
    // Generation method of the maze
    private String builder;
    // Flag to check if maze has rooms
    private boolean rooms;
    // Driver for traversing maze, default to null
    private String driver = null;
    // Robot sensor configuration, default Premium
    private String robotConfig = "Premium";

    // Global singleton for sharing generated maze
    public Singleton singleton;

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
    // private instance of maze generation thread
    private mazeGenerationThread mazeGeneration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating);

        // Get maze configuration from AMazeActivity
        Intent intent = getIntent();
        this.seed = intent.getIntExtra("Seed", 13);
        this.size = intent.getIntExtra("Size", 0);
        this.builder = intent.getStringExtra("Builder");
        this.rooms = intent.getBooleanExtra("Rooms",true);

        // Log message that displays the maze configuration received from AMazeActivity, for debugging purposes
        Log.v("GeneratingActivity","Received the following information from AMazeActivity:\nSeed: " + this.seed + ", Size: " + this.size + ", Builder: " + this.builder + ", Rooms: " + this.rooms);

        // Radio button for driver selection
        driverGroup = findViewById(R.id.driverRadio);

        // Spinner for robot configuration selection
        // Spinner for selecting robot configuration
        Spinner robotSpinner = findViewById(R.id.spinnerRobotConfig);
        // Populate spinner with all robot options
        ArrayAdapter<CharSequence> robotAdapter = ArrayAdapter.createFromResource(this,R.array.robots, android.R.layout.simple_spinner_item);
        robotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        robotSpinner.setAdapter(robotAdapter);
        robotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                robotConfig = adapterView.getItemAtPosition(position).toString();
                // Log message of robot configuration, for debugging
                Log.v("GeneratingActivity","Selected Robot Configuration: " + robotConfig);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Progress bar of maze generation
        mazeProgress = findViewById(R.id.genProgressBar);
        // Text displaying progress status
        progressText = findViewById(R.id.progressText);

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
                Log.v("GeneratingActivity","Returning to Title");

                // Interrupt maze generation thread
                mazeGeneration.interrupt();

                // Return to title
                Intent intent = new Intent(getApplicationContext(), AMazeActivity.class);
                startActivity(intent);
            }
        });

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

        // Start maze generation thread
        mazeGeneration = new mazeGenerationThread();
        mazeGeneration.start();
    }

    /**
     * Private thread class that runs maze generation.
     * Maze generation progress is reflected in UI progress bar.
     * @author David Ni
     */
    private class mazeGenerationThread extends Thread implements Runnable{
        /**
         * This method runs the maze generation thread.
         * Maze generation is preformed by MazeFactory.order().
         * This method updates progress bar to reflect maze generation progress.
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
            MazeFactory factory = new MazeFactory();
            StubOrder order = new StubOrder(size,getBuilder(),!rooms,seed);
            factory.order(order);
            // Simulate maze generation
            while (order.getProgress() != 100) {
                // Code used to kill thread when back button is called
                try {
                    mazeGenerationThread.sleep(0);
                }
                catch (InterruptedException e){
                    return;
                }

                // Set progress status based on order progress
                progressStatus = order.getProgress();
                // Order progress is sporadic and values can be over 100 when almost done generating
                // Default these values to 99
                if (progressStatus > 100)
                    progressStatus = 99;

                // Use handler to communicate back with UI thread
                mazeGenHandler.post(new Runnable() {
                    /**
                     * This method uses a handler to communicate the maze generation's progress to the UI's progress bar.
                     * Sets the UI's progress bar to display progress of the maze generation.
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
                    //Debugging message when maze generation is done
                    Log.v("GeneratingActivity","Maze generation done");

                    // Set the singleton to reference generated maze
                    singleton = new Singleton();
                    singleton.setMaze(order.getMazeReference());
                    // Set the progress bar to 100% (needed since progress updates are sporadic)
                    progressStatus = 100;
                    progressText.setText("Building Maze: 100%");
                    mazeProgress.setProgress(100);

                    // If no driver has been selected, send out a warning.
                    if (driver == null) {
                        try{
                            popup.setTitle("Reminder: No Driver Selected");
                            popup.setMessage("Maze has finished generating.\nPlease select a driver to start the maze game.");
                            popup.show();
                            //Debugging message for warning when no driver is selected but maze is done generating.
                            Log.v("GeneratingActivity","Reminder: No Driver selected and maze generation finished.");
                        }
                        catch (Exception e){
                            Log.v("GeneratingActivity", "Killed thread");
                        }
                    }
                    // If the maze is finished generating and a driver is selected,
                    // go to the corresponding playing activity.
                    else
                        startPlaying();
                }
            });
        }
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

        // Log message of selected driver, for debugging
        Log.v("GeneratingActivity","Selected Driver: " + this.driver);

        // Set the popup warning message
        this.popup.setTitle("Warning: Maze Still Generated");
        this.popup.setMessage("Please wait for the maze to finish generating, before selecting a driver.");
        // If a driver was selected before the maze was done generating, send out a warning
        // Make sure that no additional warnings are sent if popup is currently open
        if(this.progressStatus != 100) {
            this.popup.show();
            //Debugging message for warning when driver is selected before maze is done generating.
            Log.v("GeneratingActivity","Warning: Driver selected while maze still generating.");
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
            // Log message to show that app is moving to PlayManuallyActivity, for debugging.
            Log.v("GeneratingActivity", "Starting PlayManuallyActivity");

            // Start PlayManuallyActivity
            // Maze is referenced using global variable
            // Driver inferred to be Manual, and robotConfig is irrelevant for manual play
            Intent intent = new Intent(this, PlayManuallyActivity.class);
            startActivity(intent);
        }
        else{
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

    /**
     * Converts builder algorithm specified from AMazeActivity as a Builder enumerated type.
     * @return the builder algorithm from AMazeActivity as a Builder enumerated type.
     */
    public Builder getBuilder() {
        switch(this.builder) {
            case "Boruvka":
                return Builder.Boruvka;
            case "Prim":
                return Builder.Prim;
            // Default builder is DFS
            default:
                return Builder.DFS;
        }
    }

    /**
     * Override onDestroy, so Handler for maze generation properly
     * stops thread if GeneratingActivity is close.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mazeGenHandler.removeCallbacks(mazeGeneration);
    }
}