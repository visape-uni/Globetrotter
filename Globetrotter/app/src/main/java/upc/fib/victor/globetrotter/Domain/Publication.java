package upc.fib.victor.globetrotter.Domain;

import java.util.ArrayList;
import java.util.Date;

public class Publication {
    private String id;
    private String message;
    private String uidUser;
    private String userName;
    private Date date;
    private ArrayList<String> answers;
    private ArrayList<String> uidLikes;

    public Publication() {
    }

    public Publication(String uidUser, String userName, String message, Date date) {
        this.message = message;
        this.uidUser = uidUser;
        this.userName = userName;
        this.date = date;
        answers = new ArrayList<>();
        uidLikes = new ArrayList<>();
    }

    public Publication(String id, String message, String uidUser, String userName, Date date, ArrayList<String> answers, ArrayList<String> uidLikes) {
        this.id = id;
        this.message = message;
        this.uidUser = uidUser;
        this.userName = userName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }

    public void addComment(String commentId) {
        answers.add(commentId);
    }

    public ArrayList<String> getUidLikes() {
        return uidLikes;
    }

    public void setUidLikes(ArrayList<String> uidLikes) {
        this.uidLikes = uidLikes;
    }

    public void addLike(String uid) {
        if (!uidLikes.contains(uid)) uidLikes.add(uid);
    }

    public void removeLike(String uid) {
        uidLikes.remove(uid);
    }
}
