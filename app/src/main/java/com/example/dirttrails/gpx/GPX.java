package com.example.dirttrails.gpx;

import java.util.ArrayList;
import java.util.List;

public class GPX {
    private final String mVersion;
    private final String mCreator;
    private final Metadata mMetadata;
    private final List<WayPoint> mWayPoints;
    private final List<Route> mRoutes;
    private final List<Track> mTracks;

    private GPX(Builder builder) {
        mVersion = builder.mVersion;
        mCreator = builder.mCreator;
        mMetadata = builder.mMetadata;
        mWayPoints = new ArrayList<>(builder.mWayPoints);
        mRoutes = new ArrayList<>(builder.mRoutes);
        mTracks = new ArrayList<>(builder.mTracks);
    }

    //all getters and no setters maintains immutability
    public String getVersion() { return mVersion; }

    public String getCreator() { return mCreator; }

    public Metadata getMetadata() {
        return mMetadata;
    }

    public List<WayPoint> getWayPoints() {
        return mWayPoints;
    }

    public List<Route> getRoutes() {
        return mRoutes;
    }

    public List<Track> getTracks() {
        return mTracks;
    }

    public static class Builder {
        private List<WayPoint> mWayPoints;
        private List<Route> mRoutes;
        private List<Track> mTracks;
        private String mVersion;
        private String mCreator;
        private Metadata mMetadata;

        public Builder setTracks(List<Track> tracks) {
            mTracks = tracks;
            return this;
        }

        public Builder setWayPoints(List<WayPoint> wayPoints) {
            mWayPoints = wayPoints;
            return this;
        }

        public Builder setRoutes(List<Route> routes) {
            this.mRoutes = routes;
            return this;
        }

        public Builder setVersion(String version) {
            mVersion = version;
            return this;
        }

        public Builder setCreator(String creator) {
            mCreator = creator;
            return this;
        }

        public Builder setMetadata(Metadata mMetadata) {
            this.mMetadata = mMetadata;
            return this;
        }

        //Return the constructed GPX object
        public GPX build() {
            return new GPX(this);
        }

    }
}
