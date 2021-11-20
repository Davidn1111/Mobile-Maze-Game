package com.example.amazebydavidni;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PlayManuallyActivity extends AppCompatActivity {
    // TODO set maze to be a Maze object in P7
    // maze generated in GeneratingActivity.
    Object maze = GeneratingActivity.maze;

    // Length of the path taken by the player
    private int pathLength = 0;
    // Toggle button to show solution
    private ToggleButton solutionButton;
    // Toggle button to show map of maze
    private ToggleButton mazeButton;
    // Toggle button to show currently visible walls of the maze
    private ToggleButton wallsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_manually);

        if (this.maze != null) {
            // Toast to show that GeneratingActivity global maze reference exists, for debugging purposes
            Toast.makeText(getApplicationContext(), "Received Global Maze Reference from GeneratingActivity\nMaze: " + this.maze, Toast.LENGTH_SHORT).show();
            // Log message to show that GeneratingActivity global maze reference exists, for debugging purposes
            Log.v("PlayManuallyActivity", "Global Maze Reference from GeneratingActivity not null");
        }

        // Toggle button for showing solution
        solutionButton=findViewById(R.id.manualSolutionButton);
        solutionButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /**
             * This method shows the solution of the maze if the "Show Solution" Button is toggled on.
             * If the button is togged off, the solution of the maze is not shown.
             * @param compoundButton Button view of the toggle button.
             * @param checked True if the button is toggled on, False otherwise.
             */
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                // Show solution if button is toggled on
                if(solutionButton.isChecked()){
                    // Toast to show solution button was toggled on, for debugging
                    Toast.makeText(getApplicationContext(),"Showing Solution",Toast.LENGTH_SHORT).show();
                    // Log message to show solution button was toggled on, for debugging
                    Log.v("PlayManuallyActivity", "Toggled Solution Button: ON");
                }
                // Do not show solution if button is toggled off
                else{
                    // Toast to show solution button was toggled off, for debugging
                    Toast.makeText(getApplicationContext(),"No Longer Showing Solution",Toast.LENGTH_SHORT).show();
                    // Log message to show solution button was toggled off, for debugging
                    Log.v("PlayManuallyActivity", "Toggled Solution Button: OFF");
                }
            }
        });

        // Toggle button for showing map of maze
        mazeButton=findViewById(R.id.mazeButton);
        mazeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /**
             * This method shows the map of the maze if the "Show Maze" Button is toggled on.
             * If the button is togged off, the map of the maze is not shown.
             * @param compoundButton Button view of the toggle button.
             * @param checked True if the button is toggled on, False otherwise.
             */
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                // Show map of maze if button is toggled on
                if(mazeButton.isChecked()){
                    // Toast to show maze button was toggled on, for debugging
                    Toast.makeText(getApplicationContext(),"Showing Map of Maze",Toast.LENGTH_SHORT).show();
                    // Log message to show maze button was toggled on, for debugging
                    Log.v("PlayManuallyActivity", "Toggled Show Maze Button: ON");
                }
                // Do not show map of maze if button is toggled off
                else{
                    // Toast to show maze button was toggled off, for debugging
                    Toast.makeText(getApplicationContext(),"No Longer Showing Map of Maze",Toast.LENGTH_SHORT).show();
                    // Log message to show maze button was toggled off, for debugging
                    Log.v("PlayManuallyActivity", "Toggled Show Maze Button: OFF");
                }
            }
        });

        // Toggle button for showing visible walls of the maze
        wallsButton=findViewById(R.id.wallButton);
        wallsButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /**
             * This method shows the currently visible walls of the maze if the "Show Walls" Button is toggled on.
             * If the button is togged off, the visible walls of the maze are not shown.
             * @param compoundButton Button view of the toggle button.
             * @param checked True if the button is toggled on, False otherwise.
             */
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                // Show map of maze if button is toggled on
                if(wallsButton.isChecked()){
                    // Toast to show walls button was toggled on, for debugging
                    Toast.makeText(getApplicationContext(),"Showing Visible Walls",Toast.LENGTH_SHORT).show();
                    // Log message to show walls button was toggled on, for debugging
                    Log.v("PlayManuallyActivity", "Toggled Show Walls Button: ON");
                }
                // Do not show map of maze if button is toggled off
                else{
                    // Toast to show walls button was toggled off, for debugging
                    Toast.makeText(getApplicationContext(),"No Longer Showing Visible Walls",Toast.LENGTH_SHORT).show();
                    // Log message to show walls button was toggled off, for debugging
                    Log.v("PlayManuallyActivity", "Toggled Show Walls Button: OFF");
                }
            }
        });

        // ImageButton for moving forward in maze
        ImageButton upButton = findViewById(R.id.upButton);
        // Listener for Up button
        upButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This method listens if the "Up" button was pressed.
             * If the "Up" button is pressed, move forward one cell in the maze and
             * increment the path length by 1.
             * @param view The view that was clicked
             */
            @Override
            public void onClick(View view) {
                pathLength++;
                // Toast to show Up button was pressed, for debugging
                Toast.makeText(getApplicationContext(),"Up Button Pressed \nPathLength Now: " + pathLength,Toast.LENGTH_SHORT).show();
                // Log message to show Up button was pressed, for debugging
                Log.v("PlayManuallyActivity", "Up Button Pressed");
                // Log message showing updated path length after Up button was pressed, for debugging.
                Log.v("PlayManuallyActivity", "Path Length Now: " + pathLength);
            }
        });

        // ImageButton for jumping forward in maze
        ImageButton jumpButton = findViewById(R.id.jumpButton);
        // Listener for jumpButton
        jumpButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This method listens if the "Jump" button was pressed.
             * If the "Jump" button is pressed, jump forward one cell in the maze and
             * increment the path length by 1.
             * @param view The view that was clicked
             */
            @Override
            public void onClick(View view) {
                pathLength++;
                // Toast to show Jump button was pressed, for debugging
                Toast.makeText(getApplicationContext(),"Jump Button Pressed \nPathLength Now: " + pathLength,Toast.LENGTH_SHORT).show();
                // Log message to show Jump button was pressed, for debugging
                Log.v("PlayManuallyActivity", "Jump Button Pressed");
                // Log message showing updated path length after Jump button was pressed, for debugging.
                Log.v("PlayManuallyActivity", "Path Length Now: " + pathLength);
            }
        });

        // ImageButton for rotating left in the maze
        ImageButton leftButton = findViewById(R.id.leftButton);
        // Listener for leftButton
        leftButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This method listens if the "Left" button was pressed.
             * If the "Left" button was pressed, rotate the player Counter-Clockwise 90 degrees.
             * @param view The view that was clicked
             */
            @Override
            public void onClick(View view) {
                // Toast to show Left button was pressed, for debugging
                Toast.makeText(getApplicationContext(),"Rotating Left",Toast.LENGTH_SHORT).show();
                // Log message to show Left button was pressed, for debugging
                Log.v("PlayManuallyActivity", "Left Button Pressed");
            }
        });

        // ImageButton for rotating right in the maze
        ImageButton rightButton = findViewById(R.id.rightButton);
        // Listener for rightButton
        rightButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This method listens if the "Right" button was pressed.
             * If the "Right" button was pressed, rotate the player Clockwise 90 degrees.
             * @param view The view that was clicked
             */
            @Override
            public void onClick(View view) {
                // Toast to show Left button was pressed, for debugging
                Toast.makeText(getApplicationContext(),"Rotating Right",Toast.LENGTH_SHORT).show();
                // Log message to show Left button was pressed, for debugging
                Log.v("PlayManuallyActivity", "Right Button Pressed");
            }
        });

        // ImageButton for zooming into the map
        ImageButton zoomInButton = findViewById(R.id.zoomInButton);
        // Listener for leftButton
        zoomInButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This method listens if the "Zoom In" button was pressed.
             * If the "Zoom In" button was pressed, decrease the size of the map (zoom into it).
             * @param view The view that was clicked
             */
            @Override
            public void onClick(View view) {
                // Toast to show Zoom In button was pressed, for debugging
                Toast.makeText(getApplicationContext(),"Zooming In",Toast.LENGTH_SHORT).show();
                // Log message to show Zoom In button was pressed, for debugging
                Log.v("PlayManuallyActivity", "Zooming in On Map");
            }
        });

        // ImageButton for zooming into the map
        ImageButton zoomOutButton = findViewById(R.id.zoomOutButton);
        // Listener for leftButton
        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This method listens if the "Zoom Out" button was pressed.
             * If the "Zoom Out" button was pressed, increase the size of the map (zoom out on it).
             * @param view The view that was clicked
             */
            @Override
            public void onClick(View view) {
                // Toast to show Zoom Out button was pressed, for debugging
                Toast.makeText(getApplicationContext(),"Zooming Out",Toast.LENGTH_SHORT).show();
                // Log message to show Zoom Out button was pressed, for debugging
                Log.v("PlayManuallyActivity", "Zooming out on Map");
            }
        });

        // ImageButton (ShortCut) for going to the win activity. Placeholder for maze game.
        // For P6 Only
        Button goToWin = findViewById(R.id.ShortCut);
        // Listener for goToWin button
        goToWin.setOnClickListener(new View.OnClickListener() {
            /**
             * Method listens if the "ShortCut" button (placeholder for the maze game) is pressed.
             * Communicates journey information to WinningActivity before starting it.
             * Hardcoded shortest path length of 100 used.
             */
            @Override
            public void onClick(View view) {
                goToWinning(100);
            }
        });
    }

    /**
     * Helper method called after beating the maze game.
     * Communicates journey information (path length and shortest possible path) to WinningActivity before starting it.
     * @param shortestPath Shortest possible path (without jumping) to beat the maze.
     */
    private void goToWinning(int shortestPath) {
        // Toast that you won the game, for debugging purposes
        Toast.makeText(getApplicationContext(),"Going to WinningActivity",Toast.LENGTH_SHORT).show();
        // Log message to show you won the game, for debugging purposes
        Log.v("PlayManuallyActivity", "Going to WinningActivity");
        // Log message to show what journey information was sent to WinningActivity, for debugging purposes
        Log.v("PlayManuallyActivity", "Sent the following information to WinningActivity:\nPath length: " + pathLength + ", Shortest Path: "
                + shortestPath);

        // Send journey information to WinningActivity
        Intent intent = new Intent(this, WinningActivity.class);
        intent.putExtra("PathLength", this.pathLength);
        intent.putExtra("ShortestPath", shortestPath);
        startActivity(intent);
    }

}