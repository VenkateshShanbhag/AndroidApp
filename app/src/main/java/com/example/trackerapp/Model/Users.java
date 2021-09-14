//package com.example.trackerapp.Model;
//
//import android.text.Editable;
//
//import io.realm.RealmObject;
//import io.realm.annotations.PrimaryKey;
//import io.realm.annotations.Required;
//
//import org.bson.types.ObjectId;
//
//import java.util.Date;
//
//public class Users extends RealmObject {
//    @PrimaryKey
//    @Required
//    private String _id;
//    private String city_of_purchase;
//    private String owner_name;
//    private String vehicle_reg_date;
//    private String partition_key;
//    // Standard getters & setters
//    public String get_id() { return _id; }
//    public void set_id(String _id) { this._id = _id; }
//    public String getCity_of_purchase() { return city_of_purchase; }
//    public void setCity_of_purchase(String city_of_purchase) { this.city_of_purchase = city_of_purchase; }
//    public String getOwner_name() { return owner_name; }
//    public void setOwner_name(String owner_name) { this.owner_name = owner_name; }
//    public String getVehicle_reg_date() { return vehicle_reg_date; }
//    public void setVehicle_reg_date(String vehicle_reg_date) { this.vehicle_reg_date = vehicle_reg_date; }
//    public String getPartition_key() { return partition_key; }
//    public void setPartition_key(String partition_key) { this.partition_key = partition_key; }
//
//    public Users() {}
//    @Override
//    public String toString() {
//        return owner_name + " - " + _id;
//    }
//}
