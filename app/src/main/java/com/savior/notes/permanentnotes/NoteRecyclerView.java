package com.savior.notes.permanentnotes;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savior.notes.permanentnotes.holder.OptionViewHolder;
import com.savior.notes.permanentnotes.listener.ListItemClickListener;

/**
 * Created by Orlando on 3/26/2017.
 */

public class NoteRecyclerView extends RecyclerView.Adapter<OptionViewHolder> {

    private final ListItemClickListener mOnClickListener;
    private Cursor permanentNotesCursor;
    private static final String TAG = NoteRecyclerView.class.getSimpleName();

    public NoteRecyclerView(ListItemClickListener mOnClickListener, Cursor permanentNotesCursor) {
        this.mOnClickListener = mOnClickListener;
        this.permanentNotesCursor = permanentNotesCursor;
    }

    public void swapCursor(Cursor newCursor) {
        if (permanentNotesCursor != null) permanentNotesCursor.close();
        permanentNotesCursor = newCursor;
        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
    }

    @Override
    public OptionViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.note_list_item, viewGroup, false);
        OptionViewHolder viewHolder = new OptionViewHolder(view,mOnClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OptionViewHolder holder, int position)  {
        if(!permanentNotesCursor.moveToPosition(position)){
            return;
        }
        String data = permanentNotesCursor.getString(permanentNotesCursor.getColumnIndex(Constants.DBNAME));
        String id = permanentNotesCursor.getString(permanentNotesCursor.getColumnIndex(Constants.DBID));
        String type = permanentNotesCursor.getString(permanentNotesCursor.getColumnIndex(Constants.DBTYPE_NOTE));
        holder.listItemNumberView.setText(data);
        holder.mImageView.setImageResource(Constants.NOTE.equals(type) ? R.drawable.note : R.drawable.folder);
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return permanentNotesCursor.getCount();
    }

}