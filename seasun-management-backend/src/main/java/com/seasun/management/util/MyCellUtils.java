package com.seasun.management.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MyCellUtils {

    /**
     * 用于将excel表格中列索引转成列号字母，从A对应1开始
     *
     * @param index 列索引
     * @return 列号
     */
    public static String indexToColumn(int index) {
        if (index <= 0) {
            try {
                throw new Exception("Invalid parameter");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        index--;
        String column = "";
        do {
            if (column.length() > 0) {
                index--;
            }
            column = ((char) (index % 26 + (int) 'A')) + column;
            index = (int) ((index - index % 26) / 26);
        } while (index > 0);
        return column;
    }

    public static String trimText(String text) {
        if (text == null) {
            return null;
        }

        String result = text;
        if (text.startsWith("~") || text.startsWith("一")) {
            result = text.substring(1);
        }
        if (text.startsWith("——")) {
            result = text.substring(2);
        }
        if (text.startsWith("---")) {
            result = text.substring(3);
        }
        return result;
    }

    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        String cellValue = "";

        switch (cell.getCellType()) {
            case XSSFCell.CELL_TYPE_STRING:
                cellValue = cell.getStringCellValue().trim();
                if (cellValue.equals("") || cellValue.length() <= 0) {
                    cellValue = "";
                }
                break;
            case XSSFCell.CELL_TYPE_NUMERIC:
                if (String.valueOf(cell.getNumericCellValue()).indexOf("E") == -1) {
                    cellValue = String.valueOf(cell.getNumericCellValue());
                } else {
                    cellValue = new DecimalFormat("#").format(cell.getNumericCellValue());
                }
                break;
            case XSSFCell.CELL_TYPE_FORMULA:
//          cell.setCellType(HSSFCell.CELL_TYPE_STRING);
//          cellValue = String.valueOf(cell.getStringCellValue());
                cellValue = ((XSSFCell) cell).getRawValue();
                break;
            case XSSFCell.CELL_TYPE_BLANK:
                cellValue = "";
                break;
            case XSSFCell.CELL_TYPE_BOOLEAN:
                break;
            case XSSFCell.CELL_TYPE_ERROR:
                break;

            default:
                break;
        }
        return cellValue;
    }


    /**
     * 半角转全角
     *
     * @param input String.
     * @return 全角字符串.
     */
    public static String toSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     *
     * @param input String.
     * @return 半角字符串
     */
    public static String toDBC(String input) {


        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);

            }
        }
        String returnString = new String(c);

        return returnString;
    }


    /**
     * f : 传入值
     * scale：需要保留的小数位数
     */
    public static float formatFloatNumber(float f, int scale) {
        BigDecimal b = new BigDecimal(f);
        float r = b.setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();
        return r;
    }

    // 整型数组中是否包含目标数字
    public static boolean isContainsIntValue(int[] values, int target) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] == target) {
                return true;
            }
        }
        return false;
    }

    public static Float getMoM(Float lastMonthValue, Float twoMonthAgoValue) {
        if (twoMonthAgoValue == 0F || twoMonthAgoValue == null) {
            return null;
        }
        return (lastMonthValue - twoMonthAgoValue) / twoMonthAgoValue * 100f;
    }
}