package edu.wm.cs.cs301.DavidNi.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import edu.wm.cs.cs301.DavidNi.R;

public class AMazeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // Seed of the maze
    private int seed;
    // Size of the maze, default to 0
    private int size = 0;
    // Generation method of the maze, default Boruvka
    private String builder = "Boruvka";
    // Flag to check if maze has rooms, default to true
    private boolean rooms = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_maze);

        // SeekBar for maze complexity/size
        // Range 0-10, starts at value 0
        TextView sizeText = findViewById(R.id.mazeSizeText);
        SeekBar sizeSeekBar = findViewById(R.id.sizeSeekBar);

        // SeekBar Listener
        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * This method listens to changes made to the SeekBar corresponding to the size of the maze.
             * Gets the desired size of the maze from the SeekBar's progress value.
             * Updates the Title screen text to show currently selected maze size/skill level.
             * Prints Logcat verbose message for debugging purposes.
             * @param seekBar SeekBar that determines the size of the maze
             * @param progress Current progress level of the SeekBar, value is the size of the maze
             * @param fromUser True if progress change was initiated by the user
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String output = "Skill Level: " + progress;
                sizeText.setText(output);
                // Log message about the size of the maze selected on the SeekBar, for debugging
                Log.v("AMazeActivity","Maze size set to: " + progress);
                size = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Spinner for maze generation selection
        Spinner mazeGenSpinner = findViewById(R.id.spinnerMazeGen);
        // Populate spinner with all generation options (default Boruvka)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.generation, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mazeGenSpinner.setAdapter(adapter);
        mazeGenSpinner.setOnItemSelectedListener(this);

        // Checkbox to determine if maze has rooms
        // Default checked to be true
        CheckBox roomCheck = findViewById(R.id.roomCheckBox);

        // Checkbox Listener
        roomCheck.setOnClickListener(new View.OnClickListener() {
            /**
             * This method listens for changes to the CheckBox, that determines if rooms are desired for the maze.
             * If the CheckBox is checked, set the room flag to be true.
             * If the CheckBox is unchecked, set the room flag to be false.
             * Prints Logcat verbose message for debugging purposes.
             * @param view The view that was clicked
             */
            @Override
            public void onClick(View view) {
                // If the CheckBox is checked
                if(roomCheck.isChecked()) {
                    rooms = true;
                    // Log message of room checkbox being unchecked, for debugging
                    Log.v("AMazeActivity","Rooms is checked");
                }
                // If the CheckBox is unchecked
                else {
                    rooms = false;
                    // Log message of room checkbox being unchecked, for debugging
                    Log.v("AMazeActivity","Rooms is unchecked");
                }
            }
        });

        // Button for exploring maze
        // Clicking generates a random seed
        // and sends current maze settings to GeneratingActivity for maze generation.
        Button explore = findViewById(R.id.explore);

        // Listener for the "explore" button
        explore.setOnClickListener(new View.OnClickListener() {
            /**
             * This method listens if the Explore button was pressed.
             * If the Explore button is pressed, generate a random seed and send
             * current maze configuration to GeneratingActivity for maze generation.
             * Stores maze configuration in Android internal storage.
             * @param view The view that was clicked
             */
            @Override
            public void onClick(View view) {
                exploreMaze();
            }
        });

        // Button for revisiting a previous maze
        // Clicking gets previous maze configuration stored in Android internal storage (as file)
        Button revisit = findViewById(R.id.revisit);

        // Listener for the "revisit" button
        revisit.setOnClickListener(new View.OnClickListener() {
            /**
             * This method listens if the Revisit button was pressed.
             * If the Revisit button is pressed, send previous maze configuration stored in
             * Android internal storage to GeneratingActivity for maze generation.
             * @param view The view that was clicked
             */
            @Override
            public void onClick(View view) {
                revisitMaze();
            }
        });

    }

    /**
     * This method is used to get the generation method selected in the "maze generation" Spinner (in AMazeActivity).
     * Prints Logcat verbose message for debugging purposes.
     * @param adapterView The AdapterView where selection happened
     * @param view The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id The row id of the item selected
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        this.builder = adapterView.getItemAtPosition(position).toString();
        // Log message of generation method, for debugging
        Log.v("AMazeActivity","Maze generation method selected: " + this.builder);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /**
     * Helper method that sends the selected maze configuration to GeneratingActivity for maze generation.
     * The seed of the maze configuration is generated randomly in this method, and
     * the current maze configuration is stored in Android internal storage (shared preferences).
     * Prints Logcat verbose message for debugging purposes.
     */
    public void exploreMaze() {
        // Generate random seed
        Random rand = new Random();
        this.seed = rand.nextInt();

        // Create shared preference for maze data
        SharedPreferences mazePreferences = getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor  = mazePreferences.edit();

        // Save maze preferences
        editor.putInt("Seed",this.seed);
        editor.putInt("Size",this.size);
        editor.putString("Builder",this.builder);
        editor.putBoolean("Rooms",this.rooms);
        editor.apply();

        // Log message that displays the maze configuration sent to GeneratingActivity, for debugging purposes
        Log.v("AMazeActivity","Sent the following information to GeneratingActivity:\nSeed: " + this.seed + ", Size: " + this.size + ", Builder: " + this.builder + ", Rooms: " + this.rooms);

        // Send maze configuration to GeneratingActivity using intent
        Intent intent = new Intent(this, GeneratingActivity.class);
        intent.putExtra("Seed", this.seed);
        intent.putExtra("Size", this.size);
        intent.putExtra("Builder",this.builder);
        intent.putExtra("Rooms", this.rooms);
        startActivity(intent);
    }

    /**
     * Helper method that gets the previous maze configuration from internal storage (shared preferences),
     * and sends it to GeneratingActivity for maze generation.
     * If no previous maze configuration exists, error message is sent.
     * Prints Logcat verbose message for debugging purposes.
     */
    private void revisitMaze() {
        // Create shared preference for maze data
        SharedPreferences mazePreferences = getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);

        // If previous maze preferences exist, send to GeneratingActivity
        if (mazePreferences != null && mazePreferences.contains("Seed")) {
            // Log message stating that previous maze is being revisited
            Log.v("AMazeActivity", "Revisiting Previous Maze");

            // Send maze configuration to GeneratingActivity using intent
            Intent intent = new Intent(this, GeneratingActivity.class);
            intent.putExtra("Seed", mazePreferences.getInt("Seed", 13));
            intent.putExtra("Size", mazePreferences.getInt("Size", 0));
            intent.putExtra("Builder", mazePreferences.getString("Builder", "Boruvka"));
            intent.putExtra("Rooms", mazePreferences.getBoolean("Rooms", true));
            startActivity(intent);
        }

        // Produce warning if no maze has been saved in shared preferences
        else {
            Toast.makeText(getApplicationContext(), "No previous maze to revisit", Toast.LENGTH_SHORT).show();
            Log.v("AMazeActivity","No previous maze to revisit");
        }
    }
}