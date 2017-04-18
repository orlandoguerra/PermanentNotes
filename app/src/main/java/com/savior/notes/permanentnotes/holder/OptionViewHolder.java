package com.savior.notes.permanentnotes.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.savior.notes.permanentnotes.R;
import com.savior.notes.permanentnotes.listener.ListItemClickListener;

/**
 * Created by Orlando on 3/26/2017.
 */

public class OptionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView listItemNumberView;
    private ListItemClickListener mOnClickListener;
    public ImageView mImageView;

    public OptionViewHolder(View itemView, ListItemClickListener mOnClickListener) {
        super(itemView);
        this.mOnClickListener = mOnClickListener;
        listItemNumberView = (TextView) itemView.findViewById(R.id.tv_item_number);
        mImageView = (ImageView) itemView.findViewById(R.id.imageNumber);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int clickedPosition = getAdapterPosition();
        this.mOnClickListener.onListItemClick(clickedPosition);
    }
}
