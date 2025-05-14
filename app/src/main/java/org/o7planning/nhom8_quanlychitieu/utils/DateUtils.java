package org.o7planning.nhom8_quanlychitieu.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public String getMonthName(int month) {
        String[] monthNames = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
        return monthNames[month - 1]; // Adjust for 0-based array
    }

    public static boolean isFromMonth(String dateStr, int month, int year) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int dateMonth = cal.get(Calendar.MONTH) + 1; // Calendar months are 0-based
            int dateYear = cal.get(Calendar.YEAR);

            return dateMonth == month && dateYear == year;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
