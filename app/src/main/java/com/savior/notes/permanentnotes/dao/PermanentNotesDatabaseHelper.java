package com.savior.notes.permanentnotes.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.savior.notes.permanentnotes.Constants;

import java.util.Date;

/**
 * Created by Orlando on 3/26/2017.
 */

public class PermanentNotesDatabaseHelper  extends SQLiteOpenHelper {

    private static final String DB_NAME = "PermanentNotes3";
    private static final int DB_VERSION = 1;

    public PermanentNotesDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE NOTE_FOLDER (_id INTEGER PRIMARY KEY AUTOINCREMENT "
                + ",NAME TEXT"
                + ",DESCRIPTION TEXT"
                + ",TYPE_NOTE TEXT"
                + ",DATENOTIFY NUMERIC"
                + ",PARENT INTEGER"
                + ");");

        ContentValues notefolderContent = new ContentValues();

        notefolderContent.put("NAME","Friends");
        notefolderContent.put("DESCRIPTION","Desc");
        notefolderContent.put("TYPE_NOTE", Constants.FOLDER);
        notefolderContent.put("DATENOTIFY",new Date().getTime());
        notefolderContent.put("PARENT",0);

        db.insert("NOTE_FOLDER", null, notefolderContent);

        notefolderContent = new ContentValues();

        notefolderContent.put("NAME","Family");
        notefolderContent.put("DESCRIPTION","Desc");
        notefolderContent.put("TYPE_NOTE",Constants.FOLDER);
        notefolderContent.put("DATENOTIFY",new Date().getTime());
        notefolderContent.put("PARENT",0);

        db.insert("NOTE_FOLDER", null, notefolderContent);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
