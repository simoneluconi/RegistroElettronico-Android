package com.sharpdroid.registroelettronico.database.room.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.sharpdroid.registroelettronico.database.entities.Profile;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@Dao
public interface ProfileDao {

    @Query("SELECT * FROM PROFILE")
    List<Profile> getProfilesSync();

    @Query("SELECT * FROM PROFILE WHERE ID = :profile LIMIT 1")
    Profile getProfile(long profile);

    @Insert
    void insert(@NotNull Profile profile);

    @Query("SELECT * FROM PROFILE LIMIT 1")
    Profile getRandomProfile();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(@NotNull Profile profile);

    @Query("DELETE FROM PROFILE WHERE ID = :profile")
    void delete(long profile);
}
