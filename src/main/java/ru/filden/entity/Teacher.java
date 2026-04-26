package ru.filden.entity;

public class Teacher {
    public Teacher(int id, int user_id, String name) {
        this.id = id;
        this.user_id = user_id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private int id;
    private int user_id;
    private String name;

}
