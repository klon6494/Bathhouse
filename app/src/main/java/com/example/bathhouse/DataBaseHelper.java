package com.example.bathhouse;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    // Database Information
    static final String DB_NAME = "main.db";

    // database version
    static final int DB_VERSION = 1;

    // путь к базе данных вашего приложения
    private static String DB_PATH = "/data/user/0/com.example.bathhouse/databases/";
    private SQLiteDatabase myDataBase;
    private final Context mContext;

    /**
     * Конструктор
     * Принимает и сохраняет ссылку на переданный контекст для доступа к ресурсам приложения
     * @param context
     */
    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.mContext = context;
    }

    /**
     * Создает пустую базу данных и перезаписывает ее нашей собственной базой
     * */
    public void createDataBase() throws IOException{
        boolean dbExist = checkDataBase();

        if(dbExist){
            //ничего не делать - база уже есть
        }else{
            //вызывая этот метод создаем пустую базу, позже она будет перезаписана
            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
       }
    }

    /**
     * Проверяет, существует ли уже эта база, чтобы не копировать каждый раз при запуске приложения
     * @return true если существует, false если не существует
     */
    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            //база еще не существует
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    /**
     * Копирует базу из папки assets заместо созданной локальной БД
     * Выполняется путем копирования потока байтов.
     * */
    private void copyDataBase() throws IOException {
        //Открываем локальную БД как входящий поток
        InputStream myInput = mContext.getAssets().open(DB_NAME);

        //Путь ко вновь созданной БД
        String outFileName = DB_PATH + DB_NAME;

        //Открываем пустую базу данных как исходящий поток
        OutputStream myOutput = new FileOutputStream(outFileName);

        //перемещаем байты из входящего файла в исходящий
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //закрываем потоки
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        //открываем БД
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor getDBValues(int id)
    {
        return myDataBase.query(TABLE_NAME, null, PARENT_ID + "=" + String.valueOf(id), null, null, null, ID);

    }

    public Cursor getCurrentValue(int id)
    {
        return myDataBase.query(TABLE_NAME, null, ID + "=" + String.valueOf(id), null, null, null, ID);
    }

    public boolean isItMenu(int id)
    {
        Cursor data = myDataBase.query(TABLE_NAME, null, PARENT_ID + "=" + String.valueOf(id), null, null, null, ID);
        return data.moveToFirst();
    }

    DBItem getRandomItem()
    {
        int numRows = (int)DatabaseUtils.queryNumEntries(myDataBase, "main");
        Random r = new Random();
        int rId = r.nextInt(numRows);
        Cursor query =  myDataBase.query(TABLE_NAME, null, ID + "=" + String.valueOf(rId), null, null, null, ID);
        DBItem item = new DBItem();
        if (query.moveToFirst()) {
            while (!query.isAfterLast()) {
                item.id = query.getInt(PatternMenuActivity.cols.ID.ordinal());
                item.name = query.getString(PatternMenuActivity.cols.NAME.ordinal());
                item.comment = query.getString(PatternMenuActivity.cols.COMMENT.ordinal());
                item.parentId = query.getInt(PatternMenuActivity.cols.PARENT_ID.ordinal());
                item.image = query.getString(PatternMenuActivity.cols.IMAGE.ordinal());
                item.content = query.getString(PatternMenuActivity.cols.CONTENT.ordinal());
                return item;
            }
        }
        return null;
    }


    // Здесь можно добавить вспомогательные методы для доступа и получения данных из БД
    // вы можете возвращать курсоры через "return myDataBase.query(....)", это облегчит их использование
    // в создании адаптеров для ваших view
}
