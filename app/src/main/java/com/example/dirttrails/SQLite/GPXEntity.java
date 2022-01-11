package com.example.dirttrails.SQLite;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "gpx")
public class GPXEntity {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public Long id;

    @NonNull
    public String gpxData;

    public String gpxFileName;

    public String getGpxData() {
        return gpxData;
    }

    public GPXEntity (String gpxData,
                       String gpxFileName) {
        this.gpxData = gpxData;
        this.gpxFileName = gpxFileName;
    }

}
