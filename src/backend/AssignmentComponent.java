/*
* Store assignment name and deadline date.
* Provide getters/setters.
* Convert assignment to/from a string (for file storage).
*/

package backend;

import java.sql.Date;

public class AssignmentComponent {
    String experimentName;
    String subjectName;
    Date submissionDate;
    AssignmentComponent(String experimentName,String subjectName,Date submissionDate){
        this.experimentName = experimentName;
        this.subjectName = subjectName;
        this.submissionDate = submissionDate; 
    }
}
