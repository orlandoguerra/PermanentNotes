package com.savior.notes.permanentnotes;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Orlando on 3/26/2017.
 */

public class Util {

    public static String getDateString(){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        return sdfDate.format(now);
    }
}
