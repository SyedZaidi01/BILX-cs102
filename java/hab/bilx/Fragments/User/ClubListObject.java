package hab.bilx.Fragments.User;

/**
 * The club list object
 * @author Hanzallah Burney
 */


public class ClubListObject {
    private String sentBy, subject;
    private String uri;

    public ClubListObject(String sentBy, String subject, String uri) {
        this.sentBy = sentBy;
        this.subject = subject;
        this.uri = uri;
    }
    /*
     *  @author Hanzallah Burney
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

    public String getClubIcon() {
        return uri;
    }

    public void setClubIcon(String content) {
        this.uri = uri;
    }
}
/*
 *  @author Hanzallah Burney
 */