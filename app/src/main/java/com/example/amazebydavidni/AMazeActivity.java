package com.example.amazebydavidni;

import androidx.appcompat.app.AppCompatActivity;

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

public class AMazeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String generationMethod = "DFS";
    private boolean rooms = true;
    private int size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_maze);

        // SeekBar for maze complexity/size
        TextView sizeText = (TextView)findViewById(R.id.mazeSizeText);
        SeekBar sizeSeekBar = (SeekBar)findViewById(R.id.sizeSeekBar);

        // SeekBar Listener
        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                String output = "Skill Level: " + progress;
                sizeText.setText(output);
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

        // Checkbox to determine if you want rooms
        CheckBox roomCheck = findViewById(R.id.roomCheckBox);

        // Checkbox Listener
        roomCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(roomCheck.isChecked()) {
                    rooms = true;
                    // Toast that checkbox was checked, for debugging
                    Toast.makeText(getApplicationContext(), "Perfect Checked", Toast.LENGTH_SHORT).show();
                    // Log message of room checkbox being unchecked, for debugging
                    Log.v("AMazeActivity","Rooms is checked");
                }
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
        Button explore = findViewById(R.id.explore);

        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNewMazeData();
            }
        });

        // Button for exploring maze
        Button revisit = findViewById(R.id.revisit);

        revisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readFile();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        this.generationMethod = adapterView.getItemAtPosition(position).toString();
        // Toast selected generation method, for debugging
        Toast.makeText(adapterView.getContext(),"Maze generation method selected: " + this.generationMethod, Toast.LENGTH_SHORT).show();
        // Log message of generation method, for debugging
        Log.v("AMazeActivity","Maze generation method selected: " + this.generationMethod);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void sendNewMazeData() {
        Random rand = new Random();
        int seed = rand.nextInt();
        writeFile(seed,this.size,this.generationMethod,this.rooms);
        Log.v("AMazeActivity","Send the following information to GeneratingActivity:\nSeed: " + seed + ", Size: " + this.size + ", Algorithm: " + this.generationMethod + ", Rooms: " + this.rooms);
    }

    private void writeFile(int seed, int size, String Algorithm, boolean perfect) {
        String mazeConfig = seed + "," + size + "," + Algorithm + "," + perfect;
        try {
            FileOutputStream fileOutputStream = openFileOutput("Maze Configuration.txt", MODE_PRIVATE);
            fileOutputStream.write(mazeConfig.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.close();
            Toast.makeText(getApplicationContext(), "Maze config (" + mazeConfig + ") stored in file", Toast.LENGTH_SHORT).show();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFile() {
        try {
            FileInputStream fileInputStream = openFileInput("Maze Configuration.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();

            String lines;
            while ((lines = bufferedReader.readLine()) != null) {
                stringBuffer.append(lines + "\n");
            }

            Log.v("AMazeActivity", stringBuffer.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}