package com.example.amazebydavidni;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class GeneratingActivity extends AppCompatActivity {
    private int seed;
    private int size;
    private String generationMethod;
    private boolean rooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating);

        Intent intent = getIntent();
        this.seed = intent.getIntExtra("Seed", 13);
        this.size = intent.getIntExtra("Size", 0);
        this.generationMethod = intent.getStringExtra("Algorithm");
        this.rooms = intent.getBooleanExtra("Rooms",true);

        // Toast message displaying maze configuration received from AMazeActivity, for debugging purposes
        Toast.makeText(getApplicationContext(), "Received maze configuration (" + this.seed + "," + this.size + "," + this.generationMethod+ "," + this.rooms + ") for generating", Toast.LENGTH_SHORT).show();
        // Log message that displays the maze configuration received from AMazeActivity, for debugging purposes
        Log.v("GeneratingActivity","Received the following information from AMazeActivity:\nSeed: " + this.seed + ", Size: " + this.size + ", Algorithm: " + this.generationMethod + ", Rooms: " + this.rooms);
    }
}