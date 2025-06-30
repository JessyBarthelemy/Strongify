package com.jessy_barthelemy.strongify.database.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

@Dao
public interface BaseDao<T> {
    @Insert
    Long insert(T entity);

    @Delete
    void delete(T entity);

    @Update
    void update(T entity);
}
