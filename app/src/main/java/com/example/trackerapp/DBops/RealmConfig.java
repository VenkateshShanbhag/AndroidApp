package com.example.trackerapp.DBops;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trackerapp.Model.Users;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.SyncConfiguration;

public class RealmConfig  {
    public Realm thisrealm;
    public Realm getRealm(){
        return thisrealm;
    }
}
