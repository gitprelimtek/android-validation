package io.mtini.model;


import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Constants {

    public static SimpleDateFormat getDateFormat(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 'T' HH;mm:ss");


        return format;
    }




}
