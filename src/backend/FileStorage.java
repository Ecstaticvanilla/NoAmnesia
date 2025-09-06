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

public class FileStorage {

    public static AssignmentComponent toAssignment(String json){
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);
        String[] pairs = json.split(",");
        String experimentName = "";
        String subjectName = "";
        Date submissionDate = null;
        Time submissionTime = null;
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2); 
            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim().replace("\"", "");
            switch (key) {
                case "experimentName":
                    experimentName = value;
                    break;
                case "subjectName":
                    subjectName = value;
                    break;
                case "submissionDate":
                    submissionDate = Date.valueOf(value);
                    break;
                case "submissionTime":
                    submissionTime = Time.valueOf(value);
                    break;
            }
        }
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

    // Save a list as JSON array
    private void saveAssignments(List<AssignmentComponent> assignments, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("[\n");
            for (int i = 0; i < assignments.size(); i++) {
                writer.write("  " + assignments.get(i).toString());
                if (i < assignments.size() - 1) writer.write(",");
                writer.write("\n");
            }
            writer.write("]");
        } catch (IOException e) {
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

    // Generic JSON array reader
    private List<AssignmentComponent> readAssignments(String filePath) {
        List<AssignmentComponent> assignments = new ArrayList<>();
        StringBuilder json = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line.trim());
            }
        } 
        catch (IOException e) {
            return assignments; 
        }

        String content = json.toString().trim();
        if (content.startsWith("[") && content.endsWith("]")) {
            content = content.substring(1, content.length() - 1).trim();
            if (!content.isEmpty()) {
                String[] objects = content.split("},");
                for (int i = 0; i < objects.length; i++) {
                    String obj = objects[i].trim();
                    if (!obj.endsWith("}")) obj += "}";
                    assignments.add(toAssignment(obj));
                }
            }
        }
        return assignments;
    }

    // Remove pending assignment by experimentName
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