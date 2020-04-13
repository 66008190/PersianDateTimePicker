/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wdullaer.materialdatetimepicker.date;

import com.wdullaer.materialdatetimepicker.utils.PersianCalendar;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Controller class to communicate among the various components of the date picker dialog.
 */
public interface DatePickerController {

    void onYearSelected(int year);

    void onDayOfMonthSelected(int year, int month, int day);

    void registerOnDateChangedListener(PersianDatePickerDialog.OnDateChangedListener listener);

    @SuppressWarnings("unused")
    void unregisterOnDateChangedListener(PersianDatePickerDialog.OnDateChangedListener listener);

    MonthAdapter.CalendarDay getSelectedDay();

    boolean isThemeDark();

    int getAccentColor();

    int getStartDateColor();

    int getFinishDateColor();

    int getHighlightColor();

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

    boolean isDisabled(int year, int month, int day);

    PersianDatePickerDialog.Version getVersion();

    PersianDatePickerDialog.ScrollOrientation getScrollOrientation();

    boolean isRangDatePickerEnable();

    PersianCalendar getRangeDatePickerStartDate();

    void setRangeDatePickerStartDate(int year, int month, int mSelectedDay);

    PersianCalendar getRangeDatePickerFinishDate();

    void setRangeDatePickerFinishDate(int year, int month, int mSelectedDay);

    boolean getRangeDatePickerStartIsEqualWith(int year, int month, int mSelectedDay);

    boolean getRangeDatePickerFinishIsEqualWith(int year, int month, int mSelectedDay);

    void clearRangeDatePickerFinishDate();

    void clearRangeDatePickerStartDate();

    boolean isUserTapedOnDay();

    void setUserTapedOnDay(boolean b);
}
