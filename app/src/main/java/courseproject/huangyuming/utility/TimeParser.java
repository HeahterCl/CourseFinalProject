package courseproject.huangyuming.utility;

import java.text.DecimalFormat;

/**
 * Created by huangyuming on 17-1-4.
 */

public class TimeParser {
    public static String month2English(int month) {
        switch (month) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return "error";
        }
    }

    public static String zeroPadding(int num, int length) {
        String pattern = "";
        for (int i = 0; i < length; ++i) {
            pattern += '0';
        }
        DecimalFormat df = new java.text.DecimalFormat(pattern);
        return df.format(num);
    }
}
