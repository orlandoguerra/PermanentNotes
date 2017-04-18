package com.savior.notes.permanentnotes.dao;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.savior.notes.permanentnotes.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class NoteFolderDAO {

    SQLiteOpenHelper sqlHelper;

    public NoteFolderDAO(SQLiteOpenHelper sqlHelper){
        this.sqlHelper = sqlHelper;
    }

    public Cursor getDetailsBasedParent(String parentId){
        Log.i(this.getClass().getName(),parentId);
        SQLiteDatabase db = sqlHelper.getReadableDatabase();
        Cursor cursor = db.query("NOTE_FOLDER", null, "PARENT = ?",  new String[]{parentId}, null, null, "TYPE_NOTE");
        return cursor;
    }

    public Cursor getDetails(String id){
        SQLiteDatabase db = sqlHelper.getReadableDatabase();
        Cursor cursor = db.query("NOTE_FOLDER", null, "_id = ?", new String[]{id}, null, null, "_id ASC");
        return cursor;
    }

    public String getParent(String id){
        if("0".equals(id)){
            return null;
        }
        SQLiteDatabase db = sqlHelper.getReadableDatabase();
        Cursor cursor = db.query("NOTE_FOLDER", null, "_id = ?", new String[]{id}, null, null, "_id ASC");
        String parent = null;
        try {
            if (cursor.moveToNext()) {
                parent = cursor.getString(cursor.getColumnIndex("PARENT"));
            }
        } finally {
            cursor.close();
            db.close();
        }

        return parent;
    }

    public boolean delete(String id) {
        SQLiteDatabase db = sqlHelper.getWritableDatabase();
        boolean result = db.delete("NOTE_FOLDER", " _id  =" + id, null) > 0;
        db.close();
        return result;
    }

    public void insert(ContentValues content){
        SQLiteDatabase db = sqlHelper.getWritableDatabase();
        db.insert("NOTE_FOLDER", null, content);
        db.close();
    }

    public void update(String id, ContentValues content){
        SQLiteDatabase db = sqlHelper.getWritableDatabase();
        db.update("NOTE_FOLDER", content, "_id = ?", new String[]{id});
        db.close();
    }

    public JSONArray getInfoAsJson(){
        SQLiteDatabase db = sqlHelper.getReadableDatabase();
        Cursor cursor = db.query("NOTE_FOLDER", null, null, null, null, null, "TYPE_NOTE");
        JSONArray jsonNotes = new JSONArray();
        while(cursor.moveToNext()){
            JSONObject note = new JSONObject();
            try{
                note.put(Constants.DBNAME,cursor.getString(cursor.getColumnIndex(Constants.DBNAME)));
                note.put(Constants.DBID,cursor.getString(cursor.getColumnIndex(Constants.DBID)));
                note.put(Constants.DBTYPE_NOTE,cursor.getString(cursor.getColumnIndex(Constants.DBTYPE_NOTE)));
                note.put(Constants.DBDESCRIPTION,cursor.getString(cursor.getColumnIndex(Constants.DBDESCRIPTION)));
                note.put(Constants.DBDATENOTIFY,cursor.getInt(cursor.getColumnIndex(Constants.DBDATENOTIFY)));
                note.put(Constants.DBPARENT,cursor.getInt(cursor.getColumnIndex(Constants.DBPARENT)));
            }catch(JSONException je){

            }
            jsonNotes.put(note);
        }
        return jsonNotes;
    }

    public void cleanTable(String tableName){
        SQLiteDatabase db = sqlHelper.getWritableDatabase();
        db.execSQL("delete from NOTE_FOLDER");
    }

    public void insertUsingJson(JSONArray jsonArr) throws JSONException{
        SQLiteDatabase db = sqlHelper.getWritableDatabase();
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject json = jsonArr.getJSONObject(i);
            ContentValues importContent = new ContentValues();
            importContent.put(Constants.DBID,(String)json.get(Constants.DBID));
            importContent.put(Constants.DBNAME,(String)json.get(Constants.DBNAME));
            importContent.put(Constants.DBDESCRIPTION,(String)json.get(Constants.DBDESCRIPTION));
            importContent.put(Constants.DBTYPE_NOTE,(String)json.get(Constants.DBTYPE_NOTE));
            importContent.put(Constants.DBDATENOTIFY,(Integer)json.get(Constants.DBDATENOTIFY));
            importContent.put(Constants.DBPARENT, (Integer)json.get(Constants.DBPARENT));
            db.insert("NOTE_FOLDER", null, importContent);
        }
        db.close();
    }
}
