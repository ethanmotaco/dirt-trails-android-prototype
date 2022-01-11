package com.example.dirttrails.gpx;

public class Point {
    private final Double mLatitude;
    private final Double mLongitude;
    private final Double mElevation;
    private final String mTime;
    private final String mName;
    private final String mCmt;
    private final String mDesc;
    private final Link mLink;
    private final String mSym;
    private final String mType;


    Point(Builder builder) {
        mLatitude = builder.mLatitude;
        mLongitude = builder.mLongitude;
        mElevation = builder.mElevation;
        mTime = builder.mTime;
        mName = builder.mName;
        mCmt = builder.mCmt;
        mDesc = builder.mDesc;
        mLink = builder.mLink;
        mSym = builder.mSym;
        mType = builder.mType;
    }

    /**
     * @return the latitude in degrees
     */
    public Double getLatitude() {
        return mLatitude;
    }

    /**
     * @return the longitude in degrees
     */
    public Double getLongitude() {
        return mLongitude;
    }

    /**
     * @return the elevation in meters
     */
    public Double getElevation() {
        return mElevation;
    }

    public String getTime() {
        return mTime;
    }

    /**
     * @return the point name
     */
    public String getName() {
        return mName;
    }

    public String getCmt() {
        return mCmt;
    }

    /**
     * @return the description
     */
    public String getDesc() {
        return mDesc;
    }

    public Link getLink() {
        return mLink;
    }

    public String getSym() {
        return mSym;
    }

    /**
     * @return the type (category)
     */
    public String getType() {
        return mType;
    }

    public static abstract class Builder {
        private Double mLatitude;
        private Double mLongitude;
        private Double mElevation;
        private String mTime;
        private String mName;
        private String mCmt;
        private String mDesc;
        private Link mLink;
        private String mSym;
        private String mType;

        public Builder setLatitude(Double latitude) {
            mLatitude = latitude;
            return this;
        }

        public Builder setLongitude(Double longitude) {
            mLongitude = longitude;
            return this;
        }

        public Builder setElevation(Double elevation) {
            mElevation = elevation;
            return this;
        }

        public Builder setTime(String time) {
            mTime = time;
            return this;
        }

        public Builder setName(String name) {
            mName = name;
            return this;
        }

        public Builder setCmt(String cmt) {
            mCmt = cmt;
            return this;
        }

        public Builder setDesc(String desc) {
            mDesc = desc;
            return this;
        }

        public Builder setLink(Link link) {
            mLink = link;
            return this;
        }

        public Builder setSym(String sym) {
            mSym = sym;
            return this;
        }

        public Builder setType(String type) {
            mType = type;
            return this;
        }

        public abstract Point build();
    }
}
