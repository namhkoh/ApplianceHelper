package com.unity3d.player;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeHandler {
    static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String formattedDate = dateFormat.format(date);
        return formattedDate;
    }

    static String stringToDate(Date date_){

        String result = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        result  = dateFormat.format(date_);
        return result ;
    }

    static Date convertDate(String hours,String minutes){

        Date result = null;
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
            StringBuilder sb = new StringBuilder();
            sb.append(hours).append(":").append(minutes);
            result  = dateFormat.parse(sb.toString());
        }

        catch(java.text.ParseException e){
            e.printStackTrace();

        }
        return result ;
    }

    static String setNewTime(String hours,String minutes) {
        //Date date = convertDate("05:00");
        Date date = convertDate(hours,minutes);
        String val = stringToDate(date);
        Log.e("new time",val);
        return val;
    }
}
