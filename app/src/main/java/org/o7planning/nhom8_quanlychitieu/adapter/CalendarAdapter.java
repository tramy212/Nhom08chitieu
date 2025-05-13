package org.o7planning.nhom8_quanlychitieu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.o7planning.nhom8_quanlychitieu.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarAdapter extends BaseAdapter {

    private Context context;
    private List<Date> dates;
    private Calendar currentDate;
    private Date selectedDate;

    public CalendarAdapter(Context context, Calendar currentDate) {
        this.context = context;
        this.currentDate = currentDate;
        this.selectedDate = currentDate.getTime();
        this.dates = new ArrayList<>();
        refreshDates();
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
        notifyDataSetChanged();
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setCurrentDate(Calendar currentDate) {
        this.currentDate = currentDate;
        refreshDates();
    }

    private void refreshDates() {
        dates.clear();
        Calendar calendar = (Calendar) currentDate.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);
        // Adjust for Monday as first day of week
        int offset = firstDayOfMonth - Calendar.MONDAY;
        if (offset < 0) offset += 7;

        calendar.add(Calendar.DAY_OF_MONTH, -offset);

        // Add 42 days (6 weeks) to the calendar
        for (int i = 0; i < 42; i++) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false);
        }

        TextView tvDay = convertView.findViewById(R.id.tvDay);
        Date date = dates.get(position);

        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);

        // Set the day number
        tvDay.setText(String.valueOf(dateCal.get(Calendar.DAY_OF_MONTH)));

        // Check if this date is in the current month
        Calendar currentCal = (Calendar) currentDate.clone();
        if (dateCal.get(Calendar.MONTH) != currentCal.get(Calendar.MONTH)) {
            tvDay.setTextColor(Color.LTGRAY);
        } else {
            // Check if it's today
            Calendar today = Calendar.getInstance();
            if (dateCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    dateCal.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    dateCal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
                tvDay.setTextColor(Color.BLUE);
            } else if (dateCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                // Sunday
                tvDay.setTextColor(Color.RED);
            } else {
                tvDay.setTextColor(Color.BLACK);
            }
        }

        // Check if this date is selected
        Calendar selectedCal = Calendar.getInstance();
        selectedCal.setTime(selectedDate);
        if (dateCal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR) &&
                dateCal.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH) &&
                dateCal.get(Calendar.DAY_OF_MONTH) == selectedCal.get(Calendar.DAY_OF_MONTH)) {
            tvDay.setBackgroundResource(R.drawable.circle_background);
            tvDay.setBackgroundColor(Color.parseColor("#4FC3F7"));
            tvDay.setTextColor(Color.WHITE);
        } else {
            tvDay.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }
}
