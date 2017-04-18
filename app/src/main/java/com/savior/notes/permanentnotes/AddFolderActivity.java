package com.savior.notes.permanentnotes;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.savior.notes.permanentnotes.dao.NoteFolderDAO;
import com.savior.notes.permanentnotes.dao.PermanentNotesDatabaseHelper;

import java.util.Date;

public class AddFolderActivity extends AppCompatActivity {

    private NoteFolderDAO noteFoldeDAO;
    private String updateId;
    private String parentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noteFoldeDAO = new NoteFolderDAO(new PermanentNotesDatabaseHelper(this));
        updateId = getIntent().getStringExtra(Constants.ID);
        parentId = getIntent().getStringExtra(Constants.PARENTID);
        setContentView(R.layout.activity_add_folder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.main_cancel){
            startActivity(new Intent(AddFolderActivity.this, MainActivity.class));
        }
        return true;
    }

    public void addFolder(View v) {
        String editTitle = ((EditText) findViewById(R.id.EditTextTitle)).getText().toString();

        if("".equals(editTitle)){
            Toast.makeText(getApplicationContext(), getString(R.string.fields_required), Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues notefolderContent = new ContentValues();
        notefolderContent.put(Constants.DBNAME,editTitle);
        notefolderContent.put(Constants.DBDESCRIPTION,editTitle);
        notefolderContent.put(Constants.DBTYPE_NOTE,Constants.FOLDER);
        notefolderContent.put(Constants.DBDATENOTIFY,new Date().getTime());
        notefolderContent.put(Constants.DBPARENT, parentId);
        if(updateId == null){
            noteFoldeDAO.insert(notefolderContent);
        }else{
            noteFoldeDAO.update(updateId, notefolderContent);
        }
        Toast.makeText(getApplicationContext(), updateId == null?
                getString(R.string.folder_add):getString(R.string.folder_up), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AddFolderActivity.this, MainActivity.class);
        intent.putExtra(Constants.ID, parentId);
        startActivity(intent);
    }
}
