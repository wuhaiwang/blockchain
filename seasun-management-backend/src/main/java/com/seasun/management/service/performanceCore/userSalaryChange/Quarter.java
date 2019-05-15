package com.seasun.management.service.performanceCore.userSalaryChange;

public class Quarter {
    public int year;
    public int quarter;

    public boolean isNextQuarter(Quarter next) {
        if (this.year == next.year && this.quarter != 4)
            return next.quarter == this.quarter + 1;
        else if (this.quarter == 4)
            return next.year == this.year + 1 && next.quarter == 1;
        else
            return false;
    }

    public boolean isNextQuarter(int year, int quarter) {
        if (this.year == year && this.quarter != 4)
            return quarter == this.quarter + 1;
        else if (this.quarter == 4)
            return year == this.year + 1 && quarter == 1;
        else
            return false;
    }

    public boolean isEarly(Quarter temp) {
        if (this.year < temp.year)
            return true;
        else if (this.year == temp.year)
            return this.quarter < temp.quarter;
        else
            return false;
    }

    public boolean isEqual(Quarter temp) {
        return this.year == temp.year && this.quarter == temp.quarter;
    }

    public boolean equal(int year, int quarter) {
        return this.year == year && this.quarter == quarter;
    }

    public Quarter getNextQuarter() {
        if (this.quarter != 4)
            return new Quarter(this.year, this.quarter + 1);
        else
            return new Quarter(this.year + 1, 1);
    }

    public Quarter(int year, int quarter) {
        this.year = year;
        this.quarter = quarter;
    }
}