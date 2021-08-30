package com.pfa.pfaapp.utils;

public class CustomDateUtils {

    //yyyy-MM-dd'T'HH:mm:ss.SSSZ  : 2016-04-07T05:38:02.341529Z
    //"EEE, MMM d, ''yy"    : Wed, Jul 4, '01
    //"MMM dd, yyyy"    : Jul 4, 2001


    public String getDateString(int day, int month, int year) {
        if (day == -1 || month == -1 || year == -1)
            return "";

        String dateStr = "";

        if (day < 10) {
            dateStr += "0" + day;
        } else {
            dateStr += "" + day;
        }

        if (month < 10) {
            dateStr += "-0" + month;
        } else {
            dateStr += "-" + month;
        }

        dateStr += ("-" + year);

        return dateStr;

    }

//    public long dateDifference(Date currentDate, Date date2) {
//
//        return currentDate.getTime() - date2.getTime();
//
//    }


//    private Date getPastDate(int numOfDays) {
//        // get Calendar instance
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date());
//        cal.add(Calendar.DAY_OF_YEAR, -(numOfDays));
//        // convert to date
//        Date myDate = cal.getTime();
//        try {
//            DateFormat targetFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
//            myDate = targetFormat.parse(targetFormat.format(myDate));
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return myDate;
//    }

//        public boolean isDateGreater(String minDate, String date, String maxDate) {
//        try {
//            DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//            Date date1 = targetFormat.parse(minDate);
//            Date date2 = targetFormat.parse(date);
//            Date max = targetFormat.parse(maxDate);
//
//            return (!date2.before(date1)) && (!date2.after(max));
//        } catch (ParseException e) {
//            e.printStackTrace();
////            printStackTrace(e);
//        }
//
//        return false;
//    }


//    public int getCurrentMonth() {
//        Date date = new Date();
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
//        return cal.get(Calendar.MONTH);
//    }

//    public String getMonthName(int pos) {
//        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
//        return months[pos];
//    }


}
