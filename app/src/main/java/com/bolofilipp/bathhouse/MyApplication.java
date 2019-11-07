package com.bolofilipp.bathhouse;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

public class MyApplication extends Application {

    private DataBaseHelper m_db;
    private Stack<Integer> backStack = new Stack<>();
    private HashMap<Integer, DBItem> m_dbItemMap;

    public Integer stackSize()
    {
        return backStack.size();
    }

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

        m_dbItemMap = new HashMap<Integer, DBItem>();
        Cursor query = m_db.getAllItems();

        if (query.moveToFirst()) {
            while (!query.isAfterLast()) {
                DBItem item = new DBItem();
                item.id = query.getInt(PatternMenuActivity.cols.ID.ordinal());
                item.name = query.getString(PatternMenuActivity.cols.NAME.ordinal());
                item.comment = query.getString(PatternMenuActivity.cols.COMMENT.ordinal());
                item.parentId = query.getInt(PatternMenuActivity.cols.PARENT_ID.ordinal());
                item.image = query.getString(PatternMenuActivity.cols.IMAGE.ordinal());
                item.content = query.getString(PatternMenuActivity.cols.CONTENT.ordinal());
                m_dbItemMap.put(item.id, item);
                query.moveToNext();
            }
            query.close();
        }
    }

    public DataBaseHelper db() {
        return this.m_db;
    }

    public ArrayList<DBItem> getDBValues(int id)
    {
        ArrayList<DBItem> list = new ArrayList<>();
        for (DBItem item : m_dbItemMap.values()) {
            if(item.parentId == id) {
                list.add(item);
            }
        }
        return list;
    }

    public DBItem getCurrentValue(int id)
    {
        if(id == 0)
            return new DBItem();
        return m_dbItemMap.get(id);
    }

    public boolean isItMenu(int id)
    {
        if(id == 0)
            return true;

        for (DBItem item : m_dbItemMap.values()) {
            if(item.parentId == id) {
                return true;
            }
        }
        return false;
    }

    public DBItem getRandomItem() {
        Random generator = new Random();
        Object[] values = m_dbItemMap.values().toArray();
        return (DBItem)values[generator.nextInt(values.length)];
    }
}
