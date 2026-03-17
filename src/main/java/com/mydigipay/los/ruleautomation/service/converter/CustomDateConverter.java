package com.mydigipay.los.ruleautomation.service.converter;

import com.github.eloyzone.jalalicalendar.DateConverter;
import com.github.eloyzone.jalalicalendar.JalaliDate;
import com.github.eloyzone.jalalicalendar.JalaliDateFormatter;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Author: f.bahramnejad
 */
public class CustomDateConverter {

    private static Date stringToDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

// Parse the string to a Date object
        return sdf.parse(dateString);
    }

    public static long jalaliToTimeStamp(String date) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(stringToDate(date));
        DateConverter dateConverter = new DateConverter();
        LocalDate gregorianDate = dateConverter.jalaliToGregorian(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime dateTime = gregorianDate.atStartOfDay();
        long epochSeconds = dateTime.toEpochSecond(zoneId.getRules().getOffset(dateTime));
        return epochSeconds * 1000;
    }

    public static String jalaliToGregorian(String date) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(stringToDate(date));
        DateConverter dateConverter = new DateConverter();
        return dateConverter.jalaliToGregorian(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public static String getTodayInJalali() {
        DateConverter dateConverter = new DateConverter();
        LocalDate localDate = LocalDate.now();
        JalaliDate jalaliDate = dateConverter.gregorianToJalali(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth());
        return jalaliDate.format(new JalaliDateFormatter("yyyy/mm/dd", JalaliDateFormatter.FORMAT_IN_ENGLISH));
    }

}
