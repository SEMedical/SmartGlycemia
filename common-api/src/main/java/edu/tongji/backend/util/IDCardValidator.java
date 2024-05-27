package edu.tongji.backend.util;

public class IDCardValidator {
    // 加权因子
    private static final int[] WEIGHTS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    // 校验码对照表
    private static final char[] CHECK_CODE = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    public static boolean validate(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            return false;
        }

        // 前17位
        String front17 = idCard.substring(0, 17);
        // 第18位校验码
        char checkBit = idCard.charAt(17);

        // 计算校验码
        char calculatedCheckBit = calculateCheckBit(front17);

        // 比较校验码
        return checkBit == calculatedCheckBit;
    }

    private static char calculateCheckBit(String front17) {
        int sum = 0;
        for (int i = 0; i < front17.length(); i++) {
            sum += (front17.charAt(i) - '0') * WEIGHTS[i];
        }
        int mod = sum % 11;
        return CHECK_CODE[mod];
    }
}
