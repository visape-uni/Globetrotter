package upc.fib.victor.globetrotter.Domain;

import java.util.Date;

public class Recommendation {

    private String idInterestPoint;
    private String uid;
    private String comment;
    private Date date;
    private boolean visited;

    public Recommendation() {
    }

    public Recommendation(String idInterestPoint, String uid, String comment, Date date, boolean visited) {
        this.idInterestPoint = idInterestPoint;
        this.uid = uid;
        this.comment = comment;
        this.date = date;
        this.visited = visited;
    }

    public String getIdInterestPoint() {
        return idInterestPoint;
    }

    public void setIdInterestPoint(String idInterestPoint) {
        this.idInterestPoint = idInterestPoint;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}
