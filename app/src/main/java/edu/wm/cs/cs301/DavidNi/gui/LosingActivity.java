package edu.wm.cs.cs301.DavidNi.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import edu.wm.cs.cs301.DavidNi.R;

public class LosingActivity extends AppCompatActivity {
    // Length of the path taken to win
    private int pathLength;
    // Shortest possible path length (without jumping) to win
    private int shortestPath;
    // Energy consumption used by robot on journey
    // Negative value used to denote manual play
    private int energyConsumption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_losing);

        // Get journey information from playing activity
        Intent intent = getIntent();
        this.pathLength = intent.getIntExtra("PathLength", 0);
        this.shortestPath = intent.getIntExtra("ShortestPath", 0);
        this.energyConsumption = intent.getIntExtra("energyConsumption", -1);

        // Toast message displaying journey information received from playing activity, for debugging purposes
        Toast.makeText(getApplicationContext(), "You lost the game with \nPath Length: " + this.pathLength+ ", ShortestPath: " + this.shortestPath + ",energyConsumption: " + this.energyConsumption, Toast.LENGTH_SHORT).show();
        // Log message that displays journey information received from playing activity, for debugging purposes
        Log.v("LosingActivity","Received the following information\nPath Length: " + this.pathLength+ ", ShortestPath: " + this.shortestPath + ",energyConsumption: " + this.energyConsumption);

        // Set the winning screen's text to match given journey information
        // TextView for path length
        TextView pathLengthText = findViewById(R.id.lPathLengthText);
        pathLengthText.setText("Length of Path: " + this.pathLength);

        // TextView for shortest path
        TextView shortestPathText = findViewById(R.id.lShortestPathText);
        shortestPathText.setText("Length of Shortest Path: "+ this.shortestPath);

        // TextView for energy consumption
        TextView energyConsumptionText = findViewById(R.id.lEnergyConsumptionText);
        energyConsumptionText.setText("Energy Consumed: " + this.energyConsumption);
    }
}