package com.example.trackerapp.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import org.bson.types.ObjectId;

public class Tracking extends RealmObject {
    @PrimaryKey
    @Required
    private ObjectId _id;

    private String city_of_purchase;

    private Double lat;

    private Double lon;

    private String partition_key;

    // Standard getters & setters
    public ObjectId get_id() { return _id; }
    public void set_id(ObjectId _id) { this._id = _id; }

    public String getCity_of_purchase() { return city_of_purchase; }
    public void setCity_of_purchase(String city_of_purchase) { this.city_of_purchase = city_of_purchase; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLon() { return lon; }
    public void setLon(Double lon) { this.lon = lon; }

    public String getPartition_key() { return partition_key; }
    public void setPartition_key(String partition_key) { this.partition_key = partition_key; }
}
