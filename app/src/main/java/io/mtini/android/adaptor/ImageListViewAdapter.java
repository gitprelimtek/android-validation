package io.mtini.android.adaptor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;

import io.mtini.android.tenantmanager.R;
import com.prelimtek.android.picha.view.PhotoProcUtil;
import io.mtini.model.AppDAO;

@Deprecated
public class ImageListViewAdapter extends BaseAdapter {

    Context context;
    List<String> rowItems;
    View.OnClickListener onClickListener;
    int layoutId = -1;

    AppDAO db = null;


    public ImageListViewAdapter(Context context, List<String> items,int layoutId, View.OnClickListener onClickListener) {
        this.context = context;
        this.rowItems = items;
        this.onClickListener = onClickListener;
        this.layoutId=layoutId;
        try {
            //TODO make this cache
            db = AppDAO.builder().open(context);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private class ViewHolder {
        ImageView imageView;
        TextView name;
        TextView type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(layoutId, null);

        holder = new ViewHolder();
        holder.imageView = (ImageView) convertView.findViewById(R.id.itemImage);
        //holder.name = (TextView)convertView.findViewById(R.id.imageName);
        convertView.setOnClickListener(onClickListener);


        String rowItem = (String) getItem(position);
        String encodedBitmap = db.getImageById(rowItem);

        if(encodedBitmap!=null) {
            Bitmap bitmap = PhotoProcUtil.toBitMap(encodedBitmap);
            PhotoProcUtil.setPic(holder.imageView,bitmap);
        }
        //holder.name.setText(rowItem);

        convertView.setFocusable(true);

        convertView.setTag(rowItem);

        return convertView;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }
}
