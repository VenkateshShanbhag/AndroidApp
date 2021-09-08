

# Confluent IIOT tracking App
This Android application targets to utilize and demonstate the power and features of mongodb atlas availabe in latest release 5.0 .
The android application has embeded features such as time series collection, RealmDB, sync, triggers and push notifications.
We have utilised time series collection hosted on mongodb managed atlas cluster as a sink for confluent connector to store the stream data for stimulation of moving vehicles. The stream can be generated using below python script <Add python script link github>.

## Initail setup
There are some prerequisits for installing and running the application.

    1. Android Studio.
    2. Mongodb Atlas cluster with mongodb version 5.0 or higher.
    3. Firebase Account. (for Alerts and Push notifications).
    4. GCP cloud credentials for maps service and firebase service.

#### ANDROID STUDIO:
1. install android studio:
   Download and Install android studio from : https://www.google.com/aclk?sa=l&ai=DChcSEwj0pf-3n9jyAhUikmYCHcgVDwIYABAAGgJzbQ&sig=AOD64_19frVYUKgRCbJHPUUxlDE6trN6cQ&q&nis=1&adurl&ved=2ahUKEwjF3u23n9jyAhXI4zgGHdeiBmAQ0Qx6BAgCEAE
   Clone confluent-demo app from github :  https://github.com/VenkateshShanbhag/AndroidApp.git
   Open project and Sync all the gradle dependencies.

2. Configure ATLAS:
   Create collections:
   1. users
   2. tracking
   3. tracking-historic (Time series collection)

3. Configure Realm:
   Create a realm app with below schema for user and tracking collections

   Tracking:

       {
             "properties": {
                   "partition_key": {
                   "bsonType": "string"
             },
             "_id": {
                   "bsonType": "objectId"
             },
             "lat": {
                   "bsonType": "double"
             },
             "lon": {
                   "bsonType": "double"
             },
             "Timestamp": {
                   "bsonType": "date"
             },
             "reg_num": {
                   "bsonType": "string"
             }
       },
       "required": ["_id"],
             "title": "Tracking",
             "bsonType": "object"
       }

   Users:

       {
             "properties": {
                   "partition_key": {
                   "bsonType": "string"
             },
             "owner_name": {
                   "bsonType": "string"
             },
             "vehicle_reg_date": {
                   "bsonType": "string"
             },
             "city_of_purchase": {
                   "bsonType": "string"
             },
             "_id": {
                   "bsonType": "string"
             }
             },
             "required": ["_id"],
             "title": "Users",
             "bsonType": "object"
       }

   Create webhooks to access the time series collection data.

   Function 1: tracking-data-api

         exports = function(payload, response) {
             const body = payload.body;
             console.log(payload.body);
             const doc = context.services.get("mongodb-atlas").db("vehicle").collection("tracking-historic").find(payload.query);
             return  doc;
         };

   Function 2: get-all-locations

         exports = function(payload, response) {
             const query = [
               {"$sort":{"Timestamp": -1}},
               {"$group":{
                 "_id":"$reg_num",
                 "reg_num":{"$first":"$reg_num"},
                 "Timestamp":{"$first":"$Timestamp"},
                 "lat":{"$first":"$lat"},
                 "lon":{"$first":"$lon"}
                 }
               },
               {"$project":{"_id":0}}];
               const doc = context.services.get("mongodb-atlas").db("vehicle").collection("tracking-historic").aggregate(query);
               return  doc;
         };
   //TODO : generalize the webhooks initialization    
   Copy the webhook URLs to their respective functions in repo.
4. Copy the app Id to appid variable in MyApplication class.

5. GCP map token:
   Create google API_KEY for accessing maps service and paste it in AndroidManifest.xml .


6. Firebase Account for push notifications:

7. Triggers for database collection update:
   Create a trigger with below function to listen to the database change event.

       exports = function(changeEvent) {
          const { updateDescription, fullDocument } = changeEvent;
        //Currently static latitude and longitudes are chosen. 
        // TODO: Create at the app registration, insert into a collection and read
          var static_lat = 14.24166;
          var static_lon = 74.448394;
        // Approx conversion into latitude and logitude scale.
          var radius = 10/111;
          const { reg_num, lat, lon, partition_key } = fullDocument;
          var valid = "true";
        // Validate if the point is inside the radius drawn on static lat/lon.
          var dist_points = (static_lat - lat) * (static_lat - lat) + (static_lon - lon) * (static_lon - lon);
          radius *= radius;
          if (dist_points > radius) {
             valid = "false";
             context.services.get("gcm").send({
                "to": "/topics/GeofenceTrigger",
                "notification":{
                   "title":"Alert!!",
                   "body":String(reg_num+" is violating the geoFence")
                }
             });
             const collection = context.services.get("mongodb-atlas").db("vehicle").collection("geofence_violation");
             delete fullDocument['_id'];
             collection.insertOne(fullDocument);
          }
       };



#### DOCKER:
      
