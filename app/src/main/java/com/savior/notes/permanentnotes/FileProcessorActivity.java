package com.savior.notes.permanentnotes;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.savior.notes.permanentnotes.dao.NoteFolderDAO;
import com.savior.notes.permanentnotes.dao.PermanentNotesDatabaseHelper;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileProcessorActivity  extends AppCompatActivity {

    private NoteFolderDAO noteFoldeDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noteFoldeDAO = new NoteFolderDAO(new PermanentNotesDatabaseHelper(this));

        try{
            Uri uri = getIntent().getData();
            InputStream in = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            reader.close();
            JSONArray jsonArr = new JSONArray(out.toString());
            noteFoldeDAO.cleanTable(Constants.DBT_NOTE_FOLDER);
            noteFoldeDAO.insertUsingJson(jsonArr);
        }catch (Exception fnf){
            Log.e(this.getClass().getName(),fnf.getMessage());
            Toast.makeText(getApplicationContext(), getString(R.string.error_file_load), Toast.LENGTH_LONG).show();
        }

        Intent intent = new Intent(FileProcessorActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
