package com.gxx.ordering_platform.xpyunSDK.util;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;

/**
 * 小票格式化器
 *
 * @author Gxx
 * @date 2021年3月19日
 */
public class NoteFormatter {

	private static final Integer ROW_MAX_CHAR_LEN = 32;
    private static final Integer MAX_NAME_CHAR_LEN = 20;
    private static final Integer LAST_ROW_MAX_NAME_CHAR_LEN = 16;
    private static final Integer MAX_QUANTITY_CHAR_LEN = 6;
    private static final Integer MAX_PRICE_CHAR_LEN = 6;
    private static String orderNameEmpty = StringUtils.repeat(" ", MAX_NAME_CHAR_LEN);

    /**
     * 格式化菜品列表（用于58mm打印机）
     * 注意：默认字体排版，若是字体宽度倍大后不适用
     * 58mm打印机一行可打印32个字符 汉子按照2个字符算
     * 分3列： 名称20字符一般用16字符4空格填充  数量6字符  单价6字符，不足用英文空格填充 名称过长换行
     * Format the dish list (for 58 mm printer)
     * Note: this is  default font typesetting, not applicable if the font width is doubled
     * The 58mm printer can print 32 characters per line
     * Divided into 3 columns: name(20 characters), quanity(6 characters),price(6 characters)
     * The name column is generally filled with 16  your characters and 4 spaces
     * Long name column will cause auto line break
     *
     * @param foodName 菜品名称
     * @param quantity 数量
     * @param price 价格
     * @throws Exception
     */
    public static String formatPrintOrderItem(String foodName, Integer quantity, Double price) throws Exception {
        StringBuilder builder = new StringBuilder();
        byte[] itemNames = foodName.getBytes("GBK");
        String quanityStr = quantity.toString();
        byte[] itemQuans = quanityStr.getBytes("GBK");
        String priceStr = roundByTwo(price);
        byte[] itemPrices = (priceStr).getBytes("GBK");

        builder.append(foodName);
        Integer mod = itemNames.length % ROW_MAX_CHAR_LEN;

        if (mod <= LAST_ROW_MAX_NAME_CHAR_LEN) {
            // 在同一行,留4个英文字符的空格
            //if  in the same row, fill with 4 spaces at the end of name column
            builder.append(StringUtils.repeat(" ", (MAX_NAME_CHAR_LEN - mod)));
            builder.append(quanityStr).append(StringUtils.repeat(" ", (MAX_QUANTITY_CHAR_LEN - itemQuans.length)));
            builder.append(priceStr).append(StringUtils.repeat(" ", (MAX_PRICE_CHAR_LEN - itemPrices.length)));
        } else {
            // 另起新行
            // new line
            builder.append("<BR>");
            builder.append(orderNameEmpty);
            builder.append(quanityStr).append(StringUtils.repeat(" ", (MAX_QUANTITY_CHAR_LEN - itemQuans.length)));
            builder.append(priceStr).append(StringUtils.repeat(" ", (MAX_PRICE_CHAR_LEN - itemPrices.length)));
        }
        builder.append("<BR>");
        return builder.toString();
    }

    /**
     * 将double格式化为指定小数位的String，不足小数位用0补全 (小数点后保留2位)
     * Format the double as a String with specified decimal places, fill in with 0 if the decimal place is not enough
     *
     * @param v - 需要格式化的数字  Number to be formatted
     * @return 返回指定位数的字符串 Returns a string with a specified number of digits
     */
    public static String roundByTwo(double v) {
        return roundByScale(v, 2);
    }

    /**
     * 将double格式化为指定小数位的String，不足小数位用0补全
     * Format the double as a String with specified decimal places, and use 0 to complete the decimal places
     *
     * @param v     - 需要格式化的数字  number to be formatted
     * @param scale - 小数点后保留几位  places after the decimal point
     * @return 返回指定位数的字符串 Returns a string with a specified number of digits
     */
    public static String roundByScale(double v, int scale) {
        if (scale == 0) {
            return new DecimalFormat("0").format(v);
        }
        String formatStr = "0.";
        for (int i = 0; i < scale; i++) {
            formatStr = formatStr + "0";
        }
        return new DecimalFormat(formatStr).format(v);
    }
}
