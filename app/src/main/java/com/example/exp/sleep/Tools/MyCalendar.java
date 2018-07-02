package com.example.exp.sleep.Tools;

import java.io.Serializable;
import java.util.Calendar;

public class MyCalendar implements Serializable {
    private static final long serialVersionUID=1L;
    private Calendar mCalendar;

    public void setCalendar(Calendar mCalendar) {
        this.mCalendar = mCalendar;
    }

    public Calendar getCalendar() {
        return mCalendar;
    }

    public MyCalendar(Calendar calendar){
        mCalendar = calendar;
    }
}
