package com.example.fitnessappactions.Model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FitActivityDao {

    /**
     * @param max define a max result count.
     * @return a list of FitActivity items ordered by date
     */
    @Query("SELECT * FROM fitness_activities ORDER BY date DESC LIMIT :max")
    LiveData<List<FitActivity>> getAll(int max);

    /**
     * @param max define a max result count.
     * @return a list of FitActivity items ordered by date
     */
    @Query("SELECT * FROM fitness_activities WHERE type == :type ORDER BY date DESC LIMIT :max")
    LiveData<List<FitActivity>>   getAllOfType(FitActivity.Type type , int max);

    /**
     * @return a FitStats of the user
     */
    @Query("SELECT COUNT(*) as totalCount, SUM(distanceMeters) as totalDistanceMeters, SUM(durationMs) as totalDurationMs FROM fitness_activities")
    LiveData<FitStats> getStats();

    /**
     * Get an activity by ID
     */
    @Query("SELECT * FROM fitness_activities WHERE id == :id")
    LiveData<FitActivity> getById(String id);

    /**
     * Insert a new FitActivity in the DB
     */
    @Insert
   void insert( FitActivity fitActivity);

    /**
     * Delete the given FitActivity from DB
     */
    @Delete
    void delete( FitActivity fitActivity);
}
