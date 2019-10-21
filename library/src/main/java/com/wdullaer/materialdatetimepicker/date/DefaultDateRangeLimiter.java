/*
 * Copyright (C) 2017 Wouter Dullaert
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

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wdullaer.materialdatetimepicker.Utils;
import com.wdullaer.materialdatetimepicker.utils.PersianCalendar;
import com.wdullaer.materialdatetimepicker.utils.PersianNumberUtils;

import java.util.Calendar;
import java.util.HashSet;
import java.util.TimeZone;
import java.util.TreeSet;

public class DefaultDateRangeLimiter implements DateRangeLimiter {
    private static final int DEFAULT_START_YEAR = 1300;
    private static final int DEFAULT_END_YEAR = 1410;

    public transient DatePickerController mController;
    private int mMinYear = DEFAULT_START_YEAR;
    private int mMaxYear = DEFAULT_END_YEAR;
    private PersianCalendar mMinDate;
    private PersianCalendar mMaxDate;
    private TreeSet<PersianCalendar> selectableDays = new TreeSet<>();
    private HashSet<PersianCalendar> disabledDays = new HashSet<>();

    public DefaultDateRangeLimiter() {}

    @SuppressWarnings({"unchecked", "WeakerAccess"})
    public DefaultDateRangeLimiter(Parcel in) {
        mMinYear = in.readInt();
        mMaxYear = in.readInt();
        mMinDate = (PersianCalendar) in.readSerializable();
        mMaxDate = (PersianCalendar) in.readSerializable();
        selectableDays = (TreeSet<PersianCalendar>) in.readSerializable();
        disabledDays = (HashSet<PersianCalendar>) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mMinYear);
        out.writeInt(mMaxYear);
        out.writeSerializable(mMinDate);
        out.writeSerializable(mMaxDate);
        out.writeSerializable(selectableDays);
        out.writeSerializable(disabledDays);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("WeakerAccess")
    public static final Parcelable.Creator<DefaultDateRangeLimiter> CREATOR
            = new Parcelable.Creator<DefaultDateRangeLimiter>() {
        public DefaultDateRangeLimiter createFromParcel(Parcel in) {
            return new DefaultDateRangeLimiter(in);
        }

        public DefaultDateRangeLimiter[] newArray(int size) {
            return new DefaultDateRangeLimiter[size];
        }
    };

    public void setSelectableDays(@NonNull Calendar[] days) {
        for (Calendar selectableDay : days) {
            this.selectableDays.add((PersianCalendar) selectableDay.clone());
        }
    }

    public void setDisabledDays(@NonNull Calendar[] days) {
        for (Calendar disabledDay : days) {
            this.disabledDays.add((PersianCalendar) disabledDay.clone());
        }
    }

    public void setMinDate(@NonNull Calendar calendar) {
        mMinDate = (PersianCalendar) calendar.clone();
    }

    public void setMaxDate(@NonNull Calendar calendar) {
        mMaxDate = (PersianCalendar) calendar.clone();
    }

    public void setController(@NonNull DatePickerController controller) {
        mController = controller;
    }

    public void setYearRange(int startYear, int endYear) {
        if (endYear < startYear) {
            throw new IllegalArgumentException("Year end must be larger than or equal to year start");
        }

        mMinYear = startYear;
        mMaxYear = endYear;
    }

    public @Nullable PersianCalendar getMinDate() {
        return mMinDate;
    }

    public @Nullable PersianCalendar getMaxDate() {
        return mMaxDate;
    }

    public @Nullable PersianCalendar[] getSelectableDays() {
         return selectableDays.isEmpty() ? null : selectableDays.toArray(new PersianCalendar[0]);
    }

    public @Nullable PersianCalendar[] getDisabledDays() {
        return disabledDays.isEmpty() ? null : disabledDays.toArray(new PersianCalendar[0]);
    }

    @Override
    public int getMinYear() {
        if (!selectableDays.isEmpty()) return selectableDays.first().get(Calendar.YEAR);
        // Ensure no years can be selected outside of the given minimum date
        return mMinDate != null && mMinDate.get(Calendar.YEAR) > mMinYear ? mMinDate.get(Calendar.YEAR) : mMinYear;
    }

    @Override
    public int getMaxYear() {
        if (!selectableDays.isEmpty()) return selectableDays.last().get(Calendar.YEAR);
        // Ensure no years can be selected outside of the given maximum date
        return mMaxDate != null && mMaxDate.get(Calendar.YEAR) < mMaxYear ? mMaxDate.get(Calendar.YEAR) : mMaxYear;
    }

    @Override
    public @NonNull PersianCalendar getStartDate() {
        if (!selectableDays.isEmpty()) return (PersianCalendar) selectableDays.first().clone();
        if (mMinDate != null) return (PersianCalendar) mMinDate.clone();
        TimeZone timeZone = mController == null ? TimeZone.getDefault() : mController.getTimeZone();
        PersianCalendar output = new PersianCalendar();
        output.setPersianDate( mMinYear,1,Calendar.JANUARY);
        return output;
    }

    @Override
    public @NonNull
    PersianCalendar getEndDate() {
        if (!selectableDays.isEmpty()) return (PersianCalendar) selectableDays.last().clone();
        if (mMaxDate != null) return (PersianCalendar) mMaxDate.clone();
        TimeZone timeZone = mController == null ? TimeZone.getDefault() : mController.getTimeZone();
        PersianCalendar output = new PersianCalendar();
        output.setPersianDate( mMaxYear,Calendar.DECEMBER,31);
        return output;
    }

    /**
     * @return true if the specified year/month/day are within the selectable days or the range set by minDate and maxDate.
     * If one or either have not been set, they are considered as Integer.MIN_VALUE and
     * Integer.MAX_VALUE.
     */
    @Override
    public boolean isOutOfRange(int year, int month, int day) {
        TimeZone timezone = mController == null ? TimeZone.getDefault() : mController.getTimeZone();
        PersianCalendar date = new PersianCalendar();
        date.setPersianDate( year, month, day);
        return isOutOfRange(date);
    }

    private boolean isOutOfRange(@NonNull PersianCalendar calendar) {
        Utils.trimToMidnight(calendar);
        return isDisabled(calendar) || !isSelectable(calendar);
    }

    private boolean isDisabled(@NonNull PersianCalendar c) {
        return disabledDays.contains( isBeforeMin(c) || isAfterMax(c));
    }

    private boolean isSelectable(@NonNull PersianCalendar c) {
        return selectableDays.isEmpty() || selectableDays.contains(Utils.trimToMidnight(c));
    }

    private boolean isBeforeMin(@NonNull PersianCalendar calendar) {
        return mMinDate != null && calendar.before(mMinDate) || calendar.getPersianYear() < mMinYear;
    }

    private boolean isAfterMax(@NonNull PersianCalendar calendar) {
        return mMaxDate != null && calendar.after(mMaxDate) || calendar.getPersianYear() > mMaxYear;
    }

    @Override
    public @NonNull PersianCalendar setToNearestDate(@NonNull PersianCalendar calendar) {
        if (!selectableDays.isEmpty()) {
            PersianCalendar newCalendar = null;
            PersianCalendar higher = selectableDays.ceiling(calendar);
            PersianCalendar lower = selectableDays.lower(calendar);

            if (higher == null && lower != null) newCalendar = lower;
            else if (lower == null && higher != null) newCalendar = higher;

            if (newCalendar != null || higher == null) {
                newCalendar = newCalendar == null ? calendar : newCalendar;
                TimeZone timeZone = mController == null ? TimeZone.getDefault() : mController.getTimeZone();
                newCalendar.setTimeZone(timeZone);
                return (PersianCalendar) newCalendar.clone();
            }

            long highDistance = Math.abs(higher.getTimeInMillis() - calendar.getTimeInMillis());
            long lowDistance = Math.abs(calendar.getTimeInMillis() - lower.getTimeInMillis());

            if (lowDistance < highDistance) return (PersianCalendar) lower.clone();
            else return (PersianCalendar) higher.clone();
        }

        if (!disabledDays.isEmpty()) {
            PersianCalendar forwardDate = isBeforeMin(calendar) ? getStartDate() : (PersianCalendar) calendar.clone();
            PersianCalendar backwardDate = isAfterMax(calendar) ? getEndDate() : (PersianCalendar) calendar.clone();
            while (isDisabled(forwardDate) && isDisabled(backwardDate)) {
                forwardDate.add(Calendar.DAY_OF_MONTH, 1);
                backwardDate.add(Calendar.DAY_OF_MONTH, -1);
            }
            if (!isDisabled(backwardDate)) {
                return backwardDate;
            }
            if (!isDisabled(forwardDate)) {
                return forwardDate;
            }
        }

        TimeZone timezone = mController == null ? TimeZone.getDefault() : mController.getTimeZone();
        if (isBeforeMin(calendar)) {
            if (mMinDate != null) return (PersianCalendar) mMinDate.clone();
            PersianCalendar output = new PersianCalendar();
            output.setPersianDate( mMinYear, Calendar.JANUARY, 1);
            return output; //Utils.trimToMidnight(
        }

        if (isAfterMax(calendar)) {
            if (mMaxDate != null) return (PersianCalendar) mMaxDate.clone();
            PersianCalendar output = new PersianCalendar();
            output.setPersianDate(mMaxYear, Calendar.DECEMBER, 31);
            return output; //Utils.trimToMidnight(
        }

        return calendar;
    }
}