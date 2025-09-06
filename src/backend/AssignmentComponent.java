/*
* Store assignment name and deadline date.
* Provide getters/setters.
* Convert assignment to/from a string (for file storage).
*/

package backend;

import java.sql.Date;
import java.sql.Time;

public class AssignmentComponent {
    private String experimentName;
    private String subjectName;
    private Date submissionDate;
    private Time submissionTime;

    public AssignmentComponent(String experimentName, String subjectName,
                               Date submissionDate, Time submissionTime) {
        this.experimentName = experimentName;
        this.subjectName = subjectName;
        this.submissionDate = submissionDate;
        this.submissionTime = submissionTime;
    }

    // getters
    public String getExperimentName() {
        return experimentName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public Time getSubmissionTime() {
        return submissionTime;
    }

    // setters (if you want assignments to be editable)
    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public void setSubmissionTime(Time submissionTime) {
        this.submissionTime = submissionTime;
    }

    public String toString() {
    return "{"
        + "\"experimentName\":\"" + experimentName + "\","
        + "\"subjectName\":\"" + subjectName + "\","
        + "\"submissionDate\":\"" + submissionDate.toString() + "\","
        + "\"submissionTime\":\"" + submissionTime.toString() + "\""
        + "}";
    }
}

