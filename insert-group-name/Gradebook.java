import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * A helper class for your gradebook
 * Some of these methods may be useful for your program
 * You can remove methods you do not need
 * If you do not wish to use a Gradebook object, don't
 */
public class Gradebook {
	
	LinkedHashMap<String, ArrayList<String>> gradebook;
	LinkedHashMap<String, ArrayList<String>> assignments;
	private AES256 aes; 
	private String key;
	
	public String key() {
		return new String(key);
	}
	
	public void tamperedError() throws Exception {
		throw new Exception("Gradebook file has been tampered.");
	}
	
	public boolean isTampered() {
		String[] studRow = gradebook.keySet().toArray(new String[size()]);
		String[] assRow = assignments.keySet().toArray(new String[3]);
		for (int j = 0; j < gradebook.get("Student").size(); j++) {
  			for (int i = 0; i < size(); i++) {
  				String element = gradebook.get(studRow[i]).get(j);
  				if (i == 0) {
  					if (!Pattern.compile("^[a-zA-Z]+? [a-zA-Z]+?$").matcher(element).matches()) {
  						return true;
  					}
  				} else {
  					if (!Pattern.compile("^[0-9]+?|NG$").matcher(element).matches()) {
  						return true;
  					}
  				}
  	  		}
		}
  		for (int j = 0; j < assignments.get("Weight").size(); j++) {
  			for (int i = 0; i < 3; i++) {
  				String element = assignments.get(assRow[i]).get(j);
  				if (i == 0) {
  					if (!Pattern.compile("^[a-zA-Z_0-9]+?$").matcher(element).matches()) {
  						return true;
  					}
  				} else if (i == 1) {
  					if (!Pattern.compile("^[0-9]+?$").matcher(element).matches()) {
  						return true;
  					}
  				} else {
  					if (!Pattern.compile("^0.[0-9]+?$").matcher(element).matches()) {
  						return true;
  					}
  				}
  	  		}
  		}
		return false;
	}
	
	public void createFile(String name, String text) throws IOException {
		File file = new File(name + ".txt");
		file.createNewFile();
		FileWriter fileWriter = new FileWriter(file);
	    PrintWriter printWriter = new PrintWriter(fileWriter);
	    printWriter.print(text);
	    printWriter.close();
	}
	
	/* Read a Gradebook from a file */
	public Gradebook(String filename, String userKey) throws Exception {
		String encry = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
		aes = new AES256();
		aes.setKey(userKey);
		//createFile("decry", aes.decrypt(encry));
		try {
			createFile("decry", aes.decrypt(encry));
			Gradebook read = new Gradebook();
			this.assignments = read.assignments;
			this.gradebook = read.gradebook;
			File myObj = new File("decry.txt");  
	    	Scanner scan = new Scanner(new File("decry.txt"));
	    	scan.useDelimiter(" \\| ");  
	    	scan.next();
	    	scan.next();
	    	scan.next();
	    	while(!(scan.hasNext("\nStudent Name"))) { 
	    		addAssignment(scan.next().trim(), scan.next(), scan.next());
	        }  
	    	for (int i = 0; i < size(); i++) {
	    		scan.next();
	    	}
	    	while(scan.hasNext() && !(scan.hasNext("\nKey"))) {  
	    		String name = scan.next().trim();
	    		String[] studRow = gradebook.keySet().toArray(new String[size()]);
	            addStudent(name);
	            for (int i = 1; i < size(); i++) {
	            	addGrade(name, studRow[i], scan.next().trim());
	            }
	        }  
	    	scan.next();
	    	key = scan.next().trim();
	    	scan.close();   
	    	myObj.delete();
		} catch (Exception e) {
			System.out.println("Better Luck Next Time.");
		}
    	if (!key.equals(userKey)) {
    		throw new Exception("Wrong key.");
    	}
        if (isTampered()) {
        	tamperedError();
        }
	}

