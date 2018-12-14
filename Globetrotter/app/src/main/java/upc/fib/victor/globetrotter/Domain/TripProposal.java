package upc.fib.victor.globetrotter.Domain;

import java.util.Date;

public class TripProposal {

    private String id;
    private String message;
    private String uidUser;
    private String userName;
    private Date date;
    private Date iniDate;
    private Date endDate;
    private int budget;
    private String country;

    public TripProposal(){}

    public TripProposal(String message, String uidUser, String userName, Date date, Date iniDate, Date endDate, int budget, String country) {
        this.message = message;
        this.uidUser = uidUser;
        this.userName = userName;
        this.date = date;
        this.iniDate = iniDate;
        this.endDate = endDate;
        this.budget = budget;
        this.country = country;
    }

    public TripProposal(String id, String message, String uidUser, String userName, Date date, Date iniDate, Date endDate, int budget, String country) {
        this.id = id;
        this.message = message;
        this.uidUser = uidUser;
        this.userName = userName;
        this.date = date;
        this.iniDate = iniDate;
        this.endDate = endDate;
        this.budget = budget;
        this.country = country;
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

    public Date getIniDate() {
        return iniDate;
    }

    public void setIniDate(Date iniDate) {
        this.iniDate = iniDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getDuration() {
        long diff = iniDate.getTime() - endDate.getTime();
        return diff/(1000*60*60*24);
    }
}
