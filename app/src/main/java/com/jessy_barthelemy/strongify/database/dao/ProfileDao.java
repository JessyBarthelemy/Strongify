package com.jessy_barthelemy.strongify.database.dao;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import com.jessy_barthelemy.strongify.database.entity.Profile;

@Dao
public interface ProfileDao extends BaseDao<Profile>{
    @Query("SELECT * FROM Profile LIMIT 1")
    LiveData<Profile> getProfile();
}
