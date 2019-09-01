package com.prelimtek.android.customcomponents;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prelimtek.android.basecomponents.Configuration;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class NotesTextRecyclerViewAdapter extends RecyclerView.Adapter<NotesTextRecyclerViewAdapter.ViewHolder> {

    public static final int PAGE_BUFFER_SIZE = 2;
    public static final int PAGE_VISIBLE_SIZE = 1;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView noteDateTextView;
        TextView noteTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            noteDateTextView = (TextView) itemView.findViewById(R.id.notedate);
            noteTextView = (TextView)itemView.findViewById(R.id.noteText);
            itemView.setOnClickListener(onClickListener);
            itemView.setFocusable(true);
            //itemView.setTag(noteDateTextView.getTag());
        }

        public void bindTo(NotesModel notesModel) {
            setDateValue(noteDateTextView,notesModel.getDate());
            noteTextView.setText(notesModel.noteText);
            noteTextView.setTag(notesModel.getDate());
        }

        public void clear() {
            noteDateTextView.setText(null);
            noteDateTextView.setTag(null);
            noteTextView.setText(null);
            noteTextView.setTag(null);
        }

        //TODO put in util class
        public void setDateValue(TextView view,
                                        final Long newDate ){
            view.setTag(newDate);
            DateFormat dateFormat = Configuration.configuredPreferences(view.getContext()).dateFormat;
            if(newDate!=null) {
                String newDateStr = dateFormat.format(new Date(newDate));
                view.setText(newDateStr);
            }

        }


    }

    Context context;
    List<NotesModel> rowItems;
    View.OnClickListener onClickListener;
    int layoutId = -1;
    LayoutInflater inflator = null;
    //MediaDAOInterface db = null;



    public NotesTextRecyclerViewAdapter(@NonNull Context context, @NonNull List<NotesModel> items, int layoutId, @NonNull View.OnClickListener onClickListener) {
        this.context = context;
        this.rowItems = items;
        this.onClickListener = onClickListener;
        this.layoutId=layoutId;
        this.inflator = LayoutInflater.from(context);
        //this.db = dbHelper;
        /*try {
            //TODO make this cache
            db =  AppDAO.builder().open(context);
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public int getItemCount() {
        return rowItems==null?0:rowItems.size();
    }

    public void setRowItems(List<NotesModel> rowItems) {
        this.rowItems = rowItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View convertView = inflator.inflate(R.layout.notes_list_layout,parent,false);

        ViewHolder holder = new NotesTextRecyclerViewAdapter.ViewHolder(convertView);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        NotesModel notesModel = rowItems.get(position);

        if(notesModel!=null)
            holder.bindTo(notesModel);
        else
            holder.clear();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public NotesModel getItem(int position) {
        return rowItems.get(position);
    }


}
