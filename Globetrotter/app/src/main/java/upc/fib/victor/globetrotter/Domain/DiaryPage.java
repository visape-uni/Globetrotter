package upc.fib.victor.globetrotter.Domain;

import java.util.Calendar;
import java.util.Date;

public class DiaryPage {
    private String uid;
    private String title;
    private String content;
    private Date dateModified;

    public DiaryPage() {
        this.uid = "";
        this.title = "";
        this.content = "";
        dateModified = null;
    }

    public DiaryPage (String uid, String title, String content, Date dateModified) {
        this.uid = uid;
        this.title = title;
        this.content = content;
        this.dateModified = dateModified;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }
}
