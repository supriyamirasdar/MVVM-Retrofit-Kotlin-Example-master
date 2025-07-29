package com.lifestyle.retail_dashboard.view.attendance.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lifestyle.retail_dashboard.R;
import com.lifestyle.retail_dashboard.view.attendance.model.AttendanceDetail;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class GridCellAdapterNew extends RecyclerView.Adapter<GridCellAdapterNew.ItemRowHolder> {

    private static final String tag = "Abrar";
    private final Context _context;
    private final Date todaysDate;
    private final String today;
    public static DecimalFormat decimalFormat = new DecimalFormat("00");
    private final List<String> list;
    private final List<String> actualDate = new ArrayList<>();
    private static final int DAY_OFFSET = 1;
    private final String[] weekdays = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private int daysInMonth;
    private int currentDayOfMonth;
    private int currentWeekDay;

    private final HashMap<String, Integer> eventsPerMonthMap;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
    private OnCalenderListener listener;
    private List<AttendanceDetail> attendanceDetails = new ArrayList<>();

    // Days in Current Month
    public GridCellAdapterNew(Context context, int month, int year, OnCalenderListener listener, List<AttendanceDetail> attendanceDetails) {
        super();
        this._context = context;
        this.list = new ArrayList<String>();
        this.listener = listener;
        this.attendanceDetails = attendanceDetails;
        Calendar calendar = Calendar.getInstance();
        setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

        Log.d("Abrar", "Attendance Data " + new Gson().toJson(attendanceDetails));

        todaysDate = Calendar.getInstance().getTime();
        today = dateFormatter.format(todaysDate);

        // Print Month
        printMonth(month, year);

        // Find Number of Events
        eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.screen_gridcell, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(ItemRowHolder row, int position) {
        // Get a reference to the Day gridcell


        // ACCOUNT FOR SPACING

        String[] day_color = list.get(position).split("-");
        String theday = day_color[0];
        String themonth = day_color[2];
        String theyear = day_color[3];
            /*if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
                if (eventsPerMonthMap.containsKey(theday)) {
                    num_events_per_day = (TextView) row
                            .findViewById(R.id.num_events_per_day);
                    Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
                    num_events_per_day.setText(numEvents.toString());
                }
            }*/

        // Set the Day GridCell
        row.gridcell.setText(theday);

        if (day_color[1].equals("GREY")) {
            row.gridcell.setTextColor(_context.getResources()
                    .getColor(R.color.light_grey));
            row.present.setVisibility(View.GONE);
            row.cell.setTag("");
        }
        if (day_color[1].equals("WHITE")) {
            row.gridcell.setTextColor(_context.getResources().getColor(R.color.text));
            row.cell.setTag("On" + theday + "-" + themonth + "-" + theyear + " Data not available");
        }

        if (actualDate.get(position).equalsIgnoreCase(today)) {
            row.gridcell.setTextColor(_context.getResources().getColor(R.color.blue_toggle));
            row.cell.setTag("On" + theday + "-" + themonth + "-" + theyear + " Data not available");
        }

        for (AttendanceDetail attendanceDetail : attendanceDetails) {
            if (attendanceDetail.getAttnDate() != null
                    && attendanceDetail.getAttnDate().equalsIgnoreCase(actualDate.get(position))) {
                if (attendanceDetail.getStatus().equalsIgnoreCase("NOT DEFND"))
                    row.present.setText("ND");
                else
                    row.present.setText(attendanceDetail.getStatus());
                Log.d("Abrar", "Status " + attendanceDetail.getStatus());
                if (attendanceDetail.getStatus().equalsIgnoreCase("P")) {
                    row.present.setTextColor(_context.getResources().getColor(R.color.green));
                    row.cell.setTag("On" + theday + "-" + themonth + "-" + theyear + " In-Time: " + attendanceDetail.getInTime() + " Out-Time:" + attendanceDetail.getOutTime());
                } else if (attendanceDetail.getStatus().equalsIgnoreCase("A")) {
                    row.present.setTextColor(_context.getResources().getColor(R.color.red));
                    row.cell.setTag("On" + theday + "-" + themonth + "-" + theyear + " is Absent");
                } else if (attendanceDetail.getStatus().equalsIgnoreCase("S")) {
                    row.present.setTextColor(_context.getResources().getColor(R.color.red));
                    row.cell.setTag("On" + theday + "-" + themonth + "-" + theyear + " In-Time: " + attendanceDetail.getInTime() + " Out-Time:" + attendanceDetail.getOutTime());
                } else if (attendanceDetail.getStatus().equalsIgnoreCase("L")) {
                    row.present.setTextColor(_context.getResources().getColor(R.color.red));
                    row.cell.setTag("On" + theday + "-" + themonth + "-" + theyear + " is on Leave");
                } else if (attendanceDetail.getStatus().equalsIgnoreCase("H")) {
                    row.present.setTextColor(_context.getResources().getColor(R.color.green));
                    row.cell.setTag("On" + theday + "-" + themonth + "-" + theyear + " is Holiday");
                } else if (attendanceDetail.getStatus().equalsIgnoreCase("WO")) {
                    row.present.setTextColor(_context.getResources().getColor(R.color.blue_toggle));
                    row.cell.setTag("On" + theday + "-" + themonth + "-" + theyear + " is Week Off");
                } else if (attendanceDetail.getStatus().equalsIgnoreCase("CO")) {
                    row.present.setTextColor(_context.getResources().getColor(R.color.green));
                    row.cell.setTag("On" + theday + "-" + themonth + "-" + theyear + " is Compensatory Off");
                } else if (attendanceDetail.getStatus().equalsIgnoreCase("HL")) {
                    row.present.setTextColor(_context.getResources().getColor(R.color.red));
                    row.cell.setTag("On" + theday + "-" + themonth + "-" + theyear + " In-Time: " + attendanceDetail.getInTime() + " Out-Time:" + attendanceDetail.getOutTime());
                } else if (attendanceDetail.getStatus().equalsIgnoreCase("HA")) {
                    row.present.setTextColor(_context.getResources().getColor(R.color.red));
                    row.cell.setTag("On" + theday + "-" + themonth + "-" + theyear + " In-Time: " + attendanceDetail.getInTime() + " Out-Time:" + attendanceDetail.getOutTime());
                } else if (attendanceDetail.getStatus().equalsIgnoreCase("OH")) {
                    row.present.setTextColor(_context.getResources().getColor(R.color.red));
                    row.cell.setTag("On" + theday + "-" + themonth + "-" + theyear + " is Optional Holiday");
                }
                row.present.setVisibility(View.VISIBLE);

                try {
                    Date parsedDate = dateFormatter.parse(actualDate.get(position));
                    assert parsedDate != null;
                    if (parsedDate.after(todaysDate)) {
                        //present.setVisibility(View.GONE);
                        //cell.setTag("");
                        // present.setBackgroundColor(_context.getResources().getColor(R.color.grey));
                        //gridcell.setBackgroundColor(_context.getResources().getColor(R.color.grey));
                        row.cell.setBackgroundColor(_context.getResources().getColor(R.color.blue_50));
                        //present.setTextColor(_context.getResources().getColor(R.color.light_blue_300));
                    }
                } catch (ParseException e) {
                    Log.d("Abrar", "Exception: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }

            }
        }


        row.cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date_month_year = (String) view.getTag();
                listener.onDateClicked(date_month_year);
        /*Log.e("Selected date", date_month_year);
        Toast.makeText(_context, "Selected is " + date_month_year, Toast.LENGTH_SHORT).show();
        try {
            Date parsedDate = dateFormatter.parse(date_month_year);
            Log.d(tag, "Parsed Date: " + parsedDate.toString());

        } catch (ParseException e) {
            Log.d("Abrar", "Exception: " + e.getLocalizedMessage());
            e.printStackTrace();
        }*/
            }
        });
        /*cell.setOnClickListener(view -> {
            Log.d("Abrar","Clicked: ")
            if (present.getText().toString().equalsIgnoreCase("P")) {
                listener.onDateClicked(tvTime.getText().toString());
            }else
                listener.onDateClicked(tvTime.getText().toString());
        });*/

            /*if (parsedDate.getTime() > System.currentTimeMillis()){
                Log.d("Abrar","Match: "+System.currentTimeMillis());
            }*/


        /*if (absentList.contains(actualDate.get(position))) {
            present.setTextColor(_context.getResources().getColor(R.color.green));
            present.setText("A");
            present.setVisibility(View.VISIBLE);
        } else {
            present.setTextColor(Color.RED);
            present.setText("A");
            present.setVisibility(View.VISIBLE);
        }*/

        //Log.d("Abrar", "Actual Date: " + actualDate.get(position));

        try {
            Date parsedDate = dateFormatter.parse(actualDate.get(position));
            assert parsedDate != null;
            // commented as per KR told.. show full data in attandance detaill.. Date 05-02-2021
            /*if (parsedDate.after(todaysDate)) {
                present.setVisibility(View.GONE);
                cell.setTag("");
            }*/

            /*if (parsedDate.after(todaysDate)) {
                //present.setVisibility(View.GONE);
                //cell.setTag("");
               // present.setBackgroundColor(_context.getResources().getColor(R.color.grey));
                //gridcell.setBackgroundColor(_context.getResources().getColor(R.color.grey));
                cell.setBackgroundColor(_context.getResources().getColor(R.color.grey));
            }*/
        } catch (ParseException e) {
            Log.d("Abrar", "Exception: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        /*for (String presentDate : items) {
            Log.d("Abrar", "Present Day:" + presentDate + " date " + actualDate.get(position));
            if (presentDate.equalsIgnoreCase(actualDate.get(position))) {
                gridcell.setBackgroundColor(Color.GREEN);
                present.setVisibility(View.VISIBLE);
            } else
                present.setVisibility(View.GONE);
        }*/

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        private TextView gridcell;
        private ConstraintLayout cell;
        private TextView present;

        private ItemRowHolder(View view) {
            super(view);
            gridcell = view.findViewById(R.id.tvDate);
            present = view.findViewById(R.id.present);
            cell = view.findViewById(R.id.cell);
        }
    }

    /**
     * Prints Month
     *
     * @param mm
     * @param yy
     */
    private void printMonth(int mm, int yy) {
        int trailingSpaces = 0;
        int daysInPrevMonth = 0;
        int prevMonth = 0;
        int prevYear = 0;
        int nextMonth = 0;
        int nextYear = 0;

        int currentMonth = mm - 1;
        String currentMonthName = getMonthAsString(currentMonth);
        daysInMonth = getNumberOfDaysOfMonth(currentMonth);

        GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);

        if (currentMonth == 11) {
            prevMonth = currentMonth - 1;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            nextMonth = 0;
            prevYear = yy;
            nextYear = yy + 1;
        } else if (currentMonth == 0) {
            prevMonth = 11;
            prevYear = yy - 1;
            nextYear = yy;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            nextMonth = 1;
        } else {
            prevMonth = currentMonth - 1;
            nextMonth = currentMonth + 1;
            nextYear = yy;
            prevYear = yy;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
        }

        int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
        trailingSpaces = currentWeekDay;

        if (cal.isLeapYear(cal.get(Calendar.YEAR)))
            if (mm == 2)
                ++daysInMonth;
            else if (mm == 3)
                ++daysInPrevMonth;

        // Trailing Month days
        for (int i = 0; i < trailingSpaces; i++) {
            list.add(decimalFormat.format((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
                    + i)
                    + "-GREY"
                    + "-"
                    + getMonthAsString(prevMonth)
                    + "-"
                    + prevYear);

            actualDate.add(decimalFormat.format((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i)
                    + "-"
                    + getMonthAsString(prevMonth)
                    + "-"
                    + prevYear);
        }

        // Current Month Days
        for (int i = 1; i <= daysInMonth; i++) {
            /*if (i == getCurrentDayOfMonth()) {
                list.add(decimalFormat.format(i) + "-BLUE" + "-"
                        + getMonthAsString(currentMonth) + "-" + yy);

                actualDate.add(decimalFormat.format(i) +  "-"
                        + getMonthAsString(currentMonth) + "-" + yy);

            } else {*/
            list.add(decimalFormat.format(i) + "-WHITE" + "-"
                    + getMonthAsString(currentMonth) + "-" + yy);

            actualDate.add(decimalFormat.format(i) + "-"
                    + getMonthAsString(currentMonth) + "-" + yy);
            //}
        }

        // Leading Month days
        for (int i = 0; i < list.size() % 7; i++) {
            list.add(decimalFormat.format(i + 1) + "-GREY" + "-"
                    + getMonthAsString(nextMonth) + "-" + nextYear);
            actualDate.add(decimalFormat.format(i + 1) + "-"
                    + getMonthAsString(nextMonth) + "-" + nextYear);
        }
    }

    private HashMap<String, Integer> findNumberOfEventsPerMonth(int year,
                                                                int month) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        return map;
    }

    private String getMonthAsString(int i) {
        return months[i];
    }

    private String getWeekDayAsString(int i) {
        return weekdays[i];
    }

    private int getNumberOfDaysOfMonth(int i) {
        return daysOfMonth[i];
    }

    public String getItem(int position) {
        return list.get(position);
    }

    public int getCurrentDayOfMonth() {
        return currentDayOfMonth;
    }

    private void setCurrentDayOfMonth(int currentDayOfMonth) {
        this.currentDayOfMonth = currentDayOfMonth;
    }

    public void setCurrentWeekDay(int currentWeekDay) {
        this.currentWeekDay = currentWeekDay;
    }

    public int getCurrentWeekDay() {
        return currentWeekDay;
    }
}
