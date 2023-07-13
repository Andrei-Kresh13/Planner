package com.example.planer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ListTaskActivity extends AppCompatActivity {


    private final List<Task> taskList1 = new ArrayList<>();
    private TaskAdapter adapter;
    private RecyclerView recyclerView;
    private DatabaseHelper db;

    private FloatingActionButton fab;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    private EditText title;
    private EditText description;
    private Button saveButton;

    private final int[] COLORS = {Color.rgb(123,3,35), Color.BLUE, Color.CYAN};
    private int currentColorIndex = 0;
    private Handler colorHandler;
    private View colorView;
    private boolean isColorChanging = false;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listtask);



        db = new DatabaseHelper(this);
        fab = findViewById(R.id.list_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createPopUpDialog();
            }
        });


        List<Task> taskList = db.getAllTasks();

        for (Task task : taskList){
            taskList1.add(task);
            Log.d("FORLOOP","Дата добавлена " + task.getDateTaskAdded());
        }


        recyclerView = findViewById(R.id.recyclerView);
        adapter = new TaskAdapter(this, taskList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter.notifyDataSetChanged();


        colorView = findViewById(R.id.constraint);

        startColorChanging();

    }

    private void createPopUpDialog() {
        builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.pop_up,null);

        //установка видов
        title = view.findViewById(R.id.popup_title);
        description = view.findViewById(R.id.popup_description);
        saveButton = view.findViewById(R.id.popup_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!title.getText().toString().isEmpty() && !description.getText().toString().isEmpty()){
                    saveTask(view);
                }else {
                    Snackbar.make(view,"Поля не могут быть пустыми", Snackbar.LENGTH_SHORT);
                }

            }
        });

        builder.setView(view);
        alertDialog = builder.create();//создание диалогового окна
        alertDialog.show();

    }

    private void saveTask(View view) {
        Task task = new Task();

        //получение текста
        String getTitle = title.getText().toString().trim();
        String getDesc = description.getText().toString().trim();

        //ввод текста в задачу
        task.setTitle(getTitle);
        task.setDescription(getDesc);

        db.addTask(task);



        Snackbar.make(view, "Задача добавлена в базу",Snackbar.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();

                startActivity(new Intent(ListTaskActivity.this, ListTaskActivity.class));
                //отключение текущего представления recycler и перезапуск его
                finish();
            }
        }, 1000);

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
}