package ru.filden.entity;

public class Duty_History {
    public Duty_History(int id, int f_student_id, int s_student_id, int group) {
        this.id = id;
        this.f_student_id = f_student_id;
        this.s_student_id = s_student_id;
        this.group = group;
    }

    public Duty_History() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFirstStudentId() {
        return f_student_id;
    }

    public void setFirstStudentId(int f_student_id) {
        this.f_student_id = f_student_id;
    }

    public Integer getSecondStudentId() {
        return s_student_id;
    }

    public void setSecondStudentId(int s_student_id) {
        this.s_student_id = s_student_id;
    }

    public int getGroupId() {
        return group;
    }

    public void setGroupId(int group) {
        this.group = group;
    }

    private int id;
    private int f_student_id;
    private Integer s_student_id;
    private int group;

}
