package org.hssh.common;

/**
 *
 * Created by hssh on 2017/2/16.
 */
public class ContextHolder {

    private static final ThreadLocal<String> holder = new ThreadLocal<>();

    public static void setHolder(String dataSourceName) {
        holder.set(dataSourceName);
    }

    public static String getHolder() {
        return holder.get();
    }

    public static void removeHolder() {
        holder.remove();
    }

}
