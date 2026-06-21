package com.shop.admin.util;

public final class PasswordValidator {

    private PasswordValidator() {
    }

    /**
     * 8〜12桁、大文字・小文字・数字・記号のうち3種類以上を含むこと。
     */
    public static boolean isValid(String password) {
        if (password == null || password.length() < 8 || password.length() > 12) {
            return false;
        }
        int types = 0;
        if (password.chars().anyMatch(Character::isUpperCase)) {
            types++;
        }
        if (password.chars().anyMatch(Character::isLowerCase)) {
            types++;
        }
        if (password.chars().anyMatch(Character::isDigit)) {
            types++;
        }
        if (password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch))) {
            types++;
        }
        return types >= 3;
    }

    public static String message() {
        return "パスワードは8〜12桁で、大文字・小文字・数字・記号のうち3種類以上を含めてください";
    }
}
