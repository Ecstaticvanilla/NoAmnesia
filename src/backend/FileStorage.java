/*
* Write assignments to assignments.json.
* Read assignments from file into a List<Assignment>.
* Handle cases where file doesn’t exist yet.
*/

package backend;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

public class FileStorage {

    // Convert JSONObject to AssignmentComponent
    public static AssignmentComponent toAssignment(JSONObject obj) {
        String experimentName = obj.getString("experimentName");
        String subjectName = obj.getString("subjectName");
        Date submissionDate = Date.valueOf(obj.getString("submissionDate"));
        Time submissionTime = Time.valueOf(obj.getString("submissionTime"));
        return new AssignmentComponent(experimentName, subjectName, submissionDate, submissionTime);
    }

    // Add to pending assignment
    public void addAssignment(AssignmentComponent x) {
        List<AssignmentComponent> assignments = retrievePendingAssignments(); 
        assignments.add(x);
        saveAssignments(assignments, "Data/Pending.json");
    }

    // Add to submitted assignment
    public void submittedAssignment(AssignmentComponent x) {
        List<AssignmentComponent> assignments = retrieveSubmittedAssignments(); 
        assignments.add(x);
        saveAssignments(assignments, "Data/Submitted.json");
    }

    // Save list as JSON array
    private void saveAssignments(List<AssignmentComponent> assignments, String filePath) {
        JSONArray array = new JSONArray();
        for (AssignmentComponent a : assignments) {
            JSONObject obj = new JSONObject();
            obj.put("experimentName", a.getExperimentName());
            obj.put("subjectName", a.getSubjectName());
            obj.put("submissionDate", a.getSubmissionDate().toString());
            obj.put("submissionTime", a.getSubmissionTime().toString());
            array.put(obj);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(array.toString(2)); // pretty print with indentation
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Retrieve pending assignments
    public List<AssignmentComponent> retrievePendingAssignments() {
        return readAssignments("Data/Pending.json");
    }

    // Retrieve submitted assignments
    public List<AssignmentComponent> retrieveSubmittedAssignments() {
        return readAssignments("Data/Submitted.json");
    }

    // Read JSON file into AssignmentComponent list
    private List<AssignmentComponent> readAssignments(String filePath) 
    {
        List<AssignmentComponent> assignments = new ArrayList<>();
        StringBuilder json = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) 
        {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
        } 
        catch (IOException e){
            return assignments;
        }

        try {
            JSONArray array = new JSONArray(json.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                assignments.add(toAssignment(obj));
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return assignments;
    }

    // Remove pending assignment
    public void removePendingAssignment(String experimentName) {
        List<AssignmentComponent> assignments = retrievePendingAssignments();
        boolean removed = assignments.removeIf(a -> a.getExperimentName().equalsIgnoreCase(experimentName));
        if (removed) saveAssignments(assignments, "Data/Pending.json");
    }

    // Remove submitted assignment
    public void removeSubmittedAssignment(String experimentName) {
        List<AssignmentComponent> assignments = retrieveSubmittedAssignments();
        boolean removed = assignments.removeIf(a -> a.getExperimentName().equalsIgnoreCase(experimentName));
        if (removed) saveAssignments(assignments, "Data/Submitted.json");
    }
}
