package com.pfa.pfaapp.adapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.pfa.pfaapp.R;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchDDAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private List<String> originalList;
    private AppUtils appUtils;
    private List<String> filterList = new ArrayList<>();
    private Filter filter = new CustomFilter();
    private SendMessageCallback sendMessageCallback;
    private boolean isClicked = false;

    public SearchDDAdapter(Context mContext, List<String> data, SendMessageCallback sendMessageCallback) {
        this.mContext = mContext;
        this.originalList = data;
        this.sendMessageCallback = sendMessageCallback;
        appUtils = new AppUtils(mContext);
        filterList.clear();
        filterList.addAll(originalList);
    }

    @Override
    public int getCount() {
        return filterList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SearchDDHolder fboCheckViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.fbo_check_list_item, parent, false);
            fboCheckViewHolder = new SearchDDHolder(convertView);
            convertView.setTag(fboCheckViewHolder);
        } else {
            fboCheckViewHolder = (SearchDDHolder) convertView.getTag();
        }
        fboCheckViewHolder.fboCheckTtlTV.setText(String.format(Locale.getDefault(), "%d.  %s", (position + 1), filterList.get(position)));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isClicked) {
                    return;
                }

                isClicked = true;

                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isClicked = false;
                    }
                }, 100);
                if (filterList.size() <= position)
                    return;
                sendMessageCallback.sendMsg("" + (getSelectedItemPosition(filterList.get(position))));
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    class SearchDDHolder {
        TextView fboCheckTtlTV;

        SearchDDHolder(View view) {
            fboCheckTtlTV = view.findViewById(R.id.fboCheckTtlTV);
            appUtils.applyFont(fboCheckTtlTV, AppUtils.FONTS.HelveticaNeue);
        }
    }

    /**
     * Our Custom Filter Class.
     */
    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            filterList.clear();
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() < 1) {
                filterList.addAll(originalList);

                results.values = filterList;
                results.count = filterList.size();
                return results;
            }

            if (originalList != null) { // Check if the Original List and Constraint aren't null.
                for (int i = 0; i < originalList.size(); i++) {
                    if (originalList.get(i).toLowerCase().contains(constraint.toString().toLowerCase())) {
                        // Compare item in original list if it contains constraints.
                        filterList.add(originalList.get(i)); // If TRUE add item in Suggestions.
                    }
                }
            }
            results.values = filterList;
            results.count = filterList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    private int getSelectedItemPosition(String currentClickedStr) {
        int clickedPos = -1;
        if (originalList != null) {
            for (int i = 0; i < originalList.size(); i++) {
                if (originalList.get(i).equalsIgnoreCase(currentClickedStr)) {
                    clickedPos = i;
                }
            }
        }
        return clickedPos;
    }
}
