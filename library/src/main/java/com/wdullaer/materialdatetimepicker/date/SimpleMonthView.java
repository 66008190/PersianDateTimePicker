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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.wdullaer.materialdatetimepicker.R;
import com.wdullaer.materialdatetimepicker.utils.PersianNumberUtils;

public class SimpleMonthView extends MonthView {

    public SimpleMonthView(Context context, AttributeSet attr, DatePickerController controller) {
        super(context, attr, controller);
    }

    @Override
    public void drawMonthDay(Canvas canvas, int year, int month, int day,
                             int x, int y, int startX, int stopX, int startY, int stopY) {
        if (mSelectedDay == day) {
            canvas.drawCircle(x, y - (MINI_DAY_NUMBER_TEXT_SIZE / 3), DAY_SELECTED_CIRCLE_SIZE,
                    mSelectedCirclePaint);
        }

        if (isHighlighted(year, month, day) && mSelectedDay != day) {
//            canvas.drawCircle(x, y + MINI_DAY_NUMBER_TEXT_SIZE - DAY_HIGHLIGHT_CIRCLE_MARGIN,
//                    DAY_HIGHLIGHT_CIRCLE_SIZE, mSelectedCirclePaint);
           // canvas.draw(getResources().getColor(R.color.mdtp_accent_color));
            //mMonthNumPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(startX,startY,stopX,stopY,mSelectedDaysBetweenTwoDates);
            mMonthNumPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        } else {
            mMonthNumPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        }

        // gray out the day number if it's outside the range.
        if (mController.isOutOfRange(year, month, day)) {
            mMonthNumPaint.setColor(mDisabledDayTextColor);
        } else if (mSelectedDay == day) {
            mMonthNumPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            mMonthNumPaint.setColor(mSelectedDayTextColor);
        } else if (mHasToday && mToday == day) {
            mMonthNumPaint.setColor(mTodayNumberColor);
        } else  if (isDisabledDay(year, month, day) && mSelectedDay != day) {
            mMonthNumPaint.setColor(mDisabledDayTextColor);
        }else {
            mMonthNumPaint.setColor(isHighlighted(year, month, day) ? mHighlightedDayTextColor : mDayTextColor);
        }

        canvas.drawText(PersianNumberUtils.toFarsi(day), x, y, mMonthNumPaint); //String.format(mController.getLocale(), "%d", day)
    }
}
