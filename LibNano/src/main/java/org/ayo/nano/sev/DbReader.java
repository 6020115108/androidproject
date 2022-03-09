package org.ayo.nano.sev;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import org.ayo.nano.LibNano;

import java.util.ArrayList;
import java.util.List;

public class DbReader {

    public static List<String> listTables(String dbPath){
        List<String> tables = new ArrayList<>();
        SQLiteDatabase db = LibNano.app.openOrCreateDatabase(dbPath, Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while(cursor.moveToNext()){
            //遍历出表名
            String name = cursor.getString(0);
            tables.add(name);
        }
        cursor.close();
        db.close();
        return tables;
    }

    public static List<AssocArray2> listRows(String dbPath, String tableName){
        List<AssocArray2> rows = new ArrayList<>();
        SQLiteDatabase db = LibNano.app.openOrCreateDatabase(dbPath, Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("select * from " + tableName, null);

        rows.add(AssocArray2.array().add("columns", cursor.getColumnNames()));

        while(cursor.moveToNext()){
            //遍历出表名
            AssocArray2 row = new AssocArray2();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                row.add(cursor.getColumnName(i), cursor.getString(i));
            }
            rows.add(row);
        }

        cursor.close();
        db.close();
        return rows;
    }
}
