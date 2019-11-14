package com.bolofilipp.bathhouse;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

public class MyApplication extends Application {

    private DataBaseHelper m_db;
    private Stack<Integer> backStack = new Stack<>();
    private HashMap<Integer, DBItem> m_dbItemMap;

    enum cols {
        ID,
        NAME,
        COMMENT,
        PARENT_ID,
        IMAGE,
        CONTENT
    }

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
                item.id = query.getInt(cols.ID.ordinal());
                item.name = query.getString(cols.NAME.ordinal());
                item.comment = query.getString(cols.COMMENT.ordinal());
                item.parentId = query.getInt(cols.PARENT_ID.ordinal());
                item.image = query.getString(cols.IMAGE.ordinal());
                item.content = query.getString(cols.CONTENT.ordinal());
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

        for(int i = 0; i < list.size() - 1; i++)
        {
            for(int j = 0; j < list.size() - i - 1; j++)
            {
                if(list.get(j).id > list.get(j+1).id)
                {
                    DBItem tmp = list.get(j);
                    list.set(j, list.get(j+1));
                    list.set(j+1, tmp);
                }
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
