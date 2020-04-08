package com.wdullaer.datetimepickerexample;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.PersianDatePickerDialog;
import com.wdullaer.materialdatetimepicker.utils.PersianCalendar;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerFragment extends Fragment implements PersianDatePickerDialog.OnDateSetListener {

    private TextView dateTextView;
    private CheckBox modeDarkDate;
    private CheckBox modeCustomAccentDate;
    private CheckBox vibrateDate;
    private CheckBox dismissDate;
    private CheckBox titleDate;
    private CheckBox showYearFirst;
    private CheckBox showVersion2;
    private CheckBox switchOrientation;
    private CheckBox limitSelectableDays;
    private CheckBox highlightDays;
    private PersianDatePickerDialog dpd;

    public DatePickerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.datepicker_layout, container, false);

        // Find our View instances
        dateTextView = view.findViewById(R.id.date_textview);
        Button dateButton = view.findViewById(R.id.date_button);
        modeDarkDate = view.findViewById(R.id.mode_dark_date);
        modeCustomAccentDate = view.findViewById(R.id.mode_custom_accent_date);
        vibrateDate = view.findViewById(R.id.vibrate_date);
        dismissDate = view.findViewById(R.id.dismiss_date);
        titleDate = view.findViewById(R.id.title_date);
        showYearFirst = view.findViewById(R.id.show_year_first);
        showVersion2 = view.findViewById(R.id.show_version_2);
        switchOrientation = view.findViewById(R.id.switch_orientation);
        limitSelectableDays = view.findViewById(R.id.limit_dates);
        highlightDays = view.findViewById(R.id.highlight_dates);

        view.findViewById(R.id.original_button).setOnClickListener(v -> {
            PersianCalendar now = new PersianCalendar();
            new android.app.DatePickerDialog(
                    requireActivity(),
                    (view1, year, month, dayOfMonth) -> Log.d("Orignal", "Got clicked"),
                    now.getPersianYear(),
                    now.getPersianMonth(),
                    now.getPersianDay()
            ).show();
        });

        // Show a datepicker when the dateButton is clicked
        dateButton.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            /*
            It is recommended to always create a new instance whenever you need to show a Dialog.
            The sample app is reusing them because it is useful when looking for regressions
            during testing
             */

            PersianCalendar persianCalendar=new PersianCalendar();

            if (dpd == null) {
                dpd = PersianDatePickerDialog.newInstance(
                        DatePickerFragment.this,
                        persianCalendar.getPersianYear(),
                        persianCalendar.getPersianMonth(),
                        persianCalendar.getPersianDay()
                        );
            } else {
                dpd.initialize(
                        DatePickerFragment.this,
                        persianCalendar.getPersianYear(),
                        persianCalendar.getPersianMonth(),
                        persianCalendar.getPersianDay()
                );
            }
            dpd.setThemeDark(modeDarkDate.isChecked());
            dpd.vibrate(vibrateDate.isChecked());
            dpd.dismissOnPause(dismissDate.isChecked());
            dpd.showYearPickerFirst(showYearFirst.isChecked());
            dpd.setVersion(showVersion2.isChecked() ? PersianDatePickerDialog.Version.VERSION_2 : PersianDatePickerDialog.Version.VERSION_1);
            if (modeCustomAccentDate.isChecked()) {
                dpd.setAccentColor(Color.parseColor("#9C27B0"));
            }
            if (titleDate.isChecked()) {
                dpd.setTitle("DatePicker Title");
            }
            if (highlightDays.isChecked()) {
                PersianCalendar date1 = new PersianCalendar();
                PersianCalendar date2 = new PersianCalendar();
                date2.add(Calendar.WEEK_OF_MONTH, -1);
                PersianCalendar date3 = new PersianCalendar();
                date3.add(Calendar.WEEK_OF_MONTH, 1);
                PersianCalendar[] days = {date1, date2, date3};
                dpd.setHighlightedDays(days);
            }
            if (limitSelectableDays.isChecked()) {

//                PersianCalendar minDate=new PersianCalendar();
//                minDate.setPersianDate(1398,1,1);
//                PersianCalendar maxDate=new PersianCalendar();
//                maxDate.setPersianDate(1398,11,29);
//
//                dpd.setMinDate(minDate);
//                dpd.setMaxDate(maxDate);

                PersianCalendar[] days = new PersianCalendar[13];
                for (int i = -3; i < 10; i++) {
                    PersianCalendar day = PersianCalendar.getInstance();
                    day.setPersianDate(day.getPersianYear(),day.getPersianMonth(), i * 2);
                    days[i + 3] = day;
                }
                dpd.setSelectableDays(days);
            }
            if (switchOrientation.isChecked()) {
                if (dpd.getVersion() == PersianDatePickerDialog.Version.VERSION_1) {
                    dpd.setScrollOrientation(PersianDatePickerDialog.ScrollOrientation.HORIZONTAL);
                } else {
                    dpd.setScrollOrientation(PersianDatePickerDialog.ScrollOrientation.VERTICAL);
                }
            }
            dpd.setOnCancelListener(dialog -> {
                Log.d("DatePickerDialog", "Dialog was cancelled");
                dpd = null;
            });
            dpd.show(requireFragmentManager(), "Datepickerdialog");
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dpd = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        PersianDatePickerDialog dpd = (PersianDatePickerDialog) requireFragmentManager().findFragmentByTag("Datepickerdialog");
        if(dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    public void onDateSet(PersianDatePickerDialog view, PersianCalendar persianCalendar) {
        String date = "You picked the following date: " +
                +persianCalendar.getPersianYear()+"/"+persianCalendar.getPersianMonth()+"/"+persianCalendar.getPersianDay();
        dateTextView.setText(date);
        dpd = null;
    }
}
