package lab.infoworks.libshared.util;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class HourGlass {

    public enum DurationIn{
        MilliSeconds,
        Seconds,
        Minutes,
        Hours
    }

    private Date future;
    public static Integer BUFFER_TIME_IN_SECOND = 50;
    private boolean isConstant;

    public HourGlass() {
        this.isConstant = true;
    }

    public HourGlass(Date initialTime, int durationInMilliSec) {
        Calendar current = Calendar.getInstance();
        current.setTime(initialTime);
        int durationInMinute = ((durationInMilliSec / 1000) / 60);
        current.add(Calendar.MINUTE, durationInMinute);
        this.future = current.getTime();
        this.isConstant = false;
    }

    public Long timeToLive(DurationIn timeIn){

        if (this.isConstant) return BUFFER_TIME_IN_SECOND.longValue();

        long diffInMillis = Math.abs(future.getTime() - new Date().getTime());
        long diff;
        switch (timeIn){
            case Seconds:
                diff = TimeUnit.SECONDS.convert(diffInMillis, TimeUnit.MILLISECONDS);
                diff += BUFFER_TIME_IN_SECOND;
                break;
            case Minutes:
                diff = TimeUnit.MINUTES.convert(diffInMillis, TimeUnit.MILLISECONDS);
                break;
            case Hours:
                diff = TimeUnit.HOURS.convert(diffInMillis, TimeUnit.MILLISECONDS);
                break;
            default:
                diff = diffInMillis;
        }
        return diff;
    }

}
