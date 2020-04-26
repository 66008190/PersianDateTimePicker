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

package ir.arashjahani.persiandatetimepicker.date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import ir.arashjahani.persiandatetimepicker.utils.PersianNumberUtils;

public class SimpleMonthView extends MonthView {

    public SimpleMonthView(Context context, AttributeSet attr, DatePickerController controller) {
        super(context, attr, controller);
    }

    @Override
    public void drawMonthDay(Canvas canvas, int year, int month, int day,
                             int x, int y, int startX, int stopX, int startY, int stopY) {
        if (mSelectedDay == day) {
            if (!mController.isRangDatePickerEnable()) {

                canvas.drawCircle(x, y - (MINI_DAY_NUMBER_TEXT_SIZE / 3) - 3, DAY_SELECTED_CIRCLE_SIZE,
                        mSelectedCirclePaint);
            }
        }


        if (isHighlighted(year, month, day)) {

            if (mController.isRangDatePickerEnable()) {

                if (mController.getRangeDatePickerStartDate() != null && mController.getRangeDatePickerStartIsEqualWith(year, month, day)) {

                    canvas.drawRect(startX, startY + 8, stopX - 40, stopY - 7, mSelectedDaysBetweenTwoDates);
                } else if (mController.getRangeDatePickerFinishDate() != null && mController.getRangeDatePickerFinishIsEqualWith(year, month, day)) {

                    canvas.drawRect(startX + 40, startY + 8, stopX, stopY - 7, mSelectedDaysBetweenTwoDates);
                }else{
                    canvas.drawRect(startX, startY + 8, stopX, stopY - 7, mSelectedDaysBetweenTwoDates);
                }
            } else {
                canvas.drawRect(startX, startY + 8, stopX, stopY - 7, mSelectedDaysBetweenTwoDates);
            }
            mMonthNumPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        } else {
            mMonthNumPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        }

        // gray out the day number if it's outside the range.
        if (mController.isOutOfRange(year, month, day)) {
            mMonthNumPaint.setColor(mDisabledDayTextColor);
        } else if (mController.isRangDatePickerEnable() && mController.getRangeDatePickerStartDate() != null && mController.getRangeDatePickerStartIsEqualWith(year, month, day)) {
            mMonthNumPaint.setColor(mSelectedDayTextColor);
        } else if (mController.isRangDatePickerEnable() && mController.getRangeDatePickerFinishDate() != null && mController.getRangeDatePickerFinishIsEqualWith(year, month, day)) {
            mMonthNumPaint.setColor(mDayTextColor);
        } else if (mSelectedDay == day && !mController.isRangDatePickerEnable()) {
            mMonthNumPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            mMonthNumPaint.setColor(mSelectedDayTextColor);
        } else if (mHasToday && mToday == day) {
            mMonthNumPaint.setColor(mTodayNumberColor);
        } else if (

                isDisabledDay(year, month, day) && mSelectedDay != day) {
            mMonthNumPaint.setColor(mDisabledDayTextColor);
        } else {
            mMonthNumPaint.setColor(isHighlighted(year, month, day) ? mHighlightedDayTextColor : mDayTextColor);
        }

        if (mController.isRangDatePickerEnable()) {

            if (mController.getRangeDatePickerStartDate() != null && mController.getRangeDatePickerStartIsEqualWith(year, month, day)) {

                canvas.drawCircle(x, y - (MINI_DAY_NUMBER_TEXT_SIZE / 3) - 3, DAY_SELECTED_CIRCLE_SIZE,
                        mSelectedCirclePaint);
            }
            if (mController.getRangeDatePickerFinishDate() != null && mController.getRangeDatePickerFinishIsEqualWith(year, month, day)) {

                canvas.drawCircle(x, y - (MINI_DAY_NUMBER_TEXT_SIZE / 3) - 3, DAY_SELECTED_CIRCLE_SIZE,
                        mSelectedBorderCirclePaint);
            }

        }

        canvas.drawText(PersianNumberUtils.toFarsi(day), x, y, mMonthNumPaint); //String.format(mController.getLocale(), "%d", day)
    }
}
