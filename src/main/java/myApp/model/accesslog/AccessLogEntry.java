package myApp.model.accesslog;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "ACCESS_LOG")
public class AccessLogEntry implements Serializable {
// ------------------------------ FIELDS ------------------------------

    private static final long serialVersionUID = 8727628444649383575L;

    public enum Reason {
        NONE, UNKNOWN_USERNAME, WRONG_PASSWORD, ACCOUNT_LOCKED, ACCOUNT_DISABLED, GENERIC_EXCEPTION, UNCATCHED_EXCEPTION
    }

    public enum Result {
        SUCCESS, FAILURE
    }

    public enum Event {
        LOGIN, LOGOUT, SESSION_EXPIRATION
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Event event;

    @NotNull
    private Date timestamp;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Result result;

    @Enumerated(EnumType.STRING)
    private Reason reason;

    @NotNull
    private String username;

    private String fullUserName;

    protected AccessLogEntry() {
    }

    protected AccessLogEntry(Event event, Date timestamp, Result result, Reason reason, String username, String fullUserName) {
        this.event = event;
        this.timestamp = timestamp;
        this.result = result;
        this.reason = reason;
        this.username = username;
        this.fullUserName = fullUserName;
    }

    public static AccessLogEntry forSuccessfulLogin(String username, String fullUserName) {

        return new AccessLogEntry(Event.LOGIN, Calendar.getInstance().getTime(), Result.SUCCESS, Reason.NONE, username, fullUserName);
    }

    public static AccessLogEntry forFailedLogin(String username, Reason reason) {

        return new AccessLogEntry(Event.LOGIN, Calendar.getInstance().getTime(), Result.FAILURE, reason, username, "");
    }

    public static AccessLogEntry forSuccessfulLogout(String username, String fullUserName) {

        return new AccessLogEntry(Event.LOGOUT, Calendar.getInstance().getTime(), Result.SUCCESS, Reason.NONE, username, fullUserName);
    }

    public static AccessLogEntry forSessionExpiration(String username, String fullUserName) {

        return new AccessLogEntry(Event.SESSION_EXPIRATION, Calendar.getInstance().getTime(), Result.SUCCESS, Reason.NONE, username, fullUserName);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullUserName() {
        return fullUserName;
    }

    public void setFullUserName(String fullUserName) {
        this.fullUserName = fullUserName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccessLogEntry that = (AccessLogEntry) o;

        if (event != null ? !event.equals(that.event) : that.event != null) return false;
        if (result != null ? !result.equals(that.result) : that.result != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result1 = event != null ? event.hashCode() : 0;
        result1 = 31 * result1 + (timestamp != null ? timestamp.hashCode() : 0);
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        result1 = 31 * result1 + (username != null ? username.hashCode() : 0);
        return result1;
    }

    @Override
    public String toString() {
        return "AccessLogEntry{" +
                "id=" + id +
                ", event='" + event + '\'' +
                ", timestamp=" + timestamp +
                ", result='" + result + '\'' +
                ", reason='" + reason + '\'' +
                ", username='" + username + '\'' +
                ", fullUserName='" + fullUserName + '\'' +
                '}';
    }
}