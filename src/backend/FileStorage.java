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

    //Add to pending assignment
    public void addAssignment(backend.AssignmentComponent x) {
        String assignmentJson = x.toString(); 
        System.out.println(assignmentJson);
        try (BufferedWriter pendingWriter = new BufferedWriter(new FileWriter("Data/Pending.json", true))) {
            pendingWriter.write(assignmentJson);
            pendingWriter.newLine(); 
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //Add to submitted assignment
    public void submittedAssignment(backend.AssignmentComponent x) {
        String assignmentJson = x.toString(); 
        System.out.println(assignmentJson);
        try (BufferedWriter submittedWriter = new BufferedWriter(new FileWriter("Data/Submitted.json", true))) {
            submittedWriter.write(assignmentJson);
            submittedWriter.newLine(); 
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    //retrieve list of pending assignments
    public List<AssignmentComponent> retrievePendingAssignments() {
        List<AssignmentComponent> assignments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("Data/Pending.json"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    AssignmentComponent obj = toAssignment(line);
                    assignments.add(obj);
                }
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return assignments;
    }
    //retrieve list of submitted assignments
    public List<AssignmentComponent> retrieveSubmittedAssignments() { 
        List<AssignmentComponent> assignments = new ArrayList<>();
        try (BufferedReader submittedReader = new BufferedReader(new FileReader("Data/Submitted.json"))) {
            String line;
            while ((line = submittedReader.readLine()) != null) {
                assignments.add(toAssignment(line));
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }  
        return assignments;
    }

    //remove assignment from pending.json
    public void removePendingAssignment(String experimentName) {
        List<AssignmentComponent> assignments = retrievePendingAssignments();
        boolean removed = assignments.removeIf(a -> a.getExperimentName().equalsIgnoreCase(experimentName));
        if (removed) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Data/Pending.json"))) {
                for (AssignmentComponent a : assignments) {
                    writer.write(a.toString());
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //remove assignment from submission.json
        public void removeSubmittedAssignment(String experimentName) {
        List<AssignmentComponent> assignments = retrieveSubmittedAssignments();
        boolean removed = assignments.removeIf(a -> a.getExperimentName().equalsIgnoreCase(experimentName));
        if (removed) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Data/Submitted.json"))) {
                for (AssignmentComponent a : assignments) {
                    writer.write(a.toString());
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}