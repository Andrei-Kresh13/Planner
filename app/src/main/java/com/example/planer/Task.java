package com.example.planer;

public class Task {
    private int id;
    private String title;
    private String description;
    private String dateTaskAdded;
    
    public Task() {
    }

    //этот конструктор позволяет нам передавать значение без идентификатора
    public Task(String title, String description, String dateTaskAdded) {
        this.title = title;
        this.description = description;
        this.dateTaskAdded = dateTaskAdded;
    }

    public Task(int id, String title, String description, String dateTaskAdded) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateTaskAdded = dateTaskAdded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateTaskAdded() {
        return dateTaskAdded;
    }

    public void setDateTaskAdded(String dateTaskAdded) {
        this.dateTaskAdded = dateTaskAdded;
    }
}
