package edu.wm.cs.cs301.DavidNi.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
        shortestPathText.setText("Shortest Walk Path: "+ this.shortestPath);

        // TextView for energy consumption
        TextView energyConsumptionText = findViewById(R.id.lEnergyConsumptionText);
        energyConsumptionText.setText("Energy Consumed: " + this.energyConsumption);

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
                // Toast for returning to title
                Toast.makeText(getApplicationContext(), "Returning to Title", Toast.LENGTH_SHORT).show();
                // Log message for returning to title
                Log.v("LosingActivity","Returning to Title");

                // Return to title
                Intent intent = new Intent(getApplicationContext(), AMazeActivity.class);
                startActivity(intent);
            }
        });
    }
}