package com.prelimtek.android.picha.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prelimtek.android.picha.R;
import com.prelimtek.android.picha.dao.MediaDAOInterface;
import com.prelimtek.android.picha.view.PhotoProcUtil;

import java.util.List;


public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ViewHolder> {



    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name;
        TextView type;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.itemImage);
            //holder.name = (TextView)convertView.findViewById(R.id.imageName);
            itemView.setOnClickListener(onClickListener);
            itemView.setFocusable(true);
            //itemView.setTag(imageView.getTag());
        }
    }

    Context context;
    List<String> rowItems;
    View.OnClickListener onClickListener;
    int layoutId = -1;
    LayoutInflater inflator = null;
    MediaDAOInterface db = null;



    public ImageRecyclerViewAdapter(@NonNull  Context context, @NonNull List<String> items,int layoutId,@NonNull View.OnClickListener onClickListener,@NonNull  MediaDAOInterface dbHelper) {
        this.context = context;
        this.rowItems = items;
        this.onClickListener = onClickListener;
        this.layoutId=layoutId;
        this.inflator = LayoutInflater.from(context);
        this.db = dbHelper;
        /*try {
            //TODO make this cache
            db =  AppDAO.builder().open(context);
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public int getItemCount() {
        return rowItems.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = inflator.inflate(R.layout.list_image_layout,parent,false);

        ViewHolder holder = new ImageRecyclerViewAdapter.ViewHolder(convertView);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageId = rowItems.get(position);
        String rowItem = (String) getItem(position);
        String encodedBitmap = db.getImageById(rowItem);

        if(encodedBitmap!=null) {
            Bitmap bitmap = PhotoProcUtil.toBitMap(encodedBitmap);
            PhotoProcUtil.setPic(holder.imageView,bitmap);
            holder.imageView.setTag(imageId);
            holder.itemView.setTag(imageId);
        }

        //holder.name.setText(rowItem);

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    public Object getItem(int position) {
        return rowItems.get(position);
    }




}
