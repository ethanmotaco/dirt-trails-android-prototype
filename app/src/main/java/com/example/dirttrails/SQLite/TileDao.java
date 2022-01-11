package com.example.dirttrails.SQLite;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface TileDao {

    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertTiles(TileEntity... tileEntities);

    // Delete
    @Query("DELETE FROM tiles")
    public void clearTable();

    @Delete
    public void deleteTiles(TileEntity... tileEntities);

    // Query
    @Query("SELECT * FROM tiles")
    public TileEntity[] loadAllTiles();

    @Query("Select data FROM tiles WHERE z = :z AND x = :x AND y = :y")
    byte[] loadTile(int z, int x, int y);














//    //Queries
//
//    @Query("SELECT chartId FROM chart_info ORDER BY :order")
//    LiveData<List<Long>> loadAllChartIDs(String order);
//
//    @Query("SELECT chartName FROM chart_info WHERE chartId = :id")
//    String loadChartName(int id);
//
//    @Query("SELECT creator FROM chart_info WHERE chartId = :id")
//    String loadCreator(int id);
//
//    @Query("SELECT creationDate FROM chart_info WHERE chartId = :id")
//    String loadCreationDate(int id);
//
//
//    @Query("SELECT sequenceNum FROM chart WHERE chartId = :id")
//    List<Integer> loadSequenceNum(int id);
//
//    @Query("SELECT instruction FROM chart WHERE chartId = :id")
//    List<String> loadInstruction(int id);
//
//    @Query("SELECT waypointInfo FROM chart WHERE chartId = :id")
//    List<String> loadWaypointInfo(int id);
//
//    @Query("SELECT tulipDiagram FROM chart WHERE chartId = :id")
//    List<String> loadTulipDiagram(int id);
//
//    @Query("SELECT bearing FROM chart WHERE chartId = :id")
//    List<Integer> loadBearing(int id);
//
//    @Query("SELECT longitude FROM chart WHERE chartId = :id")
//    List<Double> loadLongitude(int id);
//
//    @Query("SELECT latitude FROM chart WHERE chartId = :id")
//    List<Double> loadLatitude(int id);
//
//    //Delete
//
//    //    @Query("DELETE FROM chart_info WHERE chartId = :id")
////    public void deleteChartById(int id);
////
//    @Query("DELETE FROM chart_info")
//    void deleteAllCharts();
//
//    @Delete
//    void deleteInfo(ChartInfoEntity chartChartInfoEntity);
//
//    //Insert
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    long insertInfo(ChartInfoEntity chartChartInfoEntity);
//
//    @Insert
//    void insertChartRow(ChartEntity chartEntity);
}
