package edu.tongji.backend.util;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 All contributors of the project
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */





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
