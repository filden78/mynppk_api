package ru.filden.entity;

public class TeacherGroup {
    private int id;
    private int teacher_id;
    private int group_id;


    public TeacherGroup(int id, int teacher_id, int group_id) {
        this.id = id;
        this.teacher_id = teacher_id;
        this.group_id = group_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTeacherId() {
        return teacher_id;
    }

    public void setTeacherId(int teacher_id) {
        this.teacher_id = teacher_id;
    }

    public int getGroupId() {
        return group_id;
    }

    public void setGroupId(int group_id) {
        this.group_id = group_id;
    }
}
