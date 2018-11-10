package upc.fib.victor.globetrotter.Domain;

import java.util.ArrayList;
import java.util.Date;

public class Publication {
    private String id;
    private String message;
    private String uidUser;
    private Date date;
    private ArrayList<Publication> answers;
    private ArrayList<String> uidLikes;

    public Publication() {
    }

    public Publication(String uidUser, String message, Date date) {
        this.message = message;
        this.uidUser = uidUser;
        this.date = date;
    }

    public Publication(String id, String message, String uidUser, Date date, ArrayList<Publication> answers, ArrayList<String> uidLikes) {
        this.id = id;
        this.message = message;
        this.uidUser = uidUser;
        this.date = date;
        this.answers = answers;
        this.uidLikes = uidLikes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUidUser() {
        return uidUser;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<Publication> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Publication> answers) {
        this.answers = answers;
    }

    public ArrayList<String> getUidLikes() {
        return uidLikes;
    }

    public void setUidLikes(ArrayList<String> uidLikes) {
        this.uidLikes = uidLikes;
    }
}
