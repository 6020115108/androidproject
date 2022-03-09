package com.xd.xoid.nano.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    /**
     * 继承SQLiteOpenHelper抽象类 重写的创表方法，此SQLiteDatabase db 不能关闭
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
//        createTable(db);
    }

    /**
     * 继承SQLiteOpenHelper抽象类 重写的升级方法
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
