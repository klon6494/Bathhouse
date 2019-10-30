package com.bolofilipp.bathhouse;

import android.app.Application;
import android.content.Context;
import android.database.SQLException;

import java.io.IOException;
import java.util.Stack;

public class MyApplication extends Application {

    private DataBaseHelper m_db;
    private Stack<Integer> backStack = new Stack<>();

    public void pushStack(int id)
    {
        backStack.push(id);
    }

    public Integer popStack()
    {
        if(backStack.size() > 0)
            return backStack.pop();
        else
            return -1;
    }

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
