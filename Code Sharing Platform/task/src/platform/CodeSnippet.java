package platform;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Entity(name="snippet")
public class CodeSnippet implements Cloneable{
    @Column
    private String code;

    @Column
    private String date;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique = true, name = "uuid", nullable = false)
    private String uuid = UUID.randomUUID().toString();

    @Column
    private int time;

    @Column
    private int views;

    @Column
    private boolean isRestrictionPresent;

    CodeSnippet(){
        new CodeSnippet(null, null, 0, 0);
    }

    CodeSnippet (String code, String date, int time, int views) {
        this.code = code;
        this.date = date;
        this.time = time;
        this.views = views;
        this.isRestrictionPresent = (views > 0);
    }

    public String getUuid() {
        return uuid;
    }

    public String getCode() {
        return code;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "CodeSnippet{date: " + date + ", id:" + uuid +", code: " + code + "}";
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getId() {
        return id;
    }

    public boolean viewCodeSnippet () {
        if (views > 0) {
            views--;
            if (views == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean checkTime () {
        if (time > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date firstDate = null;
            Date secondDate = null;
            try {
                firstDate = new Date();
                secondDate = sdf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
            long diff = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            time = (int) (time - diff);
            if (time > 0) {
                return false;
            } else {
                time = 0;
                return true;
            }
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public boolean isRestrictionPresent() {
        return isRestrictionPresent;
    }

    public void setRestrictionPresent(boolean restrictionPresent) {
        isRestrictionPresent = restrictionPresent;
    }
}
