package cn.cover.database.parser.mysql.visitor.dm.support;


public class StringUtil {

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}
