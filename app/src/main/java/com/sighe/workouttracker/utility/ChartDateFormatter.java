package com.sighe.workouttracker.utility;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by dad on 10/26/2016.
 */

public class ChartDateFormatter implements IAxisValueFormatter {
    private List<String> mValues;

    public ChartDateFormatter(List<String> values) {
        this.mValues = values;

    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        return mValues.get((int) value);
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