	/* Create a new gradebook */
	public Gradebook() {
		gradebook = new LinkedHashMap<String, ArrayList<String>>();
		assignments = new LinkedHashMap<String, ArrayList<String>>();
		gradebook.put("Student", new ArrayList<String>());
		assignments.put("Assignment", new ArrayList<String>());
		assignments.put("Points", new ArrayList<String>());
		assignments.put("Weight", new ArrayList<String>());
		aes = new AES256();
		key = aes.getKey();
	}

	/* return the num of columns for the gradebook */
	public int size() {
		return gradebook.size();
	}

	/* return the num of students */
	public int numStuds() {
		return gradebook.get("Student").size();
	}
	
	public void deleteStudent(String name) throws Exception {
		int index = gradebook.get("Student").indexOf(name);
		if (index == -1) {
			throw new Exception("Student does not exist.");
		}
		gradebook.get("Student").remove(index);
		for (int i = 0; i < assignments.get("Assignment").size(); i++) {
			gradebook.get(assignments.get("Assignment").get(i)).remove(index);
		}
	}
	
	/* Adds a student to the gradebook */
	public void addStudent(String name) throws Exception {
		if (gradebook.get("Student").contains(name)) {
			throw new Exception("Student already exists.");
		}
		gradebook.get("Student").add(name);
		for (int i = 0; i < assignments.get("Assignment").size(); i++) {
			gradebook.get(assignments.get("Assignment").get(i)).add("NG");
		}
	}

	public void deleteAssignment(String assignment) throws Exception {
		if (!gradebook.containsKey(assignment)) {
			throw new Exception("No such assignment.");
		}
		int index = assignments.get("Assignment").indexOf(assignment);
		assignments.get("Assignment").remove(index);
		assignments.get("Points").remove(index);
		assignments.get("Weight").remove(index);
		gradebook.remove(assignment);
	}
	
	/* Adds an assinment to the gradebook */
	public void addAssignment(String assignment, String value, String weight) throws Exception {
		ArrayList<String> temp = assignments.get("Weight");
		Double total = 0.0;
		for (int i = 0; i < temp.size(); i++) {
			total += Double.parseDouble(temp.get(i));
		}
		if (gradebook.containsKey(assignment)) {
			throw new Exception("Assignment already exists.");
		} else if (total + Double.parseDouble(weight) > 1) {
			throw new Exception("Total weight > 1.");
		}
		gradebook.put(assignment, new ArrayList<String>());
		while (gradebook.get(assignment).size() < numStuds()) {
			gradebook.get(assignment).add("NG");
		}
		assignments.get("Assignment").add(assignment);
		assignments.get("Points").add(value);
		assignments.get("Weight").add(weight);
	}

  	/* Adds a grade to the gradebook */
  	public void addGrade(String name, String assignment, String grade) throws Exception {
  		int index = gradebook.get("Student").indexOf(name);
  		if (index == -1 || assignments.get("Assignment").indexOf(assignment) == -1) {
  			throw new Exception("Student or assignment not found.");
  		}
  		gradebook.get(assignment).set(index, grade);
  	}

  	public String toString() {
  		String result = "Assignment | ";
  		String[] assRow = assignments.keySet().toArray(new String[assignments.size()]);
  		for (int i = 1; i < 3; i++) {
  			result += assRow[i] + " | ";
  		}
  		result += "\n";
  		for (int j = 0; j < assignments.get("Weight").size(); j++) {
  			for (int i = 0; i < 3; i++) {
  	  			result += assignments.get(assRow[i]).get(j) + " | ";
  	  		}
  			result += "\n";
  		}
  		result += "Student Name | ";
  		String[] studRow = gradebook.keySet().toArray(new String[size()]);
  		for (int i = 1; i < size(); i++) {
  			result += studRow[i] + " | ";
  		}
  		result += "\n";
  		for (int j = 0; j < gradebook.get("Student").size(); j++) {
  			for (int i = 0; i < size(); i++) {
  	  			result += gradebook.get(studRow[i]).get(j) + " | ";
  	  		}
  			result += "\n";
  		}
  		result += "Key | " + key + "\n";
  		return aes.encrypt(result);
  	}
  	
  	public static void main(String[] args) {
		//sample test run
  		//Gradebook book;
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}
}
