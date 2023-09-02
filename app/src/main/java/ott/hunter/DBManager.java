package ott.hunter;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;



public class DBManager {

    private ott.hunter.DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String name, String desc) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(ott.hunter.DatabaseHelper.SUBJECT, name);
        contentValue.put(ott.hunter.DatabaseHelper.DESC, desc);
        database.insert(ott.hunter.DatabaseHelper.TABLE_NAME, null, contentValue);
    }

   /* public Cursor fetch() {
        String[] columns = new String[] { ott.hunter.DatabaseHelper._ID, ott.hunter.DatabaseHelper.SUBJECT, ott.hunter.DatabaseHelper.DESC };
        Cursor cursor = database.query(ott.hunter.DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
*/

 /*
    public int update(long _id, String name, String desc) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ott.hunter.DatabaseHelper.SUBJECT, name);
        contentValues.put(ott.hunter.DatabaseHelper.DESC, desc);
        int i = database.update(ott.hunter.DatabaseHelper.TABLE_NAME, contentValues, ott.hunter.DatabaseHelper._ID + " = " + _id, null);
        return i;
    }
*/
   /* public void delete(long _id) {
        database.delete(ott.hunter.DatabaseHelper.TABLE_NAME, ott.hunter.DatabaseHelper._ID + "=" + _id, null);
    }
*/
}
