package hab.bilx.Fragments.User;

import android.graphics.Color;

import com.alamkanak.myweekview.WeekViewEvent;

import java.util.Calendar;

/**
 * The user activities object
 * @author Hanzallah Burney
 */


public class UserActivitiesObject {
    private String activityName,clubName, ge, time, date, location, language, description;
    private WeekViewEvent wve;
    /*
     **  @author Hanzallah Burney
     */

    public UserActivitiesObject(String activityName,String clubName, String ge, String time, String date, String location, String language, String description) {
        this.activityName = activityName;
        this.clubName = clubName;
        this.ge = ge;
        this.time = time;
        this.date = date;
        this.location = location;
        this.language = language;
        this.description = description;
        /*
         **  @author Hanzallah Burney
         */

        try {
            String year = date.substring(date.lastIndexOf("/") + 1, date.length()).trim();
            String day = date.substring(0, date.indexOf('/')).trim();
            String month = date.substring(date.indexOf('/') + 1, date.lastIndexOf('/')).trim();


            String hour = time.substring(0, time.indexOf(':')).trim();

            Calendar startTime = Calendar.getInstance();
            startTime.set( Integer.parseInt(year), Integer.parseInt(month)-1,
                    Integer.parseInt(day), Integer.parseInt(hour), 0);
            Calendar endTime = (Calendar)startTime.clone();
            endTime.add(Calendar.HOUR_OF_DAY, 1);

            String calActName = activityName.substring(activityName.indexOf(':') + 1,
                    activityName.length()).trim();

            wve = new WeekViewEvent( 1, calActName, startTime, endTime );
            wve.setColor( Color.parseColor("#01579B"));
        } catch (Exception e ){

        }

    }

    /*
     **  @author Hanzallah Burney
     */

    public WeekViewEvent getWve(){
        return this.wve;
    }


    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getGe() {
        return ge;
    }

    public void setGe(String ge) {
        this.ge = ge;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
