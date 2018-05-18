package com.example.testdemo.utils;

/**
 * Created by 那个谁 on 2018/3/7.
 * 奥特曼打小怪兽
 * 作用：枚举
 */

public enum  SortEnum {
    TODAY(0), YESTERDAY(1), THIS_WEEK(2), THIS_MONTH(3), THIS_YEAR(4);

    int sort;

    SortEnum(int sort) {
        this.sort = sort;
    }

    public static SortEnum getSortEnum(int sort) {
        switch (sort) {
            case 0:
                return SortEnum.TODAY;
            case 1:
                return SortEnum.YESTERDAY;
            case 2:
                return SortEnum.THIS_WEEK;
            case 3:
                return SortEnum.THIS_MONTH;
            case 4:
                return SortEnum.THIS_YEAR;
        }
        return SortEnum.TODAY;
    }
}
