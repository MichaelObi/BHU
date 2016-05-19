package net.devdome.bhu.app.utility;

public class TimeUtils {

    /**
     * Convert minutes to readable duration
     */
    public static String minutesToReadableDuration(Long minutes) {
        if (minutes == 0) {
            return "0 minutes";
        }
        int hours = (int) (minutes / 60);
        int remainingMinutes = (int) (minutes % 60);
        StringBuilder stringBuilder = new StringBuilder();
        if (hours > 0) {
            stringBuilder.append(hours).append(" hours");
        }
        if (remainingMinutes > 0) {
            stringBuilder.append(remainingMinutes).append(", minute");
            if(remainingMinutes > 1) {
                stringBuilder.append("s");
            }
        }
        return stringBuilder.toString();
    }
}
