package com.example.trackerapp.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import java.util.Date;
import org.bson.types.ObjectId;

public class Tracking extends RealmObject {
    @PrimaryKey
    @Required
    private ObjectId _id;

    private Date Timestamp;

    private Double lat;

    private Double lon;

    private String partition_key;

    private String reg_num;

    private String owner;

    private String city;

    // Standard getters & setters
    public ObjectId get_id() { return _id; }
    public void set_id(ObjectId _id) { this._id = _id; }

    public Date getTimestamp() { return Timestamp; }
    public void setTimestamp(Date Timestamp) { this.Timestamp = Timestamp; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLon() { return lon; }
    public void setLon(Double lon) { this.lon = lon; }

    public String getPartition_key() { return partition_key; }
    public void setPartition_key(String partition_key) { this.partition_key = partition_key; }

    public String getReg_num() { return reg_num; }
    public void setReg_num(String reg_num) { this.reg_num = reg_num; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public Tracking() {}
    @Override
    public String toString() {
        return owner + " - " + reg_num;
    }
}
