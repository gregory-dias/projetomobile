package com.example.apptodolistulife;

public class ModelTask {
    String id, task, uid;
    long timestamp;

    // constructor vazio = firebase
    public ModelTask(){

    }

    // contructor parametrizado
    public ModelTask(String id, String task, String uid, long timestamp) {
        this.id = id;
        this.task = task;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    // ================= getters / setters =================


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
