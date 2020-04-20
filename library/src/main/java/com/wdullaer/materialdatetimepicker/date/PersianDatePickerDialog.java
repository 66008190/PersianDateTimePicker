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

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.wdullaer.materialdatetimepicker.HapticFeedbackController;
import com.wdullaer.materialdatetimepicker.R;
import com.wdullaer.materialdatetimepicker.Utils;
import com.wdullaer.materialdatetimepicker.utils.PersianCalendar;
import com.wdullaer.materialdatetimepicker.utils.PersianCalendarUtils;
import com.wdullaer.materialdatetimepicker.utils.PersianNumberUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Dialog allowing users to select a date.
 */
public class PersianDatePickerDialog extends AppCompatDialogFragment implements
        OnClickListener, DatePickerController {

    public enum Version {
        VERSION_1,
        VERSION_2
    }

    public enum ScrollOrientation {
        HORIZONTAL,
        VERTICAL
    }

    public static final int UNINITIALIZED = -1;
    public static final int MONTH_AND_DAY_VIEW = 0;
    public static final int YEAR_VIEW = 1;

    public static final String KEY_SELECTED_YEAR = "year";
    public static final String KEY_SELECTED_MONTH = "month";
    public static final String KEY_SELECTED_DAY = "day";
    public static final String KEY_LIST_POSITION = "list_position";
    public static final String KEY_WEEK_START = "week_start";
    public static final String KEY_CURRENT_VIEW = "current_view";
    public static final String KEY_LIST_POSITION_OFFSET = "list_position_offset";
    public static final String KEY_HIGHLIGHTED_DAYS = "highlighted_days";
    public static final String KEY_DISABLED_DAYS = "disabled_days";
    public static final String KEY_ACCENT = "accent";
    public static final String KEY_VIBRATE = "vibrate";
    public static final String KEY_DISMISS = "dismiss";
    public static final String KEY_AUTO_DISMISS = "auto_dismiss";
    public final String KEY_DEFAULT_VIEW = "default_view";
    public static final String KEY_TITLE = "title";
    public static final String KEY_OK_RESID = "ok_resid";
    public static final String KEY_RANGE_PICKER_RESULT_HINT = "range_picker_result_hint";
    public static final String KEY_OK_STRING = "ok_string";
    public static final String KEY_TITLE_STRING = "title_string";
    public static final String KEY_OK_COLOR = "ok_color";
    public static final String KEY_OK_BACKGROUND_COLOR = "ok_background_color";
    public static final String KEY_VERSION = "version";
    public static final String KEY_TIMEZONE = "timezone";
    public static final String KEY_DATERANGELIMITER = "daterangelimiter";
    public static final String KEY_SCROLL_ORIENTATION = "scrollorientation";
    public static final String KEY_LOCALE = "locale";

    public static final String KEY_HIGHLIGHT_COLOR = "highlight_color";
    public static final String KEY_START_COLOR = "start_color";
    public static final String KEY_FINISH_COLOR = "finish_color";

    public static final String KEY_RANGE_DATE_PICKER = "range_date_picker";
    public static final String KEY_RANGE_DATE_PICKER_START = "range_date_picker_start";
    public static final String KEY_RANGE_DATE_PICKER_FINITSH = "range_date_picker_finish";
    public static final String KEY_USER_TAPPED = "user_tapped";


    public static final int ANIMATION_DURATION = 300;
    public static final int ANIMATION_DELAY = 500;

    private static SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy", Locale.getDefault());
    private static SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("MMM", Locale.getDefault());
    private static SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd", Locale.getDefault());
    public static SimpleDateFormat VERSION_2_FORMAT;

    public PersianCalendar mCalendar = new PersianCalendar();
    public OnDateSetListener mCallBack;
    public HashSet<OnDateChangedListener> mListeners = new HashSet<>();
    public DialogInterface.OnCancelListener mOnCancelListener;
    public DialogInterface.OnDismissListener mOnDismissListener;

    public AccessibleDateAnimator mAnimator;

    public LinearLayout mMonthAndDayView;
    public TextView mSelectedDateTextView;
    public DayPickerGroup mDayPickerView;
    public TextView mDatePickerTitle;
    public ImageView mDatePickerClose;
    public int mCurrentView = UNINITIALIZED;

    public int mWeekStart = PersianCalendar.SATURDAY;
    public String mTitle;
    public HashSet<PersianCalendar> highlightedDays = new HashSet<>();
    public HashSet<PersianCalendar> disabledDays = new HashSet<>();

    public Integer mAccentColor = null;
    public boolean mVibrate = true;
    public boolean mDismissOnPause = false;
    public boolean mAutoDismiss = false;
    public int mDefaultView = MONTH_AND_DAY_VIEW;
    public int mOkResid = R.string.mdtp_ok;
    public int mOkDaysNumberHintResid = R.string.night;
    public int mTitleResid = R.string.mdtp_title;
    public int mDateRangePickerStartHintResId = R.string.mdtp_start_datel;
    public int mDateRangePickerFinishHintResId = R.string.mdtp_finish_date;

    public String mDateRangePickerStartHintString;
    public String mDateRangePickerFinishHintString;
    public String mTitleString;
    public Integer mOkColor = null;
    public Integer mOkBackgroundColorResId = null;
    public Integer mStartDateColor = null;
    public Integer mFinishDateColor = null;
    public Integer mHighlightColor = null;

    public Version mVersion;
    public ScrollOrientation mScrollOrientation;
    public TimeZone mTimezone;
    public Locale mLocale = Locale.getDefault();
    public DefaultDateRangeLimiter mDefaultLimiter = new DefaultDateRangeLimiter();
    public DateRangeLimiter mDateRangeLimiter = mDefaultLimiter;

    public HapticFeedbackController mHapticFeedbackController;

    public boolean mDelayAnimation = true;

    // Accessibility strings.
    public String mDayPickerDescription;
    public String mSelectDay;
    public String mYearPickerDescription;
    public String mSelectYear;

    private boolean isRangeDatePickerEnable = false;
    PersianCalendar mRangeDateStart = null;
    PersianCalendar mRangeDateFinish = null;

    boolean isUeserTapped = false;

    Button okButton;

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDateSetListener {

        /**
         * @param view        The view associated with this listener.
         * @param year        The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility
         *                    with {@link java.util.Calendar}.
         * @param dayOfMonth  The day of the month that was set.
         */
        void onDateSet(PersianDatePickerDialog view, PersianCalendar persianCalendar);

        void onRangeDateSet(PersianDatePickerDialog view, PersianCalendar startPersianCalendar, PersianCalendar finishPersianCalendar);

    }

    /**
     * The callback used to notify other date picker components of a change in selected date.
     */
    public interface OnDateChangedListener {
        void onDateChanged();
    }


    public PersianDatePickerDialog() {
        // Empty constructor required for dialog fragment.
    }

    /**
     * Create a new DatePickerDialog instance with a specific initial selection.
     *
     * @param callBack    How the parent is notified that the date is set.
     * @param year        The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth  The initial day of the dialog.
     * @return a new DatePickerDialog instance.
     */
    public static PersianDatePickerDialog newInstance(OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        PersianDatePickerDialog ret = new PersianDatePickerDialog();
        ret.initialize(callBack, year, monthOfYear, dayOfMonth);
        return ret;
    }

    /**
     * Create a new DatePickerDialog instance initialised to the current system date.
     *
     * @param callback How the parent is notified that the date is set.
     * @return a new DatePickerDialog instance
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public static PersianDatePickerDialog newInstance(OnDateSetListener callback) {
        PersianCalendar now = new PersianCalendar();
        return PersianDatePickerDialog.newInstance(callback, now);
    }

    /**
     * Create a new DatePickerDialog instance with a specific initial selection.
     *
     * @param callback         How the parent is notified that the date is set.
     * @param initialSelection A Calendar object containing the original selection of the picker.
     *                         (Time is ignored by trimming the Calendar to midnight in the current
     *                         TimeZone of the Calendar object)
     * @return a new DatePickerDialog instance
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public static PersianDatePickerDialog newInstance(OnDateSetListener callback, PersianCalendar initialSelection) {
        PersianDatePickerDialog ret = new PersianDatePickerDialog();
        ret.initialize(callback, initialSelection);
        return ret;
    }

    public void initialize(OnDateSetListener callBack, PersianCalendar initialSelection) {
        mCallBack = callBack;
        mCalendar = (PersianCalendar) initialSelection.clone();
        mScrollOrientation = null;
        //noinspection deprecation
        setTimeZone(mCalendar.getTimeZone());

        mVersion = Version.VERSION_1; //Build.VERSION.SDK_INT < Build.VERSION_CODES.M ? Version.VERSION_1 : Version.VERSION_2;
    }

    public void initialize(OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        PersianCalendar cal = new PersianCalendar();
        cal.setPersianDate(year, monthOfYear, dayOfMonth);
        this.initialize(callBack, cal);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity = requireActivity();
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setStyle(AppCompatDialogFragment.STYLE_NO_TITLE, 0);
        mCurrentView = UNINITIALIZED;
        if (savedInstanceState != null) {
            mCalendar.setPersianDate(
                    savedInstanceState.getInt(KEY_SELECTED_YEAR)
                    , savedInstanceState.getInt(KEY_SELECTED_MONTH)
                    , savedInstanceState.getInt(KEY_SELECTED_DAY));


            mDefaultView = savedInstanceState.getInt(KEY_DEFAULT_VIEW);
        }
//        if (Build.VERSION.SDK_INT < 18) {
//            VERSION_2_FORMAT = new SimpleDateFormat(activity.getResources().getString(R.string.mdtp_date_v2_daymonthyear), mLocale);
//        } else {
//            VERSION_2_FORMAT = new SimpleDateFormat(DateFormat.getBestDateTimePattern(mLocale, "EEEMMMdd"), mLocale);
//        }
//        VERSION_2_FORMAT.setTimeZone(getTimeZone());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_YEAR, mCalendar.get(PersianCalendar.YEAR));
        outState.putInt(KEY_SELECTED_MONTH, mCalendar.get(PersianCalendar.MONTH));
        outState.putInt(KEY_SELECTED_DAY, mCalendar.get(PersianCalendar.DAY_OF_MONTH));
        outState.putInt(KEY_WEEK_START, mWeekStart);
        outState.putInt(KEY_CURRENT_VIEW, mCurrentView);
        int listPosition = -1;
        if (mCurrentView == MONTH_AND_DAY_VIEW) {
            listPosition = mDayPickerView.getMostVisiblePosition();
        }

        outState.putInt(KEY_LIST_POSITION, listPosition);
        outState.putSerializable(KEY_HIGHLIGHTED_DAYS, highlightedDays);
        outState.putSerializable(KEY_DISABLED_DAYS, disabledDays);
        if (mAccentColor != null) outState.putInt(KEY_ACCENT, mAccentColor);
        outState.putBoolean(KEY_VIBRATE, mVibrate);
        outState.putBoolean(KEY_DISMISS, mDismissOnPause);
        outState.putBoolean(KEY_AUTO_DISMISS, mAutoDismiss);
        outState.putInt(KEY_DEFAULT_VIEW, mDefaultView);
        outState.putString(KEY_TITLE, mTitle);
        outState.putInt(KEY_OK_RESID, mOkResid);
        outState.putInt(KEY_RANGE_PICKER_RESULT_HINT, mOkDaysNumberHintResid);
        outState.putString(KEY_TITLE_STRING, mTitleString);
        if (mOkColor != null) outState.putInt(KEY_OK_COLOR, mOkColor);
        if (mOkBackgroundColorResId != null)
            outState.putInt(KEY_OK_BACKGROUND_COLOR, mOkBackgroundColorResId);

        if (mStartDateColor != null) outState.putInt(KEY_START_COLOR, mStartDateColor);
        if (mFinishDateColor != null) outState.putInt(KEY_FINISH_COLOR, mFinishDateColor);
        if (mHighlightColor != null) outState.putInt(KEY_HIGHLIGHT_COLOR, mHighlightColor);

        outState.putSerializable(KEY_VERSION, mVersion);
        outState.putSerializable(KEY_SCROLL_ORIENTATION, mScrollOrientation);
        outState.putSerializable(KEY_TIMEZONE, mTimezone);
        outState.putParcelable(KEY_DATERANGELIMITER, mDateRangeLimiter);
        outState.putSerializable(KEY_LOCALE, mLocale);

        outState.putBoolean(KEY_RANGE_DATE_PICKER, isRangeDatePickerEnable);
        if (mRangeDateStart != null)
            outState.putSerializable(KEY_RANGE_DATE_PICKER_START, mRangeDateStart);
        if (mRangeDateFinish != null)
            outState.putSerializable(KEY_RANGE_DATE_PICKER_FINITSH, mRangeDateFinish);
        outState.putBoolean(KEY_USER_TAPPED, isUeserTapped);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int listPosition = -1;
        int listPositionOffset = 0;
        int currentView = mDefaultView;
        if (mScrollOrientation == null) {
            mScrollOrientation = mVersion == Version.VERSION_1
                    ? ScrollOrientation.VERTICAL
                    : ScrollOrientation.HORIZONTAL;
        }
        if (savedInstanceState != null) {
            mWeekStart = savedInstanceState.getInt(KEY_WEEK_START);
            currentView = savedInstanceState.getInt(KEY_CURRENT_VIEW);
            listPosition = savedInstanceState.getInt(KEY_LIST_POSITION);
            listPositionOffset = savedInstanceState.getInt(KEY_LIST_POSITION_OFFSET);
            //noinspection unchecked
            highlightedDays = (HashSet<PersianCalendar>) savedInstanceState.getSerializable(KEY_HIGHLIGHTED_DAYS);
            disabledDays = (HashSet<PersianCalendar>) savedInstanceState.getSerializable(KEY_DISABLED_DAYS);

            if (savedInstanceState.containsKey(KEY_ACCENT))
                mAccentColor = savedInstanceState.getInt(KEY_ACCENT);
            mVibrate = savedInstanceState.getBoolean(KEY_VIBRATE);
            mDismissOnPause = savedInstanceState.getBoolean(KEY_DISMISS);
            mAutoDismiss = savedInstanceState.getBoolean(KEY_AUTO_DISMISS);
            mTitle = savedInstanceState.getString(KEY_TITLE);
            mOkResid = savedInstanceState.getInt(KEY_OK_RESID);
            mOkDaysNumberHintResid = savedInstanceState.getInt(KEY_RANGE_PICKER_RESULT_HINT);
            mTitleString = savedInstanceState.getString(KEY_TITLE_STRING);
            if (savedInstanceState.containsKey(KEY_OK_COLOR))
                mOkColor = savedInstanceState.getInt(KEY_OK_COLOR);

            if (savedInstanceState.containsKey(KEY_OK_BACKGROUND_COLOR))
                mOkBackgroundColorResId = savedInstanceState.getInt(KEY_OK_BACKGROUND_COLOR);

            if (savedInstanceState.containsKey(KEY_START_COLOR))
                mStartDateColor = savedInstanceState.getInt(KEY_START_COLOR);

            if (savedInstanceState.containsKey(KEY_FINISH_COLOR))
                mFinishDateColor = savedInstanceState.getInt(KEY_FINISH_COLOR);

            if (savedInstanceState.containsKey(KEY_HIGHLIGHT_COLOR))
                mHighlightColor = savedInstanceState.getInt(KEY_HIGHLIGHT_COLOR);

            mVersion = (Version) savedInstanceState.getSerializable(KEY_VERSION);
            mScrollOrientation = (ScrollOrientation) savedInstanceState.getSerializable(KEY_SCROLL_ORIENTATION);
            mTimezone = (TimeZone) savedInstanceState.getSerializable(KEY_TIMEZONE);
            mDateRangeLimiter = savedInstanceState.getParcelable(KEY_DATERANGELIMITER);

            isRangeDatePickerEnable = savedInstanceState.getBoolean(KEY_RANGE_DATE_PICKER);
            mRangeDateStart = (PersianCalendar) savedInstanceState.getSerializable(KEY_RANGE_DATE_PICKER_START);
            mRangeDateFinish = (PersianCalendar) savedInstanceState.getSerializable(KEY_RANGE_DATE_PICKER_FINITSH);

            isUeserTapped = savedInstanceState.getBoolean(KEY_USER_TAPPED);

            /*
            We need to update some variables when setting the locale, so use the setter rather
            than a plain assignment
             */
            // setLocale((Locale) savedInstanceState.getSerializable(KEY_LOCALE));

            /*
            If the user supplied a custom limiter, we need to create a new default one to prevent
            null pointer exceptions on the configuration methods
            If the user did not supply a custom limiter we need to ensure both mDefaultLimiter
            and mDateRangeLimiter are the same reference, so that the config methods actually
            affect the behaviour of the picker (in the unlikely event the user reconfigures
            the picker when it is shown)
             */
            if (mDateRangeLimiter instanceof DefaultDateRangeLimiter) {
                mDefaultLimiter = (DefaultDateRangeLimiter) mDateRangeLimiter;
            } else {
                mDefaultLimiter = new DefaultDateRangeLimiter();
            }
        }

        mDefaultLimiter.setController(this);

        int viewRes = R.layout.mdtp_date_picker_dialog;
        View view = inflater.inflate(viewRes, container, false);
        // All options have been set at this point: round the initial selection if necessary
        mCalendar = mDateRangeLimiter.setToNearestDate(mCalendar);

        mMonthAndDayView = view.findViewById(R.id.mdtp_date_picker_month_and_day);
        mMonthAndDayView.setOnClickListener(this);
        mSelectedDateTextView = view.findViewById(R.id.mdtp_date_picker_selected_date);
        mDatePickerTitle = view.findViewById(R.id.mdtp_date_picker_title);
        mDatePickerClose = view.findViewById(R.id.mdtp_date_picker_close);
        mDatePickerClose.setOnClickListener(this);
        final Activity activity = requireActivity();
        mDayPickerView = new DayPickerGroup(activity, this);

        if (mTitleString != null) mDatePickerTitle.setText(mTitleString);
        else mDatePickerTitle.setText(mTitleResid);


        Resources res = getResources();
        mDayPickerDescription = res.getString(R.string.mdtp_day_picker_description);
        mSelectDay = res.getString(R.string.mdtp_select_day);
        mYearPickerDescription = res.getString(R.string.mdtp_year_picker_description);
        mSelectYear = res.getString(R.string.mdtp_select_year);

        int bgColorResource = R.color.mdtp_date_picker_view_animator;
        int bgColor = ContextCompat.getColor(activity, bgColorResource);
        view.setBackgroundColor(bgColor);

        mAnimator = view.findViewById(R.id.mdtp_animator);
        mAnimator.addView(mDayPickerView);
        mAnimator.setDateMillis(mCalendar.getTimeInMillis());
        // TODO: Replace with animation decided upon by the design team.
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(ANIMATION_DURATION);
        mAnimator.setInAnimation(animation);
        // TODO: Replace with animation decided upon by the design team.
        Animation animation2 = new AlphaAnimation(1.0f, 0.0f);
        animation2.setDuration(ANIMATION_DURATION);
        mAnimator.setOutAnimation(animation2);

        okButton = view.findViewById(R.id.mdtp_ok);
        okButton.setOnClickListener(v -> {
            tryVibrate();
            notifyOnDateListener();
            dismiss();
        });
        okButton.setTypeface(ResourcesCompat.getFont(activity, R.font.robotomedium));

        okButton.setText(mOkResid);


        // If an accent color has not been set manually, get it from the context
        if (mAccentColor == null) {
            mAccentColor = Utils.getAccentColorFromThemeIfAvailable(getActivity());
        }

        view.findViewById(R.id.mdtp_day_picker_selected_date_layout).setBackgroundColor(getResources().getColor(R.color.color_f5));

        // Buttons can have a different color
        if (mOkColor == null) {
            mOkColor = R.color.mdtp_white;
        }
        if (mOkBackgroundColorResId == null) {
            mOkBackgroundColorResId = R.color.mdtp_accent_color;
        }
        okButton.setTextColor(getResources().getColor(mOkColor));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            okButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), mOkBackgroundColorResId));
        }
        if (isRangeDatePickerEnable) {
            okButton.setEnabled(false);
        }

        mSelectedDateTextView.setTextColor(getStartDateColor());

        if (getDialog() == null) {
            view.findViewById(R.id.mdtp_done_background).setVisibility(View.GONE);
        }

        updateDisplay(false);
        //setCurrentView(currentView);

        if (listPosition != -1) {
            if (currentView == MONTH_AND_DAY_VIEW) {
                mDayPickerView.postSetSelection(listPosition);
            }
        }

        mHapticFeedbackController = new HapticFeedbackController(activity);
        return view;
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ViewGroup viewGroup = (ViewGroup) getView();
        if (viewGroup != null) {
            viewGroup.removeAllViewsInLayout();
            View view = onCreateView(requireActivity().getLayoutInflater(), viewGroup, null);
            viewGroup.addView(view);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mHapticFeedbackController.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mHapticFeedbackController.stop();
        if (mDismissOnPause) dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (mOnCancelListener != null) mOnCancelListener.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) mOnDismissListener.onDismiss(dialog);
    }

    private void setCurrentView(final int viewIndex) {
        long millis = mCalendar.getTimeInMillis();

        switch (viewIndex) {
            case MONTH_AND_DAY_VIEW:

                ObjectAnimator pulseAnimator = Utils.getPulseAnimator(mMonthAndDayView, 0.9f,
                        1.05f);
                if (mDelayAnimation) {
                    pulseAnimator.setStartDelay(ANIMATION_DELAY);
                    mDelayAnimation = false;
                }
                if (mCurrentView != viewIndex) {
                    mMonthAndDayView.setSelected(true);
                    mAnimator.setDisplayedChild(MONTH_AND_DAY_VIEW);
                    mCurrentView = viewIndex;
                }
                mDayPickerView.onDateChanged();
                pulseAnimator.start();


                int flags = DateUtils.FORMAT_SHOW_DATE;
                String dayString = DateUtils.formatDateTime(getActivity(), millis, flags);
                mAnimator.setContentDescription(mDayPickerDescription + ": " + dayString);
                Utils.tryAccessibilityAnnounce(mAnimator, mSelectDay);
                break;

        }
    }

    private void updateDisplay(boolean announce) {

        if (isRangeDatePickerEnable) {
            if (mRangeDateStart == null && mRangeDateFinish == null) {

                mSelectedDateTextView.setText(getDateRangePickerStartHintString());
            } else if (mRangeDateFinish == null) {
                mSelectedDateTextView.setText(PersianNumberUtils.toFarsi(mRangeDateStart.getPersianShortDatePersianFormat()
                        + " - " +
                        getDateRangePickerFinishHintString()
                ));
            } else {
                mSelectedDateTextView.setText(PersianNumberUtils.toFarsi(mRangeDateStart.getPersianShortDatePersianFormat()
                        + " - " +
                        mRangeDateFinish.getPersianShortDatePersianFormat()
                ));

//                this.highlightedDays.clear();
//                setHighlightedDays(PersianCalendarUtils.getDatesBetween(mRangeDateFinish,mRangeDateStart));
            }
        } else {
            mSelectedDateTextView.setText(PersianNumberUtils.toFarsi(mCalendar.getPersianShortDatePersianFormat()));
        }
        //setCurrentView(MONTH_AND_DAY_VIEW);
//        }
    }


    public void setEnableRangePicker(Boolean state) {

        isRangeDatePickerEnable = state;
        mRangeDateStart = null;
        mRangeDateFinish = null;
        highlightedDays.clear();

    }

    /**
     * @return true if Enable DateRangePicker
     */
    @Override
    public boolean isRangDatePickerEnable() {
        return isRangeDatePickerEnable;
    }

    @Override
    public PersianCalendar getRangeDatePickerStartDate() {
        return mRangeDateStart;
    }

    @Override
    public void setRangeDatePickerStartDate(int year, int month, int mSelectedDay) {
        PersianCalendar p = new PersianCalendar();
        p.setPersianDate(year, month, mSelectedDay);
        mRangeDateStart = p;
    }

    @Override
    public PersianCalendar getRangeDatePickerFinishDate() {
        return mRangeDateFinish;
    }

    @Override
    public void setRangeDatePickerFinishDate(int year, int month, int mSelectedDay) {
        PersianCalendar p = new PersianCalendar();
        p.setPersianDate(year, month, mSelectedDay);
        mRangeDateFinish = p;
    }

    @Override
    public boolean getRangeDatePickerFinishIsEqualWith(int year, int month, int mSelectedDay) {

        PersianCalendar temp = new PersianCalendar();
        temp.setPersianDate(year, month, mSelectedDay);

        return mRangeDateFinish.equals(temp);
    }

    @Override
    public boolean getRangeDatePickerStartIsEqualWith(int year, int month, int mSelectedDay) {
        PersianCalendar temp = new PersianCalendar();
        temp.setPersianDate(year, month, mSelectedDay);

        return mRangeDateStart.equals(temp);
    }

    @Override
    public void clearRangeDatePickerFinishDate() {
        mRangeDateFinish = null;
    }

    @Override
    public void clearRangeDatePickerStartDate() {
        mRangeDateStart = null;
    }

    @Override
    public boolean isUserTapedOnDay() {
        return isUeserTapped;
    }

    @Override
    public void setUserTapedOnDay(boolean state) {
        isUeserTapped = state;
    }

    /**
     * Set whether the device should vibrate when touching fields
     *
     * @param vibrate true if the device should vibrate when touching a field
     */
    public void vibrate(boolean vibrate) {
        mVibrate = vibrate;
    }

    /**
     * Set whether the picker should dismiss itself when being paused or whether it should try to survive an orientation change
     *
     * @param dismissOnPause true if the dialog should dismiss itself when it's pausing
     */
    public void dismissOnPause(boolean dismissOnPause) {
        mDismissOnPause = dismissOnPause;
    }

    /**
     * Set whether the picker should dismiss itself when a day is selected
     *
     * @param autoDismiss true if the dialog should dismiss itself when a day is selected
     */
    @SuppressWarnings("unused")
    public void autoDismiss(boolean autoDismiss) {
        mAutoDismiss = autoDismiss;
    }

    /**
     * Set the Start color of this dialog
     *
     * @param color the Start color you want
     */
    @SuppressWarnings("unused")
    public void setStartDateColor(String color) {
        mStartDateColor = Color.parseColor(color);
    }

    /**
     * Set the Finish color of this dialog
     *
     * @param color the Finish color you want
     */
    @SuppressWarnings("unused")
    public void setFinishDateColor(String color) {
        mFinishDateColor = Color.parseColor(color);
    }

    /**
     * Set the Highlight color of this dialog
     *
     * @param color the Highlight color you want
     */
    @SuppressWarnings("unused")
    public void setHighlightColor(String color) {
        mHighlightColor = Color.parseColor(color);
    }


    /**
     * Set the accent color of this dialog
     *
     * @param color the accent color you want
     */
    @SuppressWarnings("unused")
    public void setAccentColor(String color) {
        mAccentColor = Color.parseColor(color);
    }

    /**
     * Set the text color of the OK button
     *
     * @param color the color you want
     */
    @SuppressWarnings("unused")
    public void setOkColor(String color) {
        mOkColor = Color.parseColor(color);
    }

    public void setOkColor(@ColorRes Integer color) {
        mOkColor = color;
    }

    public void setOkBackgroundColor(@ColorRes Integer color) {
        mOkBackgroundColorResId = color;
    }


    /**
     * Get the accent color of this dialog
     *
     * @return accent color
     */
    @Override
    public int getAccentColor() {
        return mAccentColor;
    }

    /**
     * Get the Start color of this dialog
     *
     * @return start color
     */

    @Override
    public int getStartDateColor() {
        return mStartDateColor == null ? mAccentColor : mStartDateColor;
    }

    /**
     * Get the Finish color of this dialog
     *
     * @return start color
     */
    @Override
    public int getFinishDateColor() {
        return mFinishDateColor == null ? mAccentColor : mFinishDateColor;
    }

    /**
     * Get the Highlite color of this dialog
     *
     * @return start color
     */
    @Override
    public int getHighlightColor() {
        return mHighlightColor == null ? mAccentColor : mHighlightColor;
    }

    /**
     * Set whether the year picker of the month and day picker is shown first
     *
     * @param yearPicker boolean
     */
    public void showYearPickerFirst(boolean yearPicker) {
        mDefaultView = yearPicker ? YEAR_VIEW : MONTH_AND_DAY_VIEW;
    }

    @SuppressWarnings("unused")
    public void setFirstDayOfWeek(int startOfWeek) {
        if (startOfWeek < Calendar.SUNDAY || startOfWeek > Calendar.SATURDAY) {
            throw new IllegalArgumentException("Value must be between Calendar.SUNDAY and " +
                    "Calendar.SATURDAY");
        }
        mWeekStart = startOfWeek;
        if (mDayPickerView != null) {
            mDayPickerView.onChange();
        }
    }

    @SuppressWarnings("unused")
    public void setYearRange(int startYear, int endYear) {
        mDefaultLimiter.setYearRange(startYear, endYear);

        if (mDayPickerView != null) {
            mDayPickerView.onChange();
        }
    }

    /**
     * Sets the minimal date supported by this DatePicker. Dates before (but not including) the
     * specified date will be disallowed from being selected.
     *
     * @param calendar a Calendar object set to the year, month, day desired as the mindate.
     */
    @SuppressWarnings("unused")
    public void setMinDate(PersianCalendar calendar) {
        mDefaultLimiter.setMinDate(calendar);

        if (mDayPickerView != null) {
            mDayPickerView.onChange();
        }
    }

    /**
     * @return The minimal date supported by this DatePicker. Null if it has not been set.
     */
    @SuppressWarnings("unused")
    public PersianCalendar getMinDate() {
        return mDefaultLimiter.getMinDate();
    }

    /**
     * Sets the minimal date supported by this DatePicker. Dates after (but not including) the
     * specified date will be disallowed from being selected.
     *
     * @param calendar a Calendar object set to the year, month, day desired as the maxdate.
     */
    @SuppressWarnings("unused")
    public void setMaxDate(PersianCalendar calendar) {
        mDefaultLimiter.setMaxDate(calendar);

        if (mDayPickerView != null) {
            mDayPickerView.onChange();
        }
    }

    /**
     * @return The maximal date supported by this DatePicker. Null if it has not been set.
     */
    @SuppressWarnings("unused")
    public PersianCalendar getMaxDate() {
        return mDefaultLimiter.getMaxDate();
    }

    /**
     * Sets an array of dates which should be highlighted when the picker is drawn
     *
     * @param highlightedDays an Array of Calendar objects containing the dates to be highlighted
     */
    @SuppressWarnings("unused")
    public void setHighlightedDays(PersianCalendar[] highlightedDays) {
        this.highlightedDays.clear();
        for (PersianCalendar highlightedDay : highlightedDays) {
            this.highlightedDays.add(Utils.trimToMidnight((PersianCalendar) highlightedDay.clone()));
        }
        if (mDayPickerView != null) mDayPickerView.onChange();
    }

    public void setHighlightedDays(HashSet<PersianCalendar> days) {
        this.highlightedDays.clear();

        this.highlightedDays = days;

//        for (PersianCalendar highlightedDay : highlightedDays) {
//            this.highlightedDays.add(highlightedDay);
//        }
        if (mDayPickerView != null) mDayPickerView.onChange();
    }

    /**
     * @return The list of dates, as Calendar Objects, which should be highlighted. null is no dates should be highlighted
     */
    @SuppressWarnings("unused")
    public PersianCalendar[] getHighlightedDays() {
        if (highlightedDays.isEmpty()) return null;
        PersianCalendar[] output = highlightedDays.toArray(new PersianCalendar[0]);
        Arrays.sort(output);
        return output;
    }

    @Override
    public boolean isHighlighted(int year, int month, int day) {
        PersianCalendar date = new PersianCalendar();
        date.setPersianDate(year, month, day);
        //Utils.trimToMidnight(date);
        return highlightedDays.contains(date);
    }

    /**
     * Sets an array of dates which should be disabled when the picker is drawn
     *
     * @param disabledDays an Array of Calendar objects containing the dates to be highlighted
     */
    @SuppressWarnings("unused")
    public void setDisabledDays(PersianCalendar[] disabledDays) {
        for (PersianCalendar day : disabledDays) {
            this.disabledDays.add(Utils.trimToMidnight((PersianCalendar) day.clone()));
        }
        if (mDayPickerView != null) mDayPickerView.onChange();
    }

    public void setDisabledDays(HashSet<PersianCalendar> disabledDays) {
        this.disabledDays = disabledDays;

        if (mDayPickerView != null) mDayPickerView.onChange();
    }

    /**
     * @return The list of dates, as Calendar Objects, which should be disabled. null is no dates should be highlighted
     */
    @SuppressWarnings("unused")
    public PersianCalendar[] getDisabledDays() {
        if (disabledDays.isEmpty()) return null;
        PersianCalendar[] output = disabledDays.toArray(new PersianCalendar[0]);
        Arrays.sort(output);
        return output;
    }

    @Override
    public boolean isDisabled(int year, int month, int day) {
        PersianCalendar date = new PersianCalendar();
        date.setPersianDate(year, month, day);
        //Utils.trimToMidnight(date);
        return disabledDays.contains(date);
    }

    /**
     * Sets a list of days that are not selectable in the picker
     * Setting this value will take precedence over using setMinDate() and setMaxDate(), but stacks with setSelectableDays()
     *
     * @param disabledDays an Array of Calendar Objects containing the disabled dates
     */
    @SuppressWarnings("unused")

    public void setDisabledDaysBeforeToday() {

        PersianCalendar today = new PersianCalendar();
        PersianCalendar startDay = new PersianCalendar();
        startDay.setPersianDate(getMinYear(), 0, 0);

        this.setDisabledDays(PersianCalendarUtils.getDatesBetween(today, startDay));
    }

    /**
     * Sets a list of days which are the only valid selections.
     * Setting this value will take precedence over using setMinDate() and setMaxDate()
     *
     * @param selectableDays an Array of Calendar Objects containing the selectable dates
     */
    @SuppressWarnings("unused")
    public void setSelectableDays(PersianCalendar[] selectableDays) {
        mDefaultLimiter.setSelectableDays(selectableDays);
        if (mDayPickerView != null) mDayPickerView.onChange();
    }

    /**
     * @return an Array of Calendar objects containing the list with selectable items. null if no restriction is set
     */
    @SuppressWarnings("unused")
    public PersianCalendar[] getSelectableDays() {
        return mDefaultLimiter.getSelectableDays();
    }


    /**
     * Provide a DateRangeLimiter for full control over which dates are enabled and disabled in the picker
     *
     * @param dateRangeLimiter An implementation of the DateRangeLimiter interface
     */
    @SuppressWarnings("unused")
    public void setDateRangeLimiter(DateRangeLimiter dateRangeLimiter) {
        mDateRangeLimiter = dateRangeLimiter;
    }

    /**
     * Set the label for the Ok button (max 12 characters)
     *
     * @param okResid A resource ID to be used as the Ok button label
     */
    @SuppressWarnings("unused")
    public void setOkText(@StringRes int okResid) {
        mOkResid = okResid;
    }

    public void setOkDaysNumberHintText(@StringRes int okResid) {
        mOkDaysNumberHintResid = okResid;
    }


    public void setDateRangePickerStartHintString(String mDateRangePickerStartHintString) {
        this.mDateRangePickerStartHintString = mDateRangePickerStartHintString;
    }

    public void setDateRangePickerFinishHintString(String mDateRangePickerFinishHintString) {
        this.mDateRangePickerFinishHintString = mDateRangePickerFinishHintString;
    }

    public void setDateRangePickerStartHintString(@StringRes int resid) {
        this.mDateRangePickerStartHintResId = resid;
    }

    public void setDateRangePickerFinishHintString(@StringRes int resid) {
        this.mDateRangePickerFinishHintResId = resid;
    }

    public String getDateRangePickerStartHintString() {
        return (mDateRangePickerStartHintString == null) ? getString(mDateRangePickerStartHintResId) : mDateRangePickerStartHintString;
    }

    public String getDateRangePickerFinishHintString() {
        return (mDateRangePickerFinishHintString == null) ? getString(mDateRangePickerFinishHintResId) : mDateRangePickerFinishHintString;
    }

    /**
     * Set the label for the Title
     *
     * @param titleString A literal String to be used as the Title label
     */
    @SuppressWarnings("unused")
    public void setTitleText(String titleString) {
        mTitleString = titleString;
    }

    /**
     * Set the label for the Title
     *
     * @param titleResid A resource ID to be used as the Title label
     */
    @SuppressWarnings("unused")
    public void setTitleText(@StringRes int titleResid) {
        mTitleString = null;
        mTitleResid = titleResid;
    }


    /**
     * Set which layout version the picker should use
     *
     * @param version The version to use
     */
    public void setVersion(Version version) {
        mVersion = version;
    }

    /**
     * Get the layout version the Dialog is using
     *
     * @return Version
     */
    public Version getVersion() {
        return mVersion;
    }

    /**
     * Set which way the user needs to swipe to switch months in the MonthView
     *
     * @param orientation The orientation to use
     */
    public void setScrollOrientation(ScrollOrientation orientation) {
        mScrollOrientation = orientation;
    }

    /**
     * Get which way the user needs to swipe to switch months in the MonthView
     *
     * @return SwipeOrientation
     */
    public ScrollOrientation getScrollOrientation() {
        return mScrollOrientation;
    }

    /**
     * Set which timezone the picker should use
     * <p>
     * This has been deprecated in favor of setting the TimeZone using the constructor that
     * takes a Calendar object
     *
     * @param timeZone The timezone to use
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public void setTimeZone(TimeZone timeZone) {
        mTimezone = timeZone;
        mCalendar.setTimeZone(timeZone);
        YEAR_FORMAT.setTimeZone(timeZone);
        MONTH_FORMAT.setTimeZone(timeZone);
        DAY_FORMAT.setTimeZone(timeZone);
    }


    /**
     * Return the current locale (default or other)
     *
     * @return Locale
     */
    @Override
    public Locale getLocale() {
        return mLocale;
    }

    @SuppressWarnings("unused")
    public void setOnDateSetListener(OnDateSetListener listener) {
        mCallBack = listener;
    }


    @SuppressWarnings("unused")
    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    /**
     * Get a reference to the callback
     *
     * @return OnDateSetListener the callback
     */
    @SuppressWarnings("unused")
    public OnDateSetListener getOnDateSetListener() {
        return mCallBack;
    }

    // If the newly selected month / year does not contain the currently selected day number,
    // change the selected day number to the last day of the selected month or year.
    //      e.g. Switching from Mar to Apr when Mar 31 is selected -> Apr 30
    //      e.g. Switching from 2012 to 2013 when Feb 29, 2012 is selected -> Feb 28, 2013
    private PersianCalendar adjustDayInMonthIfNeeded(PersianCalendar calendar) {
        int day = calendar.getPersianDay();
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (day > daysInMonth) {
            calendar.setPersianDate(calendar.getPersianYear(), calendar.getPersianMonth(), daysInMonth);
        }
        return mDateRangeLimiter.setToNearestDate(calendar);
    }

    @Override
    public void onClick(View v) {
        tryVibrate();
//        if (v.getId() == R.id.mdtp_date_picker_month_and_day) {
//            setCurrentView(MONTH_AND_DAY_VIEW);
//        } else
            if (v.getId() == R.id.mdtp_date_picker_close) {
            dismiss();
        }
    }

    @Override
    public void onYearSelected(int year) {
        mCalendar.setPersianDate(year, mCalendar.getPersianMonth(),
                mCalendar.getPersianDay());
        mCalendar = adjustDayInMonthIfNeeded(mCalendar);
        updatePickers();
        setCurrentView(MONTH_AND_DAY_VIEW);
        updateDisplay(true);
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {
        mCalendar.setPersianDate(year, month, day);

        if (isRangeDatePickerEnable) {
            if (mRangeDateStart != null && mRangeDateFinish != null) {
                setHighlightedDays(PersianCalendarUtils.getDatesBetween(mRangeDateFinish, mRangeDateStart));

                okButton.setText(String.format("%s (%s %s)", getString(mOkResid), PersianNumberUtils.toFarsi(highlightedDays.size() - 1), getString(mOkDaysNumberHintResid)));
                okButton.setEnabled(true);
            } else {
                okButton.setText(mOkResid);
                highlightedDays.clear();
                okButton.setEnabled(false);
            }
        }

        updatePickers();
        updateDisplay(true);
        if (mAutoDismiss) {
            notifyOnDateListener();
            dismiss();
        }
    }

    private void updatePickers() {
        for (OnDateChangedListener listener : mListeners) listener.onDateChanged();
    }


    @Override
    public MonthAdapter.CalendarDay getSelectedDay() {
        return new MonthAdapter.CalendarDay(mCalendar, getTimeZone());
    }

    @Override
    public PersianCalendar getStartDate() {
        return mDateRangeLimiter.getStartDate();
    }

    @Override
    public PersianCalendar getEndDate() {
        return mDateRangeLimiter.getEndDate();
    }

    @Override
    public int getMinYear() {
        return mDateRangeLimiter.getMinYear();
    }

    @Override
    public int getMaxYear() {
        return mDateRangeLimiter.getMaxYear();
    }


    @Override
    public boolean isOutOfRange(int year, int month, int day) {
        return mDateRangeLimiter.isOutOfRange(year, month, day);
    }

    @Override
    public int getFirstDayOfWeek() {
        return mWeekStart;
    }

    @Override
    public void registerOnDateChangedListener(OnDateChangedListener listener) {
        mListeners.add(listener);
    }

    @Override
    public void unregisterOnDateChangedListener(OnDateChangedListener listener) {
        mListeners.remove(listener);
    }

    @Override
    public void tryVibrate() {
        if (mVibrate) mHapticFeedbackController.tryVibrate();
    }

    @Override
    public TimeZone getTimeZone() {
        return mTimezone == null ? TimeZone.getDefault() : mTimezone;
    }

    public void notifyOnDateListener() {
        if (mCallBack != null) {
            if (isRangeDatePickerEnable)
                mCallBack.onRangeDateSet(PersianDatePickerDialog.this, mRangeDateStart, mRangeDateFinish);
            else
                mCallBack.onDateSet(PersianDatePickerDialog.this, mCalendar);
        }
    }
}
