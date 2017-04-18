package com.savior.notes.permanentnotes;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.savior.notes.permanentnotes.dao.NoteFolderDAO;
import com.savior.notes.permanentnotes.dao.PermanentNotesDatabaseHelper;
import com.savior.notes.permanentnotes.listener.ListItemClickListener;


public class MainActivity extends AppCompatActivity {

    private static final String[]permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int PERMISSION_REQUEST_CODE = 11041984;

    private NoteRecyclerView mAdapter;
    private RecyclerView mRecyclerView;
    private Cursor permanentNotesCursor;
    private NoteFolderDAO noteFoldeDAO;
    private AppCompatActivity activity;
    private String parentId;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        id = getIntent().getStringExtra(Constants.ID) == null?"0":getIntent().getStringExtra(Constants.ID);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_numbers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        noteFoldeDAO = new NoteFolderDAO(new PermanentNotesDatabaseHelper(this));
        if(!"0".equals(id)){
            Cursor cursor = noteFoldeDAO.getDetails(id);
            if(cursor.moveToFirst()){
                String name = cursor.getString(cursor.getColumnIndex(Constants.DBNAME));
                setTitle(name);
            }
        }
        parentId = noteFoldeDAO.getParent(id);
        permanentNotesCursor = noteFoldeDAO.getDetailsBasedParent(id);
        mAdapter = new NoteRecyclerView(new ClickNote(),permanentNotesCursor);
        mRecyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                String id = (String)viewHolder.itemView.getTag();
                AskOption(id).show();
            }
        }).attachToRecyclerView(mRecyclerView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NoteDetailActivity.class);
                intent.putExtra(Constants.PARENTID, id);
                startActivity(intent);
            }
        });
    }


    private AlertDialog AskOption(final String idDelete) {

        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                .setTitle(getString(R.string.del_title))
                .setMessage(getString(R.string.del_confirmation))
                .setIcon(R.drawable.delete)
                .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        noteFoldeDAO.delete(idDelete);
                        permanentNotesCursor = noteFoldeDAO.getDetailsBasedParent(id);
                        mAdapter.swapCursor(permanentNotesCursor);
                        Toast.makeText(getApplicationContext(), getString(R.string.rec_deleted), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })

                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.swapCursor(noteFoldeDAO.getDetailsBasedParent(id));
                        dialog.dismiss();
                    }
                }).create();
        return myQuittingDialogBox;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        if(parentId == null){
            MenuItem item = menu.findItem(R.id.main_back);
            item.setVisible(false);
        }
        return true;
    }

    class ClickNote implements ListItemClickListener{

        @Override
        public void onListItemClick(int clickedItem) {
            if(!permanentNotesCursor.moveToPosition(clickedItem)){
                return;
            }
            String data = permanentNotesCursor.getString(permanentNotesCursor.getColumnIndex(Constants.DBID));
            String type = permanentNotesCursor.getString(permanentNotesCursor.getColumnIndex(Constants.DBTYPE_NOTE));

            Intent intent =  new Intent(MainActivity.this,Constants.NOTE.equals(type)?NoteDetailActivity.class:MainActivity.class);
            intent.putExtra(Constants.ID, data);
            intent.putExtra(Constants.PARENTID, id);
            startActivity(intent);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        if(item.getItemId() == R.id.main_addfolder){
            intent = new Intent(MainActivity.this, AddFolderActivity.class);
            intent.putExtra(Constants.PARENTID, id);
            startActivity(intent);
        }else if (item.getItemId() == R.id.main_import){
            if(!hasPermission()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(permissions, PERMISSION_REQUEST_CODE);
                    return true;
                }
            }else{
                intent = new Intent(MainActivity.this, ImpExpActivity.class);
                startActivity(intent);
            }
        }else{
            intent = new Intent(MainActivity.this, MainActivity.class);
            intent.putExtra(Constants.ID, parentId);

        }
        startActivity(intent);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        boolean allowed = true;
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:
                for(int res:grantResults){
                    allowed = allowed && (res== PackageManager.PERMISSION_GRANTED);
                }
                break;
            default: allowed = false;
                break;
        }

        if(allowed){
            Intent intent = new Intent(MainActivity.this, ImpExpActivity.class);
            intent.putExtra(Constants.ID, parentId);
            startActivity(intent);
        }
    }

    private boolean hasPermission(){
        int res = 0;
        for (String permission:permissions) {
            res = checkCallingOrSelfPermission(permission);
            if(!(res==PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

}
