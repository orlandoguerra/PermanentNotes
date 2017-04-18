package com.savior.notes.permanentnotes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.savior.notes.permanentnotes.dao.NoteFolderDAO;
import com.savior.notes.permanentnotes.dao.PermanentNotesDatabaseHelper;

import java.util.Date;

public class NoteDetailActivity extends AppCompatActivity {
    private NoteFolderDAO noteFoldeDAO;
    private String updateId;
    private String parentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        noteFoldeDAO = new NoteFolderDAO(new PermanentNotesDatabaseHelper(this));
        updateId = getIntent().getStringExtra(Constants.ID);
        parentId = getIntent().getStringExtra(Constants.PARENTID);
        if(updateId != null){
            Cursor note = noteFoldeDAO.getDetails(updateId);
            if(!note.moveToFirst()){
                return;
            }
            String name = note.getString(note.getColumnIndex(Constants.DBNAME));
            String desc = note.getString(note.getColumnIndex(Constants.DBDESCRIPTION));
            ((EditText) findViewById(R.id.EditTextTitle)).setText(name);
            ((EditText) findViewById(R.id.EditTextDesc)).setText(desc);
            ((Button) findViewById(R.id.ButtonSendFeedback)).setText(getString(R.string.update));
            note.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.main_cancel){
            startActivity(new Intent(NoteDetailActivity.this, MainActivity.class));
        }
        return true;
    }

    public void addNote(View v) {
        String editTitle = ((EditText) findViewById(R.id.EditTextTitle)).getText().toString();
        String editDesc = ((EditText) findViewById(R.id.EditTextDesc)).getText().toString();

        if("".equals(editTitle) || "".equals(editDesc)){
            Toast.makeText(getApplicationContext(),getString(R.string.fields_required), Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues notefolderContent = new ContentValues();
        notefolderContent.put(Constants.DBNAME,editTitle);
        notefolderContent.put(Constants.DBDESCRIPTION,editDesc);
        notefolderContent.put(Constants.DBTYPE_NOTE,Constants.NOTE);
        notefolderContent.put(Constants.DBDATENOTIFY,new Date().getTime());
        notefolderContent.put(Constants.DBPARENT, parentId);
        if(updateId == null){
            noteFoldeDAO.insert(notefolderContent);
        }else{
            noteFoldeDAO.update(updateId,notefolderContent);
        }
        Toast.makeText(getApplicationContext(), updateId == null?getString(R.string.note_add):getString(R.string.note_up), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(NoteDetailActivity.this, MainActivity.class);
        intent.putExtra(Constants.ID, parentId);
        startActivity(intent);
    }
}
