package hab.bilx.Fragments.Club;

/**
 * The club notification object for recycler view
 * @author Hanzallah Burney
 */

public class ClubNotificationObject {
    private String sentBy, subject, content;

    public ClubNotificationObject(String sentBy, String subject, String content) {
        this.sentBy = sentBy;
        this.subject = subject;
        this.content = content;
    }
    /*
     **  @author Hanzallah Burney
     */

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
/*
 **  @author Hanzallah Burney
 */
