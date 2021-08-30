package com.pfa.pfaapp.customviews;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.pfa.pfaapp.R;
import com.pfa.pfaapp.interfaces.GetDateCallback;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateCustomDialog {
    //        calendarType = {present, past,all,date_from,date_to}
    public static void showDatePickerDialog(final Context context, final GetDateCallback callback, String dateType, String date_from, String date_to, String inputDate) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.datepicker_layout);

        SharedPrefUtils sharedPrefUtils= new SharedPrefUtils(context);

        final DatePicker datePicker = dialog.findViewById(R.id.datePicker);

        String[] splitArray = null;
        if (inputDate != null && (!inputDate.isEmpty())) {
            splitArray = inputDate.split("-");
        }

        if ((date_from != null && (!date_from.isEmpty())) || (date_to != null && (!date_to.isEmpty()))) {
            SimpleDateFormat f = new SimpleDateFormat("dd-mm-yyyy", Locale.getDefault());
            try {
                if (date_from != null && !date_from.isEmpty()) {
                    String[] dateFromSplitArray = date_from.split("-");
                    if (splitArray == null)
                        splitArray = dateFromSplitArray;


                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, Integer.parseInt(dateFromSplitArray[2]));
                    calendar.set(Calendar.MONTH, Integer.parseInt(dateFromSplitArray[1]) - 1);
                    calendar.set(Calendar.DATE, Integer.parseInt(dateFromSplitArray[0]));

                    datePicker.setMinDate(calendar.getTimeInMillis()); // end date

                }

                if (date_to != null && !date_to.isEmpty()) {
                    Date toDate = f.parse(date_to);
                    datePicker.setMaxDate(toDate.getTime()); // end date
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            if (dateType == null || dateType.equalsIgnoreCase("past")) {
                datePicker.setMaxDate(System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 365 * 17)); // min age 17 years from current date
                datePicker.setMinDate(System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 365 * 90)); // max 90 years age from current date
            } else if (dateType.equalsIgnoreCase("present")) {
                datePicker.setMinDate(System.currentTimeMillis() - 10000);
            } else {
                sharedPrefUtils.printLog("Calendar Type== all", "Show whole calendar");
            }
        }


        if (splitArray != null && splitArray.length == 3)
            datePicker.init(Integer.parseInt(splitArray[2]), Integer.parseInt(splitArray[1]) - 1, Integer.parseInt(splitArray[0]), null);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // do something for phones running an SDK before lollipop
            // e.g. datePicker.getCalendarView() ...
            datePicker.getCalendarView().setVisibility(View.GONE);
        }

        Button setDateBtn = dialog.findViewById(R.id.setDateBtn);
        setDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();

                callback.onDateSelected(day, month, year);
                dialog.dismiss();
            }
        });

        Button cancelBTn = dialog.findViewById(R.id.cancelBTn);

        cancelBTn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onDateSelected(-1, -1, -1);
                dialog.dismiss();
            }
        });


        sharedPrefUtils.applyFont(setDateBtn,AppUtils.FONTS.HelveticaNeueMedium);
        sharedPrefUtils.applyFont(cancelBTn,AppUtils.FONTS.HelveticaNeueMedium);

        dialog.show();

    }


}
