package com.ezc.aragenPR.webapp.util;

import java.util.ArrayList;
import java.util.List;

public class FinancialYearUtil {
    public static int getFinancialYearStart(int year, int month) {
        return (month >= 4) ? year : year - 1;
    }

    public static List<Integer> getFinancialYears(int currentYear, int numYears) {
        List<Integer> years = new ArrayList<>();
        for (int i = 0; i < numYears; i++) {
            years.add(currentYear - i);
        }
        return years;
    }

}
