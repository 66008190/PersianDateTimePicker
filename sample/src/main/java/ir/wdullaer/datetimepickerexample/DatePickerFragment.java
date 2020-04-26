package ir.wdullaer.datetimepickerexample;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wdullaer.datetimepickerexample.R;

import ir.arashjahani.persiandatetimepicker.date.PersianDatePickerDialog;
import ir.arashjahani.persiandatetimepicker.utils.PersianCalendar;
import ir.arashjahani.persiandatetimepicker.utils.PersianCalendarUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerFragment extends Fragment implements PersianDatePickerDialog.OnDateSetListener {

    private TextView dateTextView;
    private CheckBox dateRangePicker;
    private CheckBox vibrateDate;
    private CheckBox dismissDate;
    private CheckBox switchOrientation;

    private CheckBox highlightCertainDates;
    private CheckBox highlightBetweenTwoDates;

    private CheckBox chbDisableDates;
    private CheckBox disableDatesBeforeToday;

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
        dateRangePicker = view.findViewById(R.id.date_range_picker);
        vibrateDate = view.findViewById(R.id.vibrate_date);
        dismissDate = view.findViewById(R.id.dismiss_date);
        switchOrientation = view.findViewById(R.id.switch_orientation);


        highlightCertainDates = view.findViewById(R.id.highlight_certain_dates);
        highlightBetweenTwoDates = view.findViewById(R.id.highlight_between_two_dates);

        chbDisableDates = view.findViewById(R.id.chb_disable_dates);
        disableDatesBeforeToday = view.findViewById(R.id.disable_dates_before_today);


        // Show a datepicker when the dateButton is clicked
        dateButton.setOnClickListener(v -> {

            /*
            It is recommended to always create a new instance whenever you need to show a Dialog.
            The sample app is reusing them because it is useful when looking for regressions
            during testing
             */
            PersianCalendar persianCalendar = new PersianCalendar();
            if (dpd == null) {
                dpd = PersianDatePickerDialog.newInstance(
                        DatePickerFragment.this,
                        persianCalendar.getPersianYear(),
                        persianCalendar.getPersianMonth(),
                        persianCalendar.getPersianDay()
                );
            }
            dpd.setOkBackgroundColor(R.color.button_color);
            dpd.setOkColor(R.color.white);
            //dpd.setOkDaysNumberHintText(R.string.day);
            dpd.setStartDateColor("#459efa");
            dpd.setFinishDateColor("#f5f5f5");
            dpd.setHighlightColor("#459efa");

            dpd.setYearMonthRange(persianCalendar.getPersianYear(), 0,
                    persianCalendar.getPersianYear()+1,2);
            dpd.vibrate(vibrateDate.isChecked());
            dpd.dismissOnPause(dismissDate.isChecked());
            dpd.setVersion(PersianDatePickerDialog.Version.VERSION_1);

            dpd.setEnableRangePicker(dateRangePicker.isChecked());

            if (highlightCertainDates.isChecked()) {

                PersianCalendar day1=new PersianCalendar();
                day1.setPersianDate(day1.getPersianYear(),day1.getPersianMonth(),day1.getPersianDay()+2);
                PersianCalendar day2=new PersianCalendar();
                day2.setPersianDate(day2.getPersianYear(),day2.getPersianMonth(),day2.getPersianDay()+7);

                PersianCalendar day3=new PersianCalendar();
                day3.setPersianDate(day3.getPersianYear(),day3.getPersianMonth(),day3.getPersianDay()-4);

                PersianCalendar[] days = {day1, day2,day3};

                dpd.setHighlightedDays(days);
            }
            if (highlightBetweenTwoDates.isChecked()) {
                PersianCalendar date1=new PersianCalendar();
                date1.setPersianDate(date1.getPersianYear(),date1.getPersianMonth(),date1.getPersianDay()-2);
                PersianCalendar date2=new PersianCalendar();
                date2.setPersianDate(date2.getPersianYear(),date2.getPersianMonth(),date2.getPersianDay()+7);

                dpd.setHighlightedDays(PersianCalendarUtils.getDatesBetween(date2, date1));
            }
            if (chbDisableDates.isChecked()) {

                PersianCalendar day1=new PersianCalendar();
                day1.setPersianDate(day1.getPersianYear(),day1.getPersianMonth(),day1.getPersianDay()+2);
                PersianCalendar day2=new PersianCalendar();
                day2.setPersianDate(day2.getPersianYear(),day2.getPersianMonth(),day2.getPersianDay()+7);

                PersianCalendar day3=new PersianCalendar();
                day3.setPersianDate(day3.getPersianYear(),day3.getPersianMonth(),day3.getPersianDay()-4);

                PersianCalendar[] days = {day1, day2,day3};

                dpd.setDisabledDays(days);
            }
            if (disableDatesBeforeToday.isChecked()) {
                dpd.setDisabledDaysBeforeToday();
            }
            if (switchOrientation.isChecked()) {
                if (dpd.getVersion() == PersianDatePickerDialog.Version.VERSION_1) {
                    dpd.setScrollOrientation(PersianDatePickerDialog.ScrollOrientation.HORIZONTAL);
                } else {
                    dpd.setScrollOrientation(PersianDatePickerDialog.ScrollOrientation.VERTICAL);
                }
            }

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
        if (dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    public void onDateSet(PersianDatePickerDialog view, PersianCalendar persianCalendar) {
        String date = "You picked the following date: " +
                +persianCalendar.getPersianYear() + "/" + (persianCalendar.getPersianMonth()+1) + "/" + persianCalendar.getPersianDay();
        dateTextView.setText(date);
        dpd = null;
    }

    @Override
    public void onRangeDateSet(PersianDatePickerDialog view, PersianCalendar startPersianCalendar, PersianCalendar finishPersianCalendar) {
        String date = "You picked the following range date: " +
                +startPersianCalendar.getPersianYear() + "/" + (startPersianCalendar.getPersianMonth()+1) + "/" + startPersianCalendar.getPersianDay()
                + "--" +
                +finishPersianCalendar.getPersianYear() + "/" + (finishPersianCalendar.getPersianMonth()+1) + "/" + finishPersianCalendar.getPersianDay();

        dateTextView.setText(date);
        dpd = null;
    }
}
