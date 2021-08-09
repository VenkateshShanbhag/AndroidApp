package com.example.trackerapp.Model;

import org.bson.types.ObjectId;

public class UserQuery {
    private String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getCity_of_purchase() {
        return city_of_purchase;
    }

    public void setCity_of_purchase(String city_of_purchase) {
        this.city_of_purchase = city_of_purchase;
    }

    public String getPartition_key() {
        return partition_key;
    }

    public void setPartition_key(String partition_key) {
        this.partition_key = partition_key;
    }

    private String owner_name;
    private String city_of_purchase;
    private String partition_key;

    // empty constructor required for MongoDB Data Access POJO codec compatibility
    public UserQuery() {}
    public UserQuery(String _id, String owner_name, String city_of_purchase, String partition_key) {
        this._id = _id;
        this.owner_name = owner_name;
        this.city_of_purchase = city_of_purchase;
        this.partition_key = partition_key;
    }


    @Override
    public String toString() {
        return " [registration number : " + _id + ", Name : " + owner_name + ", City of Purchase : " + city_of_purchase + ", Partition_key : " + partition_key +"]";
    }
}