package com.example.dirttrails.SQLite;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface GPXDao {

    // Insert
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertGPX(GPXEntity... gpxEntities);

    // Delete
    @Query("DELETE FROM gpx")
    public void clearTable();

    @Delete
    public void deleteGPX(GPXEntity... gpxEntities);

     // Query
    @Query("SELECT * FROM gpx")
    public GPXEntity[] loadAllGPX();

//    @Query("Select data FROM tiles WHERE z = :z AND x = :x AND y = :y")
//    byte[] loadTile(int z, int x, int y);
}
