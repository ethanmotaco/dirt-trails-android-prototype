package com.example.dirttrails.gpx;

import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class GPXWriter {

    static private final String TAG_GPX = "gpx";
    static private final String TAG_VERSION = "version";
    static private final String TAG_CREATOR = "creator";
    static private final String TAG_METADATA = "metadata";
    static private final String TAG_TRACK = "trk";
    static private final String TAG_SEGMENT = "trkseg";
    static private final String TAG_TRACK_POINT = "trkpt";
    static private final String TAG_LAT = "lat";
    static private final String TAG_LON = "lon";
    static private final String TAG_ELEVATION = "ele";
    static private final String TAG_TIME = "time";
    static private final String TAG_WAY_POINT = "wpt";
    static private final String TAG_ROUTE = "rte";
    static private final String TAG_ROUTE_POINT = "rtept";
    static private final String TAG_NAME = "name";
    static private final String TAG_DESC = "desc";
    static private final String TAG_CMT = "cmt";
    static private final String TAG_SRC = "src";
    static private final String TAG_LINK = "link";
    static private final String TAG_NUMBER = "number";
    static private final String TAG_TYPE = "type";
    static private final String TAG_TEXT = "text";
    static private final String TAG_AUTHOR = "author";
    static private final String TAG_COPYRIGHT = "copyright";
    static private final String TAG_KEYWORDS = "keywords";
    static private final String TAG_BOUNDS = "bounds";
    static private final String TAG_MIN_LAT = "minlat";
    static private final String TAG_MIN_LON = "minlon";
    static private final String TAG_MAX_LAT = "maxlat";
    static private final String TAG_MAX_LON = "maxlon";
    static private final String TAG_HREF = "href";
    static private final String TAG_YEAR = "year";
    static private final String TAG_LICENSE = "license";
    static private final String TAG_EMAIL = "email";
    static private final String TAG_ID = "id";
    static private final String TAG_DOMAIN = "domain";
    static private final String TAG_SYM = "sym";

    static private final String defaultNamespace = "http://www.topografix.com/GPX/1/1";

    public static String write(GPX gpx) {
        String mVersion = gpx.getVersion();
        String mCreator = gpx.getCreator();
        Metadata mMetadata = gpx.getMetadata();
        List<WayPoint> mWayPoints = gpx.getWayPoints();
        List<Route> mRoutes = gpx.getRoutes();
        List<Track> mTracks  = gpx.getTracks();

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", null);
            serializer.startTag(null, TAG_GPX);
            serializer.attribute(null, TAG_CREATOR, mCreator);
            serializer.attribute(null, TAG_VERSION, mVersion);
            serializer.attribute(null, "xmlns", defaultNamespace);
//            serializer.attribute(null, TAG_VERSION, mVersion);

                //Metadata
            if (mMetadata != null) {
                serializer.startTag(null, TAG_METADATA);

                //Name
                if (mMetadata.getName() != null) {
                    serializer.startTag(null, TAG_NAME);
                    serializer.text(mMetadata.getName());
                    serializer.endTag(null, TAG_NAME);
                }

                //Description
                if (mMetadata.getDesc() != null) {
                    serializer.startTag(null, TAG_DESC);
                    serializer.text(mMetadata.getDesc());
                    serializer.endTag(null, TAG_DESC);
                }

                //Author
                if (mMetadata.getAuthor() != null) {
                    serializer.startTag(null, TAG_AUTHOR);
                    serializer.attribute(null, TAG_HREF, mMetadata.getAuthor().getLink().getHref());
                    if (mMetadata.getAuthor() != null) {
                        serializer.startTag(null, TAG_NAME);
                        serializer.text(mMetadata.getAuthor().getName());
                        serializer.endTag(null, TAG_NAME);
                    }
                    if (mMetadata.getAuthor().getEmail() != null) {
                        serializer.startTag(null, TAG_EMAIL);
                        serializer.startTag(null, TAG_ID);
                        serializer.text(mMetadata.getAuthor().getEmail().getId());
                        serializer.endTag(null, TAG_ID);
                        serializer.startTag(null, TAG_DOMAIN);
                        serializer.text(mMetadata.getAuthor().getEmail().getDomain());
                        serializer.endTag(null, TAG_DOMAIN);
                        serializer.endTag(null, TAG_EMAIL);
                    }
                    if (mMetadata.getAuthor().getLink() != null) {
                        serializer.startTag(null, TAG_LINK);

                        if (mMetadata.getAuthor().getLink().getText() != null) {
                            serializer.startTag(null, TAG_TEXT);
                            serializer.text(mMetadata.getAuthor().getLink().getText());
                            serializer.endTag(null, TAG_TEXT);
                        }
                        if (mMetadata.getAuthor().getLink().getType() != null) {
                            serializer.startTag(null, TAG_TYPE);
                            serializer.text(mMetadata.getAuthor().getLink().getType());
                            serializer.endTag(null, TAG_TYPE);
                        }

                        serializer.endTag(null, TAG_LINK);
                    }
                    serializer.endTag(null, TAG_AUTHOR);
                }

                //Copyright
                if (mMetadata.getCopyright() != null) {
                    serializer.startTag(null, TAG_COPYRIGHT);
                    serializer.attribute(null, TAG_AUTHOR, mMetadata.getCopyright().getAuthor());
                    if (mMetadata.getCopyright().getYear() != null) {
                        serializer.startTag(null, TAG_YEAR);
                        serializer.text(String.valueOf(mMetadata.getCopyright().getYear()));
                        serializer.endTag(null, TAG_YEAR);
                    }
                    if (mMetadata.getCopyright().getLicense() != null) {
                        serializer.startTag(null, TAG_LICENSE);
                        serializer.text(mMetadata.getCopyright().getLicense());
                        serializer.endTag(null, TAG_LICENSE);
                    }

                    serializer.endTag(null, TAG_COPYRIGHT);
                }

                //Link
                if (mMetadata.getLink() != null) {
                    serializer.startTag(null, TAG_LINK);
                    serializer.attribute(null, TAG_HREF, mMetadata.getLink().getHref());
                    if (mMetadata.getLink().getText() != null) {
                        serializer.startTag(null, TAG_TEXT);
                        serializer.text(mMetadata.getLink().getText());
                        serializer.endTag(null, TAG_TEXT);
                    }
                    if (mMetadata.getLink().getType() != null) {
                        serializer.startTag(null, TAG_TYPE);
                        serializer.text(mMetadata.getLink().getType());
                        serializer.endTag(null, TAG_TYPE);
                    }

                    serializer.endTag(null, TAG_LINK);
                }

                //Time
                if (mMetadata.getTime() != null) {
                    serializer.startTag(null, TAG_TIME);
                    serializer.text(mMetadata.getTime());
                    serializer.endTag(null, TAG_TIME);
                }
                //Keywords
                if (mMetadata.getKeywords() != null) {
                    serializer.startTag(null, TAG_KEYWORDS);
                    serializer.text(mMetadata.getKeywords());
                    serializer.endTag(null, TAG_KEYWORDS);
                }
                //Bounds
                if (mMetadata.getBounds() != null) {
                    serializer.startTag(null, TAG_BOUNDS);
                    serializer.attribute(null, TAG_MAX_LAT, String.valueOf(mMetadata.getBounds().getMaxLat()));
                    serializer.attribute(null, TAG_MAX_LON, String.valueOf(mMetadata.getBounds().getMaxLon()));
                    serializer.attribute(null, TAG_MIN_LAT, String.valueOf(mMetadata.getBounds().getMinLat()));
                    serializer.attribute(null, TAG_MIN_LON, String.valueOf(mMetadata.getBounds().getMinLon()));
                    serializer.endTag(null, TAG_BOUNDS);
                }

                //Extensions
//                if (mMetadata.getExtensions() != null) {
//                    serializer.startTag(null, TAG_EXTENSIONS);
//                    serializer.attribute(defaultNamespace, null, mMetadata.getExtensions());
//                    serializer.endTag(null, TAG_EXTENSIONS);
//                }

                serializer.endTag(null, TAG_METADATA);
            }

            //Waypoint
            if (mWayPoints != null) {
                for (WayPoint wpt : mWayPoints) {
                    serializer.startTag(null, TAG_WAY_POINT);
                    serializer.attribute(null, TAG_LAT, String.valueOf(wpt.getLatitude()));
                    serializer.attribute(null, TAG_LON, String.valueOf(wpt.getLongitude()));
                    if (wpt.getElevation() != null) {
                        serializer.startTag(null, TAG_ELEVATION);
                        serializer.text(String.valueOf(wpt.getElevation()));
                        serializer.endTag(null, TAG_ELEVATION);
                    }
                    if (wpt.getTime() != null) {
                        serializer.startTag(null, TAG_TIME);
                        serializer.text(wpt.getTime());
                        serializer.endTag(null, TAG_TIME);
                    }
                    if (wpt.getName() != null) {
                        serializer.startTag(null, TAG_NAME);
                        serializer.text(wpt.getName());
                        serializer.endTag(null, TAG_NAME);
                    }
                    if (wpt.getCmt() != null) {
                        serializer.startTag(null, TAG_CMT);
                        serializer.text(wpt.getCmt());
                        serializer.endTag(null, TAG_CMT);
                    }
                    if (wpt.getDesc() != null) {
                        serializer.startTag(null, TAG_DESC);
                        serializer.text(wpt.getDesc());
                        serializer.endTag(null, TAG_DESC);
                    }
                    if (wpt.getLink() != null) {
                        serializer.startTag(null, TAG_LINK);
                        serializer.attribute(null, TAG_HREF, wpt.getLink().getHref());
                        if (wpt.getLink().getText() != null) {
                            serializer.startTag(null, TAG_TEXT);
                            serializer.text(wpt.getLink().getText());
                            serializer.endTag(null, TAG_TEXT);
                        }
                        if (wpt.getLink().getType() != null) {
                            serializer.startTag(null, TAG_TYPE);
                            serializer.text(wpt.getLink().getType());
                            serializer.endTag(null, TAG_TYPE);
                        }

                        serializer.endTag(null, TAG_LINK);
                    }
                    if (wpt.getSym() != null) {
                        serializer.startTag(null, TAG_SYM);
                        serializer.text(wpt.getSym());
                        serializer.endTag(null, TAG_SYM);
                    }
                    if (wpt.getType() != null) {
                        serializer.startTag(null, TAG_TYPE);
                        serializer.text(wpt.getType());
                        serializer.endTag(null, TAG_TYPE);
                    }
                    serializer.endTag(null, TAG_WAY_POINT);
                }
            }

            //Route
            if (mRoutes != null) {
                for (Route rte : mRoutes) {
                    serializer.startTag(null, TAG_ROUTE);

                    if (rte.getRouteName() != null) {
                        serializer.startTag(null, TAG_NAME);
                        serializer.text(rte.getRouteName());
                        serializer.endTag(null, TAG_NAME);
                    }
                    if (rte.getRouteCmt() != null) {
                        serializer.startTag(null, TAG_CMT);
                        serializer.text(rte.getRouteCmt());
                        serializer.endTag(null, TAG_CMT);
                    }
                    if (rte.getRouteDesc() != null) {
                        serializer.startTag(null, TAG_DESC);
                        serializer.text(rte.getRouteDesc());
                        serializer.endTag(null, TAG_DESC);
                    }
                    if (rte.getRouteSrc() != null) {
                        serializer.startTag(null, TAG_SRC);
                        serializer.text(rte.getRouteSrc());
                        serializer.endTag(null, TAG_SRC);
                    }
                    if (rte.getRouteLink() != null) {
                        serializer.startTag(null, TAG_LINK);
                        serializer.attribute(null, TAG_HREF, rte.getRouteLink().getHref());
                        if (rte.getRouteLink().getText() != null) {
                            serializer.startTag(null, TAG_TEXT);
                            serializer.text(rte.getRouteLink().getText());
                            serializer.endTag(null, TAG_TEXT);
                        }
                        if (rte.getRouteLink().getType() != null) {
                            serializer.startTag(null, TAG_TYPE);
                            serializer.text(rte.getRouteLink().getType());
                            serializer.endTag(null, TAG_TYPE);
                        }

                        serializer.endTag(null, TAG_LINK);
                    }
                    if (rte.getRouteNumber() != null) {
                        serializer.startTag(null, TAG_NUMBER);
                        serializer.text(String.valueOf(rte.getRouteNumber()));
                        serializer.endTag(null, TAG_NUMBER);
                    }
                    if (rte.getRouteType() != null) {
                        serializer.startTag(null, TAG_TYPE);
                        serializer.text(rte.getRouteType());
                        serializer.endTag(null, TAG_TYPE);
                    }
//                    if (rte.getRouteExtensions() != null) {
//                        serializer.startTag(null, TAG_EXTENSIONS);
//                        serializer.text(rte.getRouteExtensions());
//                        serializer.endTag(null, TAG_EXTENSIONS);
//                    }
                    if (rte.getRoutePoints() != null) {
                        for (RoutePoint rtpt : rte.getRoutePoints()) {
                            serializer.startTag(null, TAG_ROUTE_POINT);
                            serializer.attribute(null, TAG_LAT, String.valueOf(rtpt.getLatitude()));
                            serializer.attribute(null, TAG_LON, String.valueOf(rtpt.getLongitude()));
                            if (rtpt.getElevation() != null) {
                                serializer.startTag(null, TAG_ELEVATION);
                                serializer.text(String.valueOf(rtpt.getElevation()));
                                serializer.endTag(null, TAG_ELEVATION);
                            }
                            if (rtpt.getTime() != null) {
                                serializer.startTag(null, TAG_TIME);
                                serializer.text(rtpt.getTime());
                                serializer.endTag(null, TAG_TIME);
                            }
                            if (rtpt.getName() != null) {
                                serializer.startTag(null, TAG_NAME);
                                serializer.text(rtpt.getName());
                                serializer.endTag(null, TAG_NAME);
                            }
                            if (rtpt.getCmt() != null) {
                                serializer.startTag(null, TAG_CMT);
                                serializer.text(rtpt.getCmt());
                                serializer.endTag(null, TAG_CMT);
                            }
                            if (rtpt.getDesc() != null) {
                                serializer.startTag(null, TAG_DESC);
                                serializer.text(rtpt.getDesc());
                                serializer.endTag(null, TAG_DESC);
                            }
                            if (rtpt.getLink() != null) {
                                serializer.startTag(null, TAG_LINK);
                                serializer.attribute(null, TAG_HREF, rtpt.getLink().getHref());
                                if (rtpt.getLink().getText() != null) {
                                    serializer.startTag(null, TAG_TEXT);
                                    serializer.text(rtpt.getLink().getText());
                                    serializer.endTag(null, TAG_TEXT);
                                }
                                if (rtpt.getLink().getType() != null) {
                                    serializer.startTag(null, TAG_TYPE);
                                    serializer.text(rtpt.getLink().getType());
                                    serializer.endTag(null, TAG_TYPE);
                                }

                                serializer.endTag(null, TAG_LINK);
                            }
                            if (rtpt.getSym() != null) {
                                serializer.startTag(null, TAG_SYM);
                                serializer.text(rtpt.getSym());
                                serializer.endTag(null, TAG_SYM);
                            }
                            if (rtpt.getType() != null) {
                                serializer.startTag(null, TAG_TYPE);
                                serializer.text(rtpt.getType());
                                serializer.endTag(null, TAG_TYPE);
                            }
                            serializer.endTag(null, TAG_ROUTE_POINT);
                        }
                    }
                    serializer.endTag(null, TAG_ROUTE);
                }

            }

            //Track
            if (mTracks != null) {
                for (Track trk : mTracks) {
                    serializer.startTag(null, TAG_TRACK);

                    if (trk.getTrackName() != null) {
                        serializer.startTag(null, TAG_NAME);
                        serializer.text(trk.getTrackName());
                        serializer.endTag(null, TAG_NAME);
                    }
                    if (trk.getTrackCmt() != null) {
                        serializer.startTag(null, TAG_CMT);
                        serializer.text(trk.getTrackCmt());
                        serializer.endTag(null, TAG_CMT);
                    }
                    if (trk.getTrackDesc() != null) {
                        serializer.startTag(null, TAG_DESC);
                        serializer.text(trk.getTrackDesc());
                        serializer.endTag(null, TAG_DESC);
                    }
                    if (trk.getTrackSrc() != null) {
                        serializer.startTag(null, TAG_SRC);
                        serializer.text(trk.getTrackSrc());
                        serializer.endTag(null, TAG_SRC);
                    }
                    if (trk.getTrackLink() != null) {
                        serializer.startTag(null, TAG_LINK);
                        serializer.attribute(null, TAG_HREF, trk.getTrackLink().getHref());
                        if (trk.getTrackLink().getText() != null) {
                            serializer.startTag(null, TAG_TEXT);
                            serializer.text(trk.getTrackLink().getText());
                            serializer.endTag(null, TAG_TEXT);
                        }
                        if (trk.getTrackLink().getType() != null) {
                            serializer.startTag(null, TAG_TYPE);
                            serializer.text(trk.getTrackLink().getType());
                            serializer.endTag(null, TAG_TYPE);
                        }

                        serializer.endTag(null, TAG_LINK);
                    }
                    if (trk.getTrackNumber() != null) {
                        serializer.startTag(null, TAG_NUMBER);
                        serializer.text(String.valueOf(trk.getTrackNumber()));
                        serializer.endTag(null, TAG_NUMBER);
                    }
                    if (trk.getTrackType() != null) {
                        serializer.startTag(null, TAG_TYPE);
                        serializer.text(trk.getTrackType());
                        serializer.endTag(null, TAG_TYPE);
                    }
//                    if (rte.getRouteExtensions() != null) {
//                        serializer.startTag(null, TAG_EXTENSIONS);
//                        serializer.text(rte.getRouteExtensions());
//                        serializer.endTag(null, TAG_EXTENSIONS);
//                    }
                    if (trk.getTrackSegments() != null) {
                        for (TrackSegment trkseg : trk.getTrackSegments()) {
                            serializer.startTag(null, TAG_SEGMENT);
                            if (trkseg.getTrackPoints() != null) {
                                for (TrackPoint trkpt : trkseg.getTrackPoints()) {
                                    serializer.startTag(null, TAG_TRACK_POINT);
                                    serializer.attribute(null, TAG_LAT, String.valueOf(trkpt.getLatitude()));
                                    serializer.attribute(null, TAG_LON, String.valueOf(trkpt.getLongitude()));
                                    if (trkpt.getElevation() != null) {
                                        serializer.startTag(null, TAG_ELEVATION);
                                        serializer.text(String.valueOf(trkpt.getElevation()));
                                        serializer.endTag(null, TAG_ELEVATION);
                                    }
                                    if (trkpt.getTime() != null) {
                                        serializer.startTag(null, TAG_TIME);
                                        serializer.text(trkpt.getTime());
                                        serializer.endTag(null, TAG_TIME);
                                    }
                                    if (trkpt.getName() != null) {
                                        serializer.startTag(null, TAG_NAME);
                                        serializer.text(trkpt.getName());
                                        serializer.endTag(null, TAG_NAME);
                                    }
                                    if (trkpt.getDesc() != null) {
                                        serializer.startTag(null, TAG_DESC);
                                        serializer.text(trkpt.getDesc());
                                        serializer.endTag(null, TAG_DESC);
                                    }
                                    if (trkpt.getLink() != null) {
                                        serializer.startTag(null, TAG_LINK);
                                        serializer.attribute(null, TAG_HREF, trkpt.getLink().getHref());
                                        if (trkpt.getLink().getText() != null) {
                                            serializer.startTag(null, TAG_TEXT);
                                            serializer.text(trkpt.getLink().getText());
                                            serializer.endTag(null, TAG_TEXT);
                                        }
                                        if (trkpt.getLink().getType() != null) {
                                            serializer.startTag(null, TAG_TYPE);
                                            serializer.text(trkpt.getLink().getType());
                                            serializer.endTag(null, TAG_TYPE);
                                        }

                                        serializer.endTag(null, TAG_LINK);
                                    }
                                    if (trkpt.getSym() != null) {
                                        serializer.startTag(null, TAG_SYM);
                                        serializer.text(trkpt.getSym());
                                        serializer.endTag(null, TAG_SYM);
                                    }
                                    if (trkpt.getType() != null) {
                                        serializer.startTag(null, TAG_TYPE);
                                        serializer.text(trkpt.getType());
                                        serializer.endTag(null, TAG_TYPE);
                                    }
                                    serializer.endTag(null, TAG_TRACK_POINT);
                                }
                            }
//                            if (trkseg.getTrackExtensions() != null) {
//                                serializer.startTag(null, TAG_EXTENSIONS);
//                                serializer.text(trkseg.getTrackExtensions());
//                                serializer.endTag(null, TAG_EXTENSIONS);
//                            }
                            serializer.endTag(null, TAG_SEGMENT);
                        }
                    }
                    serializer.endTag(null, TAG_TRACK);
                }
            }

            //Extensions
//            if (mExtensions != null) {
//            serializer.startTag(null, TAG_EXTENSIONS);
//
//
//
//            serializer.endTag(null, TAG_EXTENSIONS);
//            }
            serializer.endTag(null, TAG_GPX);
            serializer.endDocument();

//            Log.e("Output", "" + xml);

            return writer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
