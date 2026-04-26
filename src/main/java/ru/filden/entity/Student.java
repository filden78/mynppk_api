package ru.filden.entity;

public class Student {
    public Student(int id, int user_id, String name, int group_h, boolean is_duty, int count_duty) {
        this.id = id;
        this.user_id = user_id;
        this.name = name;
        this.group_h = group_h;
        this.is_duty = is_duty;
        this.count_duty = count_duty;
    }

    public Student() {

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

    public int getGroupId() {
        return group_h;
    }

    public void setGroupId(int group_h) {
        this.group_h = group_h;
    }

    public boolean isDuty() {
        return is_duty;
    }

    public void setDuty(boolean is_duty) {
        this.is_duty = is_duty;
    }

    public Integer getCountDuty() {
        return count_duty;
    }

    public void setCountDuty(int count_duty) {
        this.count_duty = count_duty;
    }

    private int id;
    private int user_id;
    private String name;
    private int group_h;
    private boolean is_duty;
    private Integer count_duty;

}
