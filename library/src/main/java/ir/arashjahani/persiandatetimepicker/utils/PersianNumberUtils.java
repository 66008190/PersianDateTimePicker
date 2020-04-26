package ir.arashjahani.persiandatetimepicker.utils;

/**
 * Created by ArashJahani on 15/09/03.
 */
public class PersianNumberUtils {

    static String[] farsiChars = {"\u06F0", "\u06F1", "\u06F2", "\u06F3", "\u06F4", "\u06F5", "\u06F6", "\u06F7", "\u06F8", "\u06F9", "\u066C", "\u066B",};
    //static String[] arabicChars = {"٠","١","٢","٣","٤","٥","٦","٧","٨","٩"};
    
    public static String toFarsi(String number) {//1232452

        number = number.replaceAll("0", farsiChars[0]);
        number = number.replaceAll("1", farsiChars[1]);
        number = number.replaceAll("2", farsiChars[2]);
        number = number.replaceAll("3", farsiChars[3]);
        number = number.replaceAll("4", farsiChars[4]);
        number = number.replaceAll("5", farsiChars[5]);
        number = number.replaceAll("6", farsiChars[6]);
        number = number.replaceAll("7", farsiChars[7]);
        number = number.replaceAll("8", farsiChars[8]);
        number = number.replaceAll("9", farsiChars[9]);
        number = number.replaceAll(",", farsiChars[10]);
        number = number.replace(".", farsiChars[11]);

        return number;

    }

    public static String toFarsi(Integer number){
        return toFarsi(String.valueOf(number));
    }


    public static String toEnglish(String number) {//1232452

        number = number.replaceAll(farsiChars[0], "0");
        number = number.replaceAll(farsiChars[1], "1");
        number = number.replaceAll(farsiChars[2], "2");
        number = number.replaceAll(farsiChars[3], "3");
        number = number.replaceAll(farsiChars[4], "4");
        number = number.replaceAll(farsiChars[5], "5");
        number = number.replaceAll(farsiChars[6], "6");
        number = number.replaceAll(farsiChars[7], "7");
        number = number.replaceAll(farsiChars[8], "8");
        number = number.replaceAll(farsiChars[9], "9");

        return number;

    }

}
