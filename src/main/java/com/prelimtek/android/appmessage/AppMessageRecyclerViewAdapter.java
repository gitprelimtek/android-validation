package com.prelimtek.android.appmessage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prelimtek.android.basecomponents.Configuration;
import com.prelimtek.android.customcomponents.NotesModel;
import com.prelimtek.android.customcomponents.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class AppMessageRecyclerViewAdapter extends RecyclerView.Adapter<AppMessageRecyclerViewAdapter.ViewHolder> {

    public static final int PAGE_BUFFER_SIZE = 3;
    public static final int PAGE_VISIBLE_SIZE = 2;

    public class ViewHolder extends RecyclerView.ViewHolder {
        View layout;
        TextView appMessageTitleTextView;
        TextView appMessageDateTextView;
        TextView appMessageTextView;
        ImageView appMessageImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
            appMessageTitleTextView = (TextView) itemView.findViewById(R.id.messageTitle);
            appMessageDateTextView = (TextView) itemView.findViewById(R.id.messagedate);
            appMessageTextView = (TextView)itemView.findViewById(R.id.messageText);
            appMessageImageView = (ImageView)itemView.findViewById(R.id.messageImage);
            itemView.setOnClickListener(onClickListener);
            itemView.setFocusable(true);
            //itemView.setTag(appMessageDateTextView.getTag());
        }

        public void bindTo(AppMessageModel messageModel) {
            appMessageTitleTextView.setText(messageModel.getTitle());
            setDateValue(appMessageDateTextView,messageModel.getReceiptDate());
            appMessageTextView.setText(messageModel.getBody());
            appMessageImageView.setImageBitmap(messageModel.getIcon());
            layout.setTag(messageModel);
        }

        public void clear() {
            appMessageDateTextView.setText(null);
            appMessageDateTextView.setTag(null);
            appMessageTextView.setText(null);
            layout.setTag(null);
        }

        //TODO put in util class
        public void setDateValue(TextView view,
                                        final Long newDate ){

            if(view==null)return;
            DateFormat dateFormat = Configuration.configuredPreferences(view.getContext()).dateFormat;
            if(newDate!=null) {
                String newDateStr = dateFormat.format(new Date(newDate));
                view.setText(newDateStr);
            }

        }


    }

    Context context;
    List<AppMessageModel> rowItems;
    View.OnClickListener onClickListener;
    int layoutId = -1;
    LayoutInflater inflator = null;
    //MediaDAOInterface db = null;



    public AppMessageRecyclerViewAdapter(@NonNull Context context, @NonNull List<AppMessageModel> items, int layoutId, @NonNull View.OnClickListener onClickListener) {
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

    public void setRowItems(List<AppMessageModel> rowItems) {
        this.rowItems = rowItems;
    }

    public void appendRowItems(List<AppMessageModel> rowItems) {
        this.rowItems.addAll(rowItems);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View convertView = inflator.inflate(layoutId,parent,false);

        ViewHolder holder = new AppMessageRecyclerViewAdapter.ViewHolder(convertView);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        AppMessageModel notesModel = rowItems.get(position);

        if(notesModel!=null)
            holder.bindTo(notesModel);
        else
            holder.clear();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public AppMessageModel getItem(int position) {
        return rowItems.get(position);
    }

}
