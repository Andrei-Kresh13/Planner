package com.example.planer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private EditText title;
    private EditText description;
    private Button saveButton;
    private static DatabaseHelper db;

    private final int[] COLORS = {Color.rgb(123,3,35), Color.BLUE, Color.CYAN};
    private int currentColorIndex = 0;
    private Handler colorHandler;
    private View colorView;
    private boolean isColorChanging = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DatabaseHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopUpDialog();
            }
        });

        byPassActivity();

        colorView = findViewById(R.id.relative);

        startColorChanging();
    }

    private void byPassActivity(){
        if (db.getCount() > 0){
            startActivity(new Intent(MainActivity.this, ListTaskActivity.class));
            finish();
        }
    }

    private void createPopUpDialog() {
        alertDialogBuilder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.pop_up,null);

        //установление видов
        title = view.findViewById(R.id.popup_title);
        description = view.findViewById(R.id.popup_description);
        saveButton = view.findViewById(R.id.popup_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!title.getText().toString().isEmpty() && !description.getText().toString().isEmpty()){
                    saveTodo(view);
                }else {
                    Snackbar.make(view,"Поля не могут быть пустыми ", Snackbar.LENGTH_SHORT);
                }

            }
        });
        alertDialogBuilder.setView(view);
        alertDialog = alertDialogBuilder.create();//creating out dialog object
        alertDialog.show();

    }

    private void startColorChanging() {
        colorHandler = new Handler();
        colorHandler.postDelayed(colorRunnable, 1000); // Изменение цвета каждую секунду
    }

    private Runnable colorRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentColorIndex < COLORS.length) {
                int color = COLORS[currentColorIndex];
                colorView.setBackgroundColor(color);
                currentColorIndex++;
            } else {
                currentColorIndex = 0;
            }

            colorHandler.postDelayed(this, 1000);
        }
    };

    private void saveTodo(View view) {
        Task task = new Task();

        //get the text
        String getTitle = title.getText().toString().trim();
        String getDesc = description.getText().toString().trim();

        //set the text to the instance variable
        task.setTitle(getTitle);
        task.setDescription(getDesc);

        db.addTask(task);



        Snackbar.make(view, "Задача добавлена в базу",Snackbar.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
                //todo : then to move to next screen - details screen
                startActivity(new Intent(MainActivity.this, ListTaskActivity.class));
            }
        }, 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}