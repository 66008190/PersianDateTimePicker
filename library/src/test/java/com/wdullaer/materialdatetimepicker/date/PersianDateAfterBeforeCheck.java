package com.wdullaer.materialdatetimepicker.date;


import com.wdullaer.materialdatetimepicker.utils.PersianCalendar;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Created By ArashJahani on 2020/04/13
 */
public class PersianDateAfterBeforeCheck {

    @Test
    public void testDates(){

        PersianCalendar a=new PersianCalendar();
        PersianCalendar b=new PersianCalendar();

        a.setPersianDate(1390,11,3);
        assertEquals(a.before(b),true);

        a.setPersianDate(1399,11,24);
        assertEquals(a.before(b),false);

        a.setPersianDate(1499,5,25);
        assertEquals(a.before(b),false);


        a.setPersianDate(1390,11,3);
        assertEquals(a.after(b),false);

        a.setPersianDate(1399,11,24);
        assertEquals(a.after(b),true);

        a.setPersianDate(1499,0,26);
        assertEquals(a.after(b),true);

    }

}
