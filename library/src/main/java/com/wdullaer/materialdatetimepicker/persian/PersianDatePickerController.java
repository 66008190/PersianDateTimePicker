package com.wdullaer.materialdatetimepicker.persian;

import com.wdullaer.materialdatetimepicker.date.DatePickerController;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.date.MonthAdapter;
import com.wdullaer.materialdatetimepicker.persian.utils.PersianCalendar;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created By ArashJahani on 2019/10/06
 */
public interface PersianDatePickerController extends DatePickerController {

    void onYearSelected(int year);

    void onDayOfMonthSelected(int year, int month, int day);

    void registerOnDateChangedListener(PersianDatePickerDialog.OnDateChangedListener listener);

    @SuppressWarnings("unused")
    void unregisterOnDateChangedListener(PersianDatePickerDialog.OnDateChangedListener listener);

    MonthAdapter.CalendarDay getSelectedDay();

    boolean isThemeDark();

    int getAccentColor();

    boolean isHighlighted(int year, int month, int day);

    int getFirstDayOfWeek();

    int getMinYear();

    int getMaxYear();

    PersianCalendar getStartDate();

    PersianCalendar getEndDate();

    boolean isOutOfRange(int year, int month, int day);

    void tryVibrate();

    TimeZone getTimeZone();

    Locale getLocale();

    PersianDatePickerDialog.Version getVersion();

    PersianDatePickerDialog.ScrollOrientation getScrollOrientation();

}
