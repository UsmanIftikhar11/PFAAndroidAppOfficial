package com.pfa.pfaapp.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pfa.pfaapp.R;
import com.pfa.pfaapp.utils.AppUtils;

import java.util.List;
import java.util.Locale;

public class DropdownAdapter extends ArrayAdapter<String> {

    private LayoutInflater inflater;
    private List<String> originalData;
    private Context mContext;
    private AppUtils appUtils;

    /*************
     * CustomAdapter Constructor
     *****************/

    public DropdownAdapter(Context activitySpinner, List<String> objects) {
        super(activitySpinner, R.layout.spinner_layout, objects);

        this.mContext = activitySpinner;
        originalData = objects;
        appUtils = new AppUtils(mContext);

        inflater = (LayoutInflater) activitySpinner.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    private View getCustomView(int position, ViewGroup parent) {

        View row = inflater.inflate(R.layout.spinner_layout, parent, false);

        String tempValues = originalData.get(position);

        TextView spinnerTv = row.findViewById(R.id.spinnerTv);
        spinnerTv.setTextColor(mContext.getResources().getColor(R.color.black));

        // Set values for spinner each row
        spinnerTv.setText(String.format(Locale.getDefault(), "%s", tempValues));

        appUtils.applyFont(spinnerTv, AppUtils.FONTS.HelveticaNeueMedium);
        return row;
    }

}
