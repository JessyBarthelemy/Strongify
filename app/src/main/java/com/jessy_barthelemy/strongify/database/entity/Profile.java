package com.jessy_barthelemy.strongify.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Profile implements Serializable {

    public static int GOAL_MASS = 0;

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;

    public int size;

    public int goal;

    public int age;
}