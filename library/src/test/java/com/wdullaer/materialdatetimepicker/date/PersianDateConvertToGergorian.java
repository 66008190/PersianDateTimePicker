package com.wdullaer.materialdatetimepicker.date;


import com.wdullaer.materialdatetimepicker.utils.PersianCalendar;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created By ArashJahani on 2020/04/13
 */
public class PersianDateConvertToGergorian {

    @Test
    public void testDates(){

        PersianCalendar a=new PersianCalendar();

        a.setPersianDate(1399,0,12);
        assertEquals(a.getPersianShortDate(),"1399/0/12");


    }

}
