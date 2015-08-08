package org.angmarch.views;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * @author angelo.marchesin
 */
public class NiceSpinnerAdapter<T> extends BaseAdapter {

    private final List<T> mItems;
    private final Context mContext;
    protected int mSelectedIndex;

    public NiceSpinnerAdapter(Context context, List<T> items) {
        mItems = items;
        mContext = context;
    }

    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.spinner_list_item, null);
            textView = (TextView) convertView.findViewById(R.id.tv_tinted_spinner);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                textView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.selector));
            }

            convertView.setTag(new ViewHolder(textView));
        } else {
            textView = ((ViewHolder) convertView.getTag()).textView;
        }

        textView.setText(getItem(position).toString());

        return convertView;
    }

    @Override
    public int getCount() {
        return mItems.size() - 1;
    }

    @Override
    public T getItem(int position) {
        if (position >= mSelectedIndex) {
            return mItems.get(position + 1);
        } else {
            return mItems.get(position);
        }
    }

    public T getItemInDataset(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void notifyItemSelected(int index) {
        mSelectedIndex = index;
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    private static class ViewHolder {

        public TextView textView;

        public ViewHolder(TextView textView) {
            this.textView = textView;
        }
    }
}