package com.example.amazebydavidni;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    // Driver for traversing maze
    private String driver;

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

        // Spinner for driver selection
        Spinner mazeGenSpinner = (Spinner)findViewById(R.id.spinnerDriver);
        // Populate spinner with all driver options (default value is null/empty)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.drivers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mazeGenSpinner.setAdapter(adapter);
        mazeGenSpinner.setOnItemSelectedListener(this);
    }

    /**
     * This method is used to get the driver selected in the "driver" Spinner (in GeneratingActivity).
     * Default selected value is null/empty.
     * Prints Logcat verbose message for debugging purposes.
     * @param adapterView The AdapterView where selection happened
     * @param view The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id The row id of the item selected
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        //Set the driver to null, if spinner selects first element (No driver has been selected)
        if (position == 0) {
            this.driver = null;
            // Log message of generation method, for debugging
            Log.v("GeneratingActivity","No driver selected");
        }
        else {
            this.driver = adapterView.getItemAtPosition(position).toString();
            // Toast selected generation method, for debugging
            Toast.makeText(adapterView.getContext(),"Driver selected: " + this.driver, Toast.LENGTH_SHORT).show();
            // Log message of generation method, for debugging
            Log.v("GeneratingActivity","Driver selected: " + this.driver);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}