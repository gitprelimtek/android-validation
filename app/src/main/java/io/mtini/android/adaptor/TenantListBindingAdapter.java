package io.mtini.android.adaptor;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.prelimtek.android.validation.adapter.DatePickerBindingAdapter;
import com.prelimtek.android.validation.adapter.TextFinancialBigDecimalBindingAdapter;

import java.util.Comparator;
import java.util.List;

import io.mtini.android.tenantmanager.R;
import io.mtini.android.view.TextViewValueConverter;
import io.mtini.model.TenantModel;

public class TenantListBindingAdapter extends BaseAdapter {
    Context context;
    List<TenantModel> rowItems;
    View.OnClickListener onClickListener;
    public TenantListBindingAdapter(Context context, List<TenantModel> items, View.OnClickListener onClickListener) {
        this.context = context;
        this.rowItems = items;
        if(rowItems!=null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                rowItems.sort(new Comparator<TenantModel>() {
                    @Override
                    public int compare(TenantModel o1, TenantModel o2) {
                        return o1.getBalance().intValue() >= o2.getBalance().intValue() ? 0 : 1;
                    }
                });
            }
        }

        this.onClickListener = onClickListener;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView name;
        TextView roomNumber;
        TextView status;
        TextView dueDate;
        TextView balance;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.tenant_layout, null);
        holder = new ViewHolder();
        holder.roomNumber = (TextView) convertView.findViewById(R.id.roomNumber);
        holder.name = (TextView) convertView.findViewById(R.id.name);
        //holder.name.setOnClickListener(onClickListener);
        holder.status = (TextView) convertView.findViewById(R.id.status);
        holder.dueDate = (TextView) convertView.findViewById(R.id.dueDate);
        holder.balance = (TextView) convertView.findViewById(R.id.balance);
        //holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
        convertView.setOnClickListener(onClickListener);


        TenantModel rowItem = (TenantModel) getItem(position);

        holder.roomNumber.setText(rowItem.getBuildingNumber());
        holder.name.setText(rowItem.getName());
        holder.status.setText(rowItem.getStatus()==null?null:rowItem.getStatus().name());
        DatePickerBindingAdapter.setDateValue(holder.dueDate,rowItem.getDueDate());
        //holder.balance.setText(rowItem.getBalance()==null?null:rowItem.getBalance().toPlainString());
        TextFinancialBigDecimalBindingAdapter.setBindCurrencyValue(holder.balance,rowItem.getBalance());
        TextViewValueConverter.highlightBalance(holder.balance,rowItem.getBalance());
        //holder.imageView.setImageResource(rowItem.getImageId());//TODO set 1st image to background
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