package com.example.dirttrails.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.dirttrails.R;
import com.example.dirttrails.SQLite.AppRoomDatabase;
import com.example.dirttrails.SQLite.GPXEntity;
import com.example.dirttrails.SQLite.TileEntity;
import com.example.dirttrails.gpx.GPX;
import com.example.dirttrails.gpx.GPXParser;
import com.example.dirttrails.gpx.GPXWriter;
import com.example.dirttrails.gpx.Route;
import com.example.dirttrails.gpx.RoutePoint;
import com.example.dirttrails.gpx.Track;
import com.example.dirttrails.gpx.TrackPoint;
import com.example.dirttrails.gpx.TrackSegment;
import com.example.dirttrails.gpx.WayPoint;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, OnMapLongClickListener {

    private static final String TAG = "MainActivity";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int GET_CONTENT_RESULT_CODE = 10;
    public boolean defaultTile = true;
    private GoogleMap mMap;
    private AppRoomDatabase appDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermissions();
        setupMap();
        //Create the database if not already created
        appDB = AppRoomDatabase.getDatabase(this.getApplicationContext());

        //Button to import GPX from file
        Button load = findViewById(R.id.load);
        load.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            //intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, GET_CONTENT_RESULT_CODE);
        });

        //Button to load GPX files from DB
        Button displayGPX = findViewById(R.id.display);
        displayGPX.setOnClickListener(v -> new LoadGPXFromDB().execute());
    }

    //Requesting location permissions
    private void getPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }

    //Initialize Google map
    private void setupMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mainMap);
        mapFragment.getMapAsync(this);

    }

    //Triggers when map is ready to be displayed
    @Override
    public void onMapReady(GoogleMap googleMap) {

        //Declare new map object
        mMap = googleMap;
        LatLng ne = new LatLng(42, -72);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(ne));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(7));
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        //Declare a custom tile provider for the map
        TileProvider tileProvider = (x, y, zoom) -> {

            //Check if there is a tile cached for the given coordinates
            byte[] data = appDB.tileDao().loadTile(zoom, x, y);
            //Return the tile data as a bitmap image if the tile is available
            if (data != null) {
                return new Tile(256,256, data);
            //if the tile is not available and default tile is enabled, load the default tile image from device storage
            } else if(defaultTile) {

                File file = new File("/data/data/com.example.dirttrails/files/no_tile.png");
                try {
                    InputStream is = new FileInputStream(file);
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    while ((bytesRead = is.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                    byte[] data2 = output.toByteArray();
                    return new Tile(256, 256, data2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //if a tile image cannot be found return NO_TILE
                return TileProvider.NO_TILE;
            }
            return null;
        };

        //Create a tile overlay with the previously created tileProvider
        final TileOverlay tileOverlay = mMap.addTileOverlay(new TileOverlayOptions()
                .tileProvider(tileProvider)
                .fadeIn(false)
                .visible(true)
                .zIndex(-1));

        //On map long click listener to toggle defaultTile
        mMap.setOnMapLongClickListener(this);

        //Button to refresh tiles
        final Button refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(v -> tileOverlay.clearTileCache());

        //Button to clear the Tile database cache
        final Button clear = findViewById(R.id.clear);
        clear.setOnClickListener(v -> new ClearDatabase().execute());
    }

    //toggle defaultTile with long click on map
    @Override
    public void onMapLongClick(LatLng point) {

        Log.d(TAG, "onMapLongClick: clicked");
        defaultTile = !defaultTile;
        //debug code
//        int zoom = (int) Math.ceil(mMap.getCameraPosition().zoom);
//        TextView txt = findViewById(R.id.text);
//        txt.setText("" + zoom);
    }

    //Starts an Async Task which downloads tiles from the tile server
    public void getTiles(List<int[]> tileNumbers) {
        new DownloadTilesTask().execute(tileNumbers);
    }

    //Query to clear the tile database cache
    private class ClearDatabase extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... voids) {
            appDB.tileDao().clearTable();
            return null;
        }
    }

    //Asynchronous task runs in background thread to download tiles from tile server
    private class DownloadTilesTask extends AsyncTask<List<int[]>, Integer, Void> {
        //TODO make static?
        protected Void doInBackground(List<int[]>... tileNumbers) {

            //URL to tile provider server
            String tileProvider = "https://a.tile.openstreetmap.de";

            //Iterate through given tile numbers ArrayList
            for (List<int[]> number : tileNumbers) {

                //Iterate through each int array in the ArrayList
                ListIterator<int[]> iterator = number.listIterator();
                int loopCount = 0;
                int totalTiles = number.size();

                while (iterator.hasNext()) {
                    loopCount++;

                    int[] tileNumber = (int[]) iterator.next();
                    int zoom = tileNumber[0];
                    int x = tileNumber[1];
                    int y = tileNumber[2];

                    //Check if tile is already in DB
                    if (appDB.tileDao().loadTile(zoom, x, y) == null) {
                        String s = String.format("%s/%d/%d/%d.png",
                                tileProvider, zoom, x, y);
                        //Try downloading the tile from the tile server
                        try {
                            URL url = new URL(s);
                            InputStream is = (InputStream) url.getContent();
                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            ByteArrayOutputStream output = new ByteArrayOutputStream();
                            while ((bytesRead = is.read(buffer)) != -1) {
                                output.write(buffer, 0, bytesRead);
                            }
                            byte[] data = output.toByteArray();
                            appDB.tileDao().insertTiles(new TileEntity(data, tileNumber[0], tileNumber[1], tileNumber[2]));
                            Log.d(TAG, "doInBackground: success");

                            //publish progress for progressBar
                            publishProgress((int) ((loopCount / (float) totalTiles) * 100));

                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d(TAG, "doInBackground: fail");
                            publishProgress((int) ((loopCount / (float) totalTiles) * 100));
                        }
                    }
                }
            }
            return null;
        }

        //Code to retrieve progress from background task
        @Override
        protected void onProgressUpdate(Integer... progress) {
            setProgressPercent(progress[0]);
            Log.d(TAG, "onProgressUpdate: " + progress[0]);
        }

        //When background task is complete, reset progressbar
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setProgressPercent(0);
        }
    }

    //Code to set progressBar percentage
    public void setProgressPercent(int progress) {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(progress);
    }

    //Math to get tile number from GPS coordinates.
    public int[] getTileNumber (final int zoom, final double lat, final double lon) {
        int xtile = (int)Math.floor( (lon + 180) / 360 * (1<<zoom) ) ;
        int ytile = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<zoom) ) ;
        if (xtile < 0)
            xtile=0;
        if (xtile >= (1<<zoom))
            xtile=((1<<zoom)-1);
        if (ytile < 0)
            ytile=0;
        if (ytile >= (1<<zoom))
            ytile=((1<<zoom)-1);
        int[] result = new int[3];
        result[0] = zoom;
        result[1] = xtile;
        result[2] = ytile;
        return result;
    }

    //Asynchronous task runs in background thread to retrieve GPX objects from DB
    private class LoadGPXFromDB extends AsyncTask<Void, Void, List<GPX>> {
        protected List<GPX> doInBackground(Void... voids) {

            //Parse the GPX string from DB into GPX onbject
            GPXParser parser = new GPXParser();
            GPXEntity[] gpxEntities = appDB.gpxDao().loadAllGPX();
            List<GPX> gpxList = new ArrayList<>();
            for (GPXEntity gpxEntity : gpxEntities) {
                String data = gpxEntity.getGpxData();
                InputStream is = new ByteArrayInputStream(data.getBytes());
                try {
                    GPX gpx = parser.read(is);
                    gpxList.add(gpx);
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
            }
            //return GPX objects from database
            return gpxList;
        }

        //Display each GPX object on the map.
        protected void onPostExecute(List<GPX> gpxList) {
            for (GPX gpx : gpxList) { //TODO if GPX is already displayed, it is not necessary to redraw on map
                displayGPXOnMap(gpx);
            }
        }
    }

    //Method to create polylines and waypoints on the map from a GPX object
    public void displayGPXOnMap(GPX gpx) {

        List<int[]> tileZXYList = new ArrayList<>();
        Set<String> tileStrings = new HashSet<>();

        //Get list of Route objects from GPX object
        List<Route> routes = gpx.getRoutes();
        //Iterate through list of routes, create new polyline object for each route
        for (Route route : routes) {
            PolylineOptions polylineOptions = new PolylineOptions();

            //Get list of route points from Route object
            List<RoutePoint> routePoints = route.getRoutePoints();
            //Iterate through list of route points, create new LatLng object for each point
            for (RoutePoint routePoint : routePoints) {
                double lat = routePoint.getLatitude();
                double lon = routePoint.getLongitude();
                LatLng latLng = new LatLng(lat, lon);
                //Add the new LatLng to the polyline object
                polylineOptions.add(latLng);

                //Add necessary tiles to list for future caching
                //zoom var is zoom level
                for (int zoom = 10; zoom <= 15; zoom++) {
                    //getTileNumber method converts gps coordinates to tile number
                    int[] tileZXY = getTileNumber(zoom, lat, lon);
                    //create a string out of the tile numbers
                    String tileString = tileZXY[0] + "," + tileZXY[1] + "," + tileZXY[2];
                    //add string to string set
                    tileStrings.add(tileString);
                }
            }
            //add the route polyline to the map
            //TODO define color, thickness, and other attributes of Route
            mMap.addPolyline(polylineOptions);
        }

        //Get list of Track objects from GPX object
        List<Track> tracks = gpx.getTracks();
        //Iterate through list of tracks
        for (Track track : tracks) {

            //Get list of track segments from Track object
            List<TrackSegment> trackSegments = track.getTrackSegments();
            //Iterate through list of track points, create new polyline object for each track segment

            for (TrackSegment trackSegment : trackSegments) {

                PolylineOptions polylineOptions = new PolylineOptions();
                //Get list of track segments from TrackSegment object
                List<TrackPoint> trackPoints = trackSegment.getTrackPoints();
                //Iterate through list of track points, create new LatLng object for each point

                for (TrackPoint trackPoint : trackPoints) {
                    double lat = trackPoint.getLatitude();
                    double lon = trackPoint.getLongitude();
                    LatLng latLng = new LatLng(lat, lon);
                    //Add the new LatLng to the polyline object
                    polylineOptions.add(latLng);

                    //Add necessary tiles to list for future caching
                    //zoom var is zoom level
                    for (int zoom = 10; zoom <= 13; zoom++) {
                        //getTileNumber method converts gps coordinates to tile number
                        int[] tileZXY = getTileNumber(zoom, lat, lon);
                        //create a string out of the tile numbers
                        String tileString = tileZXY[0] + "," + tileZXY[1] + "," + tileZXY[2];
                        //add string to string set
                        tileStrings.add(tileString);
                    }
                }
                //add the track segment polyline to the map
                //TODO define color, thickness, and other attributes of Track Segment
                mMap.addPolyline(polylineOptions);
            }
        }

        //Get list of Waypoint objects from GPX object
        List<WayPoint> wayPoints = gpx.getWayPoints();
        //Iterate through list of waypoints, create new LatLng object for each point
        for (WayPoint wayPoint : wayPoints) {
            double lat = wayPoint.getLatitude();
            double lon = wayPoint.getLongitude();
            LatLng latLng = new LatLng(lat, lon);
            //add the waypoint markers to the map
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    //title and snippet are Name and Description vars of Marker object
                    .title(wayPoint.getName())
                    .snippet(wayPoint.getDesc()));
            //TODO add the rest of the waypoint attributes like icon, color, and metadata

            //Add necessary tiles to list for future caching
            //zoom var is zoom level
            for (int zoom = 0; zoom <= 20; zoom++) {
                //getTileNumber method converts gps coordinates to tile number
                int[] tileZXY = getTileNumber(zoom, lat, lon);
                //create a string out of the tile numbers
                String tileString = tileZXY[0] + "," + tileZXY[1] + "," + tileZXY[2];
                //add string to string set
                tileStrings.add(tileString);
            }
        }

        //Create separate list for zoom levels, latitutes, and longitudes by iterating through tileStrings list created earlier.
        for (String tileString : tileStrings) {
            int zoom = Integer.parseInt(tileString.split(",", 3)[0]);
            int lat = Integer.parseInt(tileString.split(",", 3)[1]);
            int lon = Integer.parseInt(tileString.split(",", 3)[2]);
            int[] result = {zoom, lat, lon};
            //Create the list of necessary tiles to be downloaded.
            tileZXYList.add(result);
        }

        Log.d(TAG, "displayGPXOnMap: " + tileZXYList);

        //Download the tiles of given coordinates from the tile server
        //TODO give user a choice to download the tiles instead of doing it automatically
        getTiles(tileZXYList);
     }

    //Get the data from file selected in file explorer activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //TODO Fix no activity available
        //TODO make MIME GPX only
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;

        if (requestCode == GET_CONTENT_RESULT_CODE) {
            Log.d(TAG, "onActivityResult: case 10");
            if (resultCode == RESULT_OK) {

                Uri uri = data.getData();

                if (uri != null) {
                    try {
                        //Get the content resolver instance for this context and use it to get a ParcelFileDescriptor for the file.
                        InputStream is = getContentResolver().openInputStream(uri);
                        //Parse input stream into GPX object
                        loadGPX(is);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.e("MainActivity", "File not found.");
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                        Log.e("MainActivity", "XmlPullParserException");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("MainActivity", "IOException");
                    }
                } else {
                    Toast.makeText(this, "No Data", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "No Result", Toast.LENGTH_LONG).show();
            }
        }
    }

    //Creates a GPX object from given input stream
    public void loadGPX(InputStream in) throws IOException, XmlPullParserException {
        GPX gpx;
        GPXParser parser = new GPXParser();
        gpx = parser.read(in);
        //Save new GPX file to database
        new SaveGPXToDB().execute(gpx);
    }

    //Asynchronous task runs in background thread to Save GPX data to DB
    private class SaveGPXToDB extends AsyncTask<GPX, Void, Void> {
        protected Void doInBackground(GPX... gpx) {

            //TODO iterate through gpx objects
            //Write GPX object to string and save to DB
            String data = GPXWriter.write(gpx[0]);
            String name = gpx[0].getMetadata().getName();
            appDB.gpxDao().insertGPX(new GPXEntity(data, name));
            return null;
        }
    }
}

