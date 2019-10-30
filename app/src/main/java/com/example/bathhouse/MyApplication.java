package com.example.bathhouse;

import android.app.Application;
import android.content.Context;
import android.database.SQLException;

import java.io.IOException;

public class MyApplication extends Application {

    private DataBaseHelper m_db;

    public void initializeDb(Context context) {
        m_db = new DataBaseHelper(context);
        try {
            m_db.updateDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            m_db.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }
    }

    public DataBaseHelper db() {
        return this.m_db;
    }

}
