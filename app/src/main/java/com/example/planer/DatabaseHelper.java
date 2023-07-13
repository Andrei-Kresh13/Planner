package com.example.planer;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final Context context;

    public DatabaseHelper(@Nullable Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION    );
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TODO_TABLE = "CREATE TABLE " + Constants.TABLE_NAME + "("
                + Constants.ID + " INTEGER PRIMARY KEY,"
                + Constants.TASK_TITLE + " TEXT,"
                + Constants.TASK_DESCRIPTION + " TEXT,"
                + Constants.TASK_DATE_ADDED + " LONG);";

        sqLiteDatabase.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);

        onCreate(sqLiteDatabase);

    }


    public void addTask(Task task){
        SQLiteDatabase db = this.getWritableDatabase();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();

                values.put(Constants.TASK_TITLE, task.getTitle());
                values.put(Constants.TASK_DESCRIPTION, task.getDescription());

                values.put(Constants.TASK_DATE_ADDED, java.lang.System.currentTimeMillis());

                Log.d("DATE", "Дата " + values.get(Constants.TASK_DATE_ADDED));

                db.insert(Constants.TABLE_NAME,null,values);

                Log.d("DBHandler", "addedItem");
            }
        });
        thread.start();
    }


    @SuppressLint("Range")
    public Task getTask(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME, new String []{
                        Constants.ID,
                        Constants.TASK_TITLE,
                        Constants.TASK_DESCRIPTION,
                        Constants.TASK_DATE_ADDED},
                Constants.ID + "=?",
                new String[]{String.valueOf(id)},null,null,null,null);

        if (cursor !=null){
            cursor.moveToFirst();
        }

        Task task = new Task();
        task.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.ID))));
        task.setTitle(cursor.getString(cursor.getColumnIndex(Constants.TASK_TITLE)));
        task.setDescription(cursor.getString(cursor.getColumnIndex(Constants.TASK_DESCRIPTION)));


        DateFormat dateFormat = DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.TASK_DATE_ADDED)))
                .getTime());

        task.setDateTaskAdded(formattedDate);


        return task;

    }
    //Получение всех задач

    @SuppressLint("Range")
    public List<Task> getAllTasks(){

        SQLiteDatabase db = this.getReadableDatabase();
        List<Task> taskList = new ArrayList<>();

        Cursor cursor = db.query(Constants.TABLE_NAME, new String[]{
                        Constants.ID,
                        Constants.TASK_TITLE,
                        Constants.TASK_DESCRIPTION,
                        Constants.TASK_DATE_ADDED},
                null,null,null,null, Constants.TASK_DATE_ADDED + " DESC");

        if (cursor.moveToFirst()){

            do {
                Task task = new Task();

                task.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.ID))));
                task.setTitle(cursor.getString(cursor.getColumnIndex(Constants.TASK_TITLE)));
                task.setDescription(cursor.getString(cursor.getColumnIndex(Constants.TASK_DESCRIPTION)));

                //установка текущей даты
                DateFormat dateFormat = DateFormat.getDateInstance();
                String formattedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.TASK_DATE_ADDED)))
                        .getTime());

                task.setDateTaskAdded(formattedDate);

                taskList.add(task);
            }while (cursor.moveToNext());
        }
        return taskList;
    }

    //Обновление задачи
    public int updateTask(Task task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.TASK_TITLE, task.getTitle());
        values.put(Constants.TASK_DESCRIPTION, task.getDescription());
        values.put(Constants.TASK_DATE_ADDED, task.getDateTaskAdded());


        return  db.update(Constants.TABLE_NAME,values,Constants.ID + "=?",new String[]{String.valueOf(task.getId())});
    }

    //Удаление задачи
    public void deleteTask(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                db.delete(Constants.TABLE_NAME, Constants.ID + "=?", new String[]{String.valueOf(id)});
                db.close();
            }
        });
        thread.start();

    }

    //подсчет количества задач
    public int getCount(){

        SQLiteDatabase db = this.getReadableDatabase();

        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME;

        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }

}
