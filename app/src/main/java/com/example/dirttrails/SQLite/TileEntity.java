package com.example.dirttrails.SQLite;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;

@Entity(tableName = "tiles",
        primaryKeys = {"data", "z", "x", "y"},
        indices = {@Index(value = {"data", "z", "x", "y"},
        unique = true)})
public class TileEntity {

    @NonNull
    public byte[] data;

    @NonNull
    public int z;

    @NonNull
    public int x;

    @NonNull
    public int y;

    public TileEntity (byte[] data,
                       int z,
                       int x,
                       int y) {
        this.data = data;
        this.z = z;
        this.x = x;
        this.y = y;
    }
}
