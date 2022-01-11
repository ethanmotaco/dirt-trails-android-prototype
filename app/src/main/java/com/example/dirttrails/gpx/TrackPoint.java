package com.example.dirttrails.gpx;

public class TrackPoint extends Point {

    private TrackPoint(Builder builder) {
        super(builder);
    }

    public static class Builder extends Point.Builder {

        @Override
        public TrackPoint build() {
            return new TrackPoint(this);
        }
    }

}
