package edu.wm.cs.cs301.DavidNi.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import edu.wm.cs.cs301.DavidNi.R;

public class AMazeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // Seed of the maze
    private int seed;
    // Size of the maze, default to 0
    private int size = 0;
    // Generation method of the maze, default Boruvka
    private String generationMethod = "Boruvka";
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
                setSize(progress);
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
                    // Toast that checkbox was checked, for debugging
                    Toast.makeText(getApplicationContext(), "Perfect Checked", Toast.LENGTH_SHORT).show();
                    // Log message of room checkbox being unchecked, for debugging
                    Log.v("AMazeActivity","Rooms is checked");
                }
                // If the CheckBox is unchecked
                else {
                    rooms = false;
                    // Toast that checkbox was unchecked, for debugging
                    Toast.makeText(getApplicationContext(), "Perfect Unchecked", Toast.LENGTH_SHORT).show();
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
             * @param view The view that was clicked
             */
            @Override
            public void onClick(View view) {
                sendNewMazeData();
            }
        });

        // Button for revisiting a previous maze
        // Clicking gets previous maze configuration stored in Android internal storage (as file)
        Button revisit = findViewById(R.id.revisit);

        // Listener for the "revisit" button
        revisit.setOnClickListener(new View.OnClickListener() {
            /**
             * This method listens if the Revisit button was pressed.
             * If the Revisit button is pressed, get previous maze configuration stored in
             * Android internal storage (as file)
             * @param view The view that was clicked
             */
            @Override
            public void onClick(View view) {
                sendNewMazeData();
                //TODO replace sendNewMazeData with commented method below for P7 (to use persistent storage)
                //sendOldMazeData();
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
        this.generationMethod = adapterView.getItemAtPosition(position).toString();
        // Toast selected generation method, for debugging
        Toast.makeText(adapterView.getContext(),"Maze generation method selected: " + this.generationMethod, Toast.LENGTH_SHORT).show();
        // Log message of generation method, for debugging
        Log.v("AMazeActivity","Maze generation method selected: " + this.generationMethod);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /**
     * Helper method that sets the desired size of the maze to the given value
     * (intended to be the progress value of the "Skill level" SeekBar in AMazeActivity).
     * @param progress the progress of the Spinner
     */
    private void setSize(int progress) {
        this.size = progress;
    }

    /**
     * Helper method that sends the selected maze configuration to GeneratingActivity for maze generation.
     * The seed of the maze configuration is generated randomly in this method, and
     * the current maze configuration is stored in Android internal storage (as a file).
     * Prints Logcat verbose message for debugging purposes.
     */
    public void sendNewMazeData() {
        // Generate random seed
        Random rand = new Random();
        this.seed = rand.nextInt();
        // Write maze configuration to file
        // TODO uncomment writeFile for P7 (to use persistent storage)
        //writeFile(this.seed,this.size,this.generationMethod,this.rooms);

        // Toast message displaying maze configuration sent to GeneratingActivity, for debugging purposes
        Toast.makeText(getApplicationContext(), "Sent maze configuration (" + this.seed + "," + this.size + "," + this.generationMethod+ "," + this.rooms + ") for generating", Toast.LENGTH_SHORT).show();
        // Log message that displays the maze configuration sent to GeneratingActivity, for debugging purposes
        Log.v("AMazeActivity","Sent the following information to GeneratingActivity:\nSeed: " + this.seed + ", Size: " + this.size + ", Algorithm: " + this.generationMethod + ", Rooms: " + this.rooms);

        // Send maze configuration to GeneratingActivity using intent
        Intent intent = new Intent(this, GeneratingActivity.class);
        intent.putExtra("Seed", this.seed);
        intent.putExtra("Size", this.size);
        intent.putExtra("Algorithm",this.generationMethod);
        intent.putExtra("Rooms", this.rooms);
        startActivity(intent);
    }

    /**
     * Helper method that gets the previous maze configuration from internal storage,
     * and sends it to GeneratingActivity for maze generation.
     * If no previous maze configuration exists, error message is sent.
     * Prints Logcat verbose message for debugging purposes.
     */
    private void sendOldMazeData() {
        // Get previous maze configuration from file
        String mazeConfig = readFile();

        // If a previous maze configuration exists
        if (mazeConfig != null) {
            // Parse maze configuration data
            String[] mazeConfigSplit = mazeConfig.split(",");
            this.seed = Integer.parseInt(mazeConfigSplit[0]);
            this.size = Integer.parseInt(mazeConfigSplit[1]);
            this.generationMethod = mazeConfigSplit[2];
            this.rooms = Boolean.parseBoolean(mazeConfigSplit[3]);

            // Toast message displaying maze configuration sent to GeneratingActivity, for debugging purposes
            Toast.makeText(getApplicationContext(), "Sent maze configuration (" + this.seed + "," + this.size + "," + this.generationMethod+ "," + this.rooms + ") for generating", Toast.LENGTH_SHORT).show();
            // Log message that displays the maze configuration sent to GeneratingActivity, for debugging purposes
            Log.v("AMazeActivity","Sent the following information to GeneratingActivity:\nSeed: " + this.seed + ", Size: " + this.size + ", Algorithm: " + this.generationMethod + ", Rooms: " + this.rooms);

            // Send maze configuration to GeneratingActivity using intent
            Intent intent = new Intent(this, GeneratingActivity.class);
            intent.putExtra("Seed", this.seed);
            intent.putExtra("Size", this.size);
            intent.putExtra("Algorithm",this.generationMethod);
            intent.putExtra("Rooms", this.rooms);
            startActivity(intent);
        }

        // No previous maze configuration exists
        else {
            // Toast message displaying maze configuration sent to GeneratingActivity, for debugging purposes
            Toast.makeText(getApplicationContext(), "Error: No previous maze exists in persistent storage", Toast.LENGTH_SHORT).show();
            Log.e("AMazeActivity", "No Previous Maze Configuration exists");
        }
    }

    /**
     * Helper method that writes given maze configuration to a file.
     * Prints Logcat verbose message for debugging purposes.
     * @param seed Seed of the maze
     * @param size Size of the maze
     * @param Algorithm Generation algorithm of the maze
     * @param perfect Perfect status of the maze
     */
    private void writeFile(int seed, int size, String Algorithm, boolean perfect) {
        // Create string storing all data of maze config (delimited by ",")
        String mazeConfig = seed + "," + size + "," + Algorithm + "," + perfect;

        // Try to write the maze configuration to file "Maze Configuration.txt"
        // Note, each call will rewrite the previous file
        try {
            FileOutputStream fileOutputStream = openFileOutput("Maze Configuration.txt", MODE_PRIVATE);
            fileOutputStream.write(mazeConfig.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.close();
            // Log message of maze configuration saved in file, for debugging
            Log.v("AMazeActivity", "Maze config (" + mazeConfig + ") saved to file");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method that reads the previous maze configuration stored in file and returns it.
     * Prints Logcat verbose message for debugging purposes.
     * @return The previous maze configuration (stored as string, with information delimited by ",")
     */
    public String readFile() {
        // Try to get the previous maze configuration stored in file "Maze Configuration.txt"
        try {
            FileInputStream fileInputStream = openFileInput("Maze Configuration.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            // maze configuration always only one line, so just read first line of bufferedReader
            String mazeConfig = bufferedReader.readLine();

            if (mazeConfig != null)
                // Log message of maze configuration read from file, for debugging
                Log.v("AMazeActivity", "Maze config (" + mazeConfig + ") read from file");

            // Return the mazeConfig
            return mazeConfig;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // return null if error was thrown
        return null;
    }
}