package com.example.bathhouse;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

public class DataBaseHelper extends SQLiteOpenHelper {

    // Table name
    public static final String TABLE_NAME = "main";

    // Table columns
    public static final String ID = "_id";
    public static final String NAME = "Name";
    public static final String COMMENT = "Comment";
    public static final String PARENT_ID = "ParentId";
    public static final String IMAGE = "Image";
    public static final String CONTENT = "Content";

    private static String DB_NAME = "main.db";
    private static String DB_PATH = "";
    private static final int DB_VERSION = 1;

    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private boolean mNeedUpdate = true;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        if (android.os.Build.VERSION.SDK_INT >= 17)
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        else
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        this.mContext = context;

        copyDataBase();

        this.getReadableDatabase();
    }

    public void updateDataBase() throws IOException {
        if (mNeedUpdate) {
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists())
                dbFile.delete();

            copyDataBase();

            mNeedUpdate = true;
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private void copyDBFile() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        //InputStream mInput = mContext.getResources().openRawResource(R.raw.info);
        OutputStream mOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public boolean openDataBase() throws SQLException {
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            mNeedUpdate = true;
    }

    public Cursor getDBValues(int id)
    {
        return mDataBase.query(TABLE_NAME, null, PARENT_ID + "=" + String.valueOf(id), null, null, null, ID);

    }

    public Cursor getCurrentValue(int id)
    {
        return mDataBase.query(TABLE_NAME, null, ID + "=" + String.valueOf(id), null, null, null, ID);
    }

    public boolean isItMenu(int id)
    {
        Cursor data = mDataBase.query(TABLE_NAME, null, PARENT_ID + "=" + String.valueOf(id), null, null, null, ID);
        return data.moveToFirst();
    }

    DBItem getRandomItem()
    {
        int numRows = (int) DatabaseUtils.queryNumEntries(mDataBase, "main");
        ArrayList<Integer> ids = new ArrayList<>();
        String[] idString = {"_id"};
        Cursor query1 =  mDataBase.query(TABLE_NAME, idString, null, null, null, null, ID);
        if (query1.moveToFirst()) {
            while (!query1.isAfterLast()) {
                ids.add(query1.getInt(PatternMenuActivity.cols.ID.ordinal()));
                query1.moveToNext();
            }
        }

        Random r = new Random();
        int rId = ids.get(r.nextInt(numRows));
        Cursor query =  mDataBase.query(TABLE_NAME, null, ID + "=" + String.valueOf(rId), null, null, null, ID);
        DBItem item = new DBItem();
        if (query.moveToFirst()) {
            while (!query.isAfterLast()) {
                item.id = query.getInt(PatternMenuActivity.cols.ID.ordinal());
                item.name = query.getString(PatternMenuActivity.cols.NAME.ordinal());
                item.comment = query.getString(PatternMenuActivity.cols.COMMENT.ordinal());
                item.parentId = query.getInt(PatternMenuActivity.cols.PARENT_ID.ordinal());
                item.image = query.getString(PatternMenuActivity.cols.IMAGE.ordinal());
                item.content = query.getString(PatternMenuActivity.cols.CONTENT.ordinal());
                query.close();
                return item;
            }
        }
        query.close();
        return null;
    }
}