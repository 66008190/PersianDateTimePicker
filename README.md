# Persian Material Date And Time Picker - Select a time/date in style


این کتابخانه ابزاری است برای پیاده سازی هر چه ساده تر تقویم و ساعت شمار فارسی و کاملا قابل شخصی سازی.

من تمام تلاشم را میکنم تا در بروزرسانی های مداوم ویژگی های بیشتری اضافه کرده و تعداد باگ ها رو به حداقل برسونم.

لطفا با مشارکت و نظراتتون من رو در این امر حمایت کنید.

پشتیبانی از نسخه اندروید ۴.۱(۱۶) و بالاتر - ۹۹.۶ درصد دیوایس های اندرویدی


Features | Range Picker | Date Picker
--- | --- | ---
![Features](https://github.com/arash-jahani/ScreenShots/blob/master/datePicker/s1.jpg) | ![Range Picker](https://github.com/arash-jahani/ScreenShots/blob/master/datePicker/s2.jpg)| ![Date Picker](https://github.com/arash-jahani/ScreenShots/blob/master/datePicker/s3.jpg)


## Table of Contents
1. [Setup](#setup)
2. [Using Persian Date](#using-material-datetime-pickers)
1. [Implement Listeners](#implement-an-ontimesetlistenerondatesetlistener)
2. [Create Pickers](#create-a-timepickerdialogdatepickerdialog-using-the-supplied-factory)
3. [Additional Options](#additional-options)
4. [FAQ](#faq)
5. [Potential Improvements](#potential-improvements)
6. [License](#license)


## Setup
 The easiest way to add the Persian Material DateTime Picker library to your project is by adding it as a dependency to your `build.gradle`
```groovy
dependencies {
    implementation 'io.github.arash-jahani:persaindatetimepicker:0.1'
}
```

You may also add the library as an Android Library to your project. All the library files live in ```library```.

The library also uses some Java 8 features, which Android Studio will need to transpile. This requires the following stanza in your app's `build.gradle`.
See https://developer.android.com/studio/write/java8-support.html for more information on Java 8 support in Android.
```groovy
android {
  ...
  // Configure only for each module that uses Java 8
  // language features (either in its source code or
  // through dependencies).
  compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
  }
}
```

## Using Persian Date/Time Pickers

For a basic implementation, you'll need to

1. Implement an `OnDateSetListener`
2. Create a `PersianDatePickerDialog` using the supplied factory
3. Theme the pickers

### Implement an `OnDateSetListener`
In order to receive the date set in the picker, you will need to implement the 
`OnDateSetListener` interfaces. Typically this will be the `Activity` or `Fragment` that creates the Pickers. The callbacks use the same API as the standard Android pickers.
```java


 @Override
    public void onDateSet(PersianDatePickerDialog view, PersianCalendar persianCalendar) {
        String date = "You picked the following date: " +
                +persianCalendar.getPersianYear() + "/" + (persianCalendar.getPersianMonth()+1) + "/" + persianCalendar.getPersianDay();
        dateTextView.setText(date);
    }

    @Override
    public void onRangeDateSet(PersianDatePickerDialog view, PersianCalendar startPersianCalendar, PersianCalendar finishPersianCalendar) {
        String date = "You picked the following range date: " +
                +startPersianCalendar.getPersianYear() + "/" + (startPersianCalendar.getPersianMonth()+1) + "/" + startPersianCalendar.getPersianDay()
                + "--" +
                +finishPersianCalendar.getPersianYear() + "/" + (finishPersianCalendar.getPersianMonth()+1) + "/" + finishPersianCalendar.getPersianDay();

        dateTextView.setText(date);
    }
```

### Create a `PersianDatePickerDialog` using the supplied factory
You will need to create a new instance of `PersianDatePickerDialog` using the static `newInstance()` method, supplying proper default values and a callback. Once the dialogs are configured, you can call `show()`.
```java
       //as global
       private PersianDatePickerDialog dpd;
       .
       .
       .
       PersianCalendar persianCalendar = new PersianCalendar();
            if (dpd == null) {
                dpd = PersianDatePickerDialog.newInstance(
                        DatePickerFragment.this,
                        persianCalendar.getPersianYear(),
                        persianCalendar.getPersianMonth(),
                        persianCalendar.getPersianDay()
                );
            }
// If you're calling this from a support Fragment
pdpd.show(getFragmentManager(), "Datepickerdialog");
// If you're calling this from an AppCompatActivity
// pdpd.show(getSupportFragmentManager(), "PersianDatePickerDialog");
```

## Additional Options

Please Check Sample Code if You Want to See More....

### [All] `setOkBackgroundColor(colorId)`
```java
dpd.setOkBackgroundColor(R.color.button_color);
```
### [All] `setOkColor(colorId)`

```java
dpd.setOkColor(R.color.white);
```
### [All] `setOkDaysNumberHintText(stringId)`

```java
dpd.setOkDaysNumberHintText(R.string.day);
```
### [All] `RangeDateColor`

```java
dpd.setStartDateColor("#459efa");
            dpd.setFinishDateColor("#f5f5f5");
            dpd.setHighlightColor("#459efa");
```
### [All] `available Range Date`

```java
dpd.setYearMonthRange(persianCalendar.getPersianYear(), 0,
                    persianCalendar.getPersianYear()+1,2);
```
### [All] `setEnableRangePicker`

```java
dpd.setEnableRangePicker(true);
```

### [DatePickerDialog] `setHighlightedDays(Calendar[] days)`  
You can pass a `Calendar[]` of days to highlight. They will be rendered in bold. You can tweak the color of the highlighted days by overwriting `mdtp_date_picker_text_highlighted`

### [DatePickerDialog] `setSelectableDays(Calendar[] days)`  
You can pass a `Calendar[]` to the `DatePickerDialog`. The values in this list are the only acceptable dates for the picker. It takes precedence over `setMinDate(Calendar day)` and `setMaxDate(Calendar day)`

### [DatePickerDialog] `setDisabledDays(Calendar[] days)`  
The values in this `Calendar[]` are explicitly disabled (not selectable). This option can be used together with `setSelectableDays(Calendar[] days)`: in case there is a clash `setDisabledDays(Calendar[] days)` will take precedence over `setSelectableDays(Calendar[] days)`



### [All] `setAccentColor(String color)` and `setAccentColor(int color)`
Set the accentColor to be used by the Dialog. The String version parses the color out using `Color.parseColor()`. The int version requires a ColorInt bytestring. It will explicitly set the color to fully opaque.

### [All] `setOkColor()` and `setCancelColor()`
Set the text color for the OK or Cancel button. Behaves similar to `setAccentColor()`

### [TimePickerDialog] `setTitle(String title)`  
Shows a title at the top of the `TimePickerDialog`

### [DatePickerDialog] `setTitle(String title)`
Shows a title at the top of the `DatePickerDialog` instead of the day of the week

### [All] `setOkText()` and `setCancelText()`  
Set a custom text for the dialog Ok and Cancel labels. Can take a resourceId of a String. Works in both the DatePickerDialog and TimePickerDialog

### [DatePickerDialog] `setMinDate(Calendar day)`
Set the minimum valid date to be selected. Date values before this date will be deactivated

### [DatePickerDialog] `setMaxDate(Calendar day)`
Set the maximum valid date to be selected. Date values after this date will be deactivated


### [All] `vibrate(boolean vibrate)`  
Set whether the dialogs should vibrate the device when a selection is made. This defaults to `true`.

### [All] `dismissOnPause(boolean dismissOnPause)`  
Set whether the picker dismisses itself when the parent Activity is paused or whether it recreates itself when the Activity is resumed.

### [DatePickerDialog] `autoDismiss(boolean autoDismiss)`
If set to `true` will dismiss the picker when the user selects a date. This defaults to `false`.

### [DatePickerDialog] `setScrollOrientation(ScrollOrientation scrollOrientation)` and `getScrollOrientationi()`
Determines whether months scroll `Horizontal` or `Vertical`. Defaults to `Horizontal` for the v2 layout and `Vertical` for the v1 layout

## FAQ

Using the following snippet in your apps `build.gradle` file you can exclude this library's transitive appcompat library dependency from being installed.

```groovy
implementation ('io.github.arash-jahani:persaindatetimepicker:0.1') {
        exclude group: 'androidx.appcompat'
        exclude group: 'androidx.recyclerview'
}
```

MaterialDateTimepicker uses the following androidx libraries:

```groovy
implementation 'androidx.appcompat:appcompat:1.0.2'
implementation 'androidx.recyclerview:recyclerview:1.0.0'
```


## License
    Copyright (c) 2015 Wouter Dullaert

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
