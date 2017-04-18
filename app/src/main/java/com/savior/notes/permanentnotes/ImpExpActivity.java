package com.savior.notes.permanentnotes;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.savior.notes.permanentnotes.dao.NoteFolderDAO;
import com.savior.notes.permanentnotes.dao.PermanentNotesDatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ImpExpActivity extends AppCompatActivity {

    private NoteFolderDAO noteFoldeDAO;
    private static final int READ_REQUEST_CODE = 42;
    private static final int READ_SEND_CODE = 45;
    private String dataLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imp_exp);
        noteFoldeDAO = new NoteFolderDAO(new PermanentNotesDatabaseHelper(this));
        File root   = Environment.getExternalStorageDirectory();
        dataLocation = root.getAbsolutePath() + "/permnotedata";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(ImpExpActivity.this, MainActivity.class));
        return true;
    }

    public void importJson(View v) {
        System.out.print("xxxxxxxxxxx");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
        startActivity(intent);
    }

    public void exportJson(View v) {
        System.out.println("exportJson");
        JSONArray json = noteFoldeDAO.getInfoAsJson();
        File dir = new File(dataLocation);
        dir.mkdirs();
        File file = new File(dir, "Data.json");
        FileOutputStream out = null;
        System.out.print("exportJso2n");
        try {
            out = new FileOutputStream(file);
            out.write(json.toString().getBytes());
        } catch (IOException ioe) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_file), Toast.LENGTH_LONG).show();
            Log.e(this.getClass().getName(), ioe.getMessage());
        } finally {
            if (out != null) {
                try{
                    out.close();
                }catch (IOException ioex){
                    ioex.printStackTrace();
                }
            }
        }

        //Uri u1 = Uri.fromFile(file);
        Uri u1 = FileProvider.getUriForFile(ImpExpActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("vnd.android.cursor.dir/email");
        emailIntent.putExtra(Intent.EXTRA_STREAM, u1);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_title) +" "+  Util.getDateString());
        startActivityForResult(emailIntent, READ_SEND_CODE);
    }

    public void pickFile(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            try{
                Uri uri = resultData.getData();
                System.out.println("Here");
                InputStream in = getContentResolver().openInputStream(uri);
                BufferedReader reader = new BufferedReader( new InputStreamReader(in));
                StringBuilder out = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                reader.close();
                JSONArray jsonArr = new JSONArray(out.toString());
                noteFoldeDAO.cleanTable(Constants.DBT_NOTE_FOLDER);
                noteFoldeDAO.insertUsingJson(jsonArr);
            }catch (IOException fnf){
                Toast.makeText(getApplicationContext(), getString(R.string.error_file), Toast.LENGTH_LONG).show();
                Log.e(this.getClass().getName(), fnf.getMessage());
            }catch (JSONException jex){
                Toast.makeText(getApplicationContext(), getString(R.string.error_file), Toast.LENGTH_LONG).show();
                Log.e(this.getClass().getName(), jex.getMessage());
            }
        }
        Intent intent = new Intent(ImpExpActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void generateFile(View v) {
        JSONArray json = noteFoldeDAO.getInfoAsJson();
        File dir = new File(dataLocation);
        dir.mkdirs();
        File file  = new File(dir, "Data.json");
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(file);
            out.write(json.toString().getBytes());
            Toast.makeText(getApplicationContext(), getString(R.string.data_file_msg)+ " "+dataLocation, Toast.LENGTH_LONG).show();
        } catch (IOException ioe) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_file), Toast.LENGTH_LONG).show();
            Log.e(this.getClass().getName(), ioe.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ioex) {
                    ioex.printStackTrace();
                }
            }
        }
    }

}
