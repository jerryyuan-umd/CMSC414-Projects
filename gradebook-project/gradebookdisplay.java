import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Prints out a gradebook in a few ways. Primarily it can print a specific student's grades,
 * print all the student grades for a specific assignment, or print all the student final grades.
 * The assignment and final grades can be further custumized to print in alphabetical order or
 * grade order. Also includes a generic print_Gradebook function (for now) for debugging, but
 * that is likely to be deleted soon.
 */
public class gradebookdisplay {
  static Gradebook book;
  static TreeMap<Student, Integer> roster;
  static TreeMap<Student,Double> final_grades;

  static class Student implements Comparable<Student> { // created Student object to make sorting easier
    String f_name;
    String l_name;

    private Student(String first, String last) { // creates new Student object
      f_name = first;
      l_name = last;
    }

    @Override
    public int compareTo(gradebookdisplay.Student other_student) { // allows for students to be sorted by last name
      if (l_name.compareTo(other_student.l_name) == 0) { // sorts by first name in case of same last name
        return f_name.compareTo(other_student.f_name);
      } else {
      return l_name.compareTo(other_student.l_name);
      }
    }
  }

  private static void print_Gradebook(Gradebook gbook) { //I'm guessing this method is for debugging
    /*
    for(int i = 0; i < num_assigment; i++) {
      dump_assignment();
      System.out.println("----------------\n");
    }
    */

    System.out.println(gbook.toString()); //I guess print to stdout??

    return;
  }

  private static void print_Assignment(String[] args) {
    String assignment = null;
    String order = null;

    for (int i = 5; i < args.length; i++) { // parses rest of command line
      if (args[i].compareTo("-AN") == 0) {
        if (args[i+1].matches("^[a-zA-Z0-9]+$")) { // ensures names are purely alphanumeric characters (letters + numbers)
          assignment = args[i+1]; // saves most recent assignment name
        } else {
          error();
        }
        i++; // goes to next command, skips over given assignment name
      } else if (order == null) {
        if (args[i].compareTo("-A") == 0 || args[i].compareTo("-G") == 0) {
          order = args[i];
        } else {
          error();
        }
      } else {
        if (order.compareTo(args[i]) != 0) {
          error();
        }
      }
    }

    if (order.compareTo("-A") == 0) { // checks for alphabetical order
      print_alphabet_order(assignment);
    } else if (order.compareTo("-G") == 0) { // checks for grade order
      print_grade_order(assignment);
    } else {
      error();
    }

    return;
  }

  private static void print_Student(String[] args) {
    String f_name = null;
    String l_name = null;
    int student_num;

    for (int i = 5; i < args.length; i++) { // parses rest of command line
      if (args[i].compareTo("-FN") == 0) {
        if (args[i+1].matches("^[a-zA-Z]+$")) { // ensures names are purely alphabetic characters
          f_name = args[i+1]; // saves most recent first name
        } else {
          error();
        }
        i++; // goes to next command, skips over given first name
      } else if (args[i].compareTo("-LN") == 0) {
        if (args[i+1].matches("^[a-zA-Z]+$")) { // ensures names are purely alphabetic characters
          l_name = args[i+1]; // saves most recent last name
        } else {
          error();
        }
        i++; // goes to next command, skips over given last name
      } else {
        error();
      }
    }

    student_num = book.gradebook.get("Student").indexOf(f_name + " " + l_name); // search for student
    if (student_num < 0) { // confirm student exists
      error();
    }

    for (String str : book.gradebook.keySet()) { // print student's grades
      if (str.compareTo("Student") != 0) { // skips student names and starts with assignment names
        if (book.gradebook.get(str).get(student_num).compareTo("NG") != 0) { // only prints assignments with added grades
          System.out.println("(" + str + ", " + book.gradebook.get(str).get(student_num) + ")");
        }
      }
    }

    return;
  }

  private static void print_Final(String[] args){
    for (int i = 5; i < args.length; i++) { // ensures that order argument can be made multiple times but not change
      if (args[5].compareTo(args[i]) != 0) {
        error();
      }
    }
    calculate_final_grades();
    if (args[5].compareTo("-A") == 0) { // checks for alphabetical order
      print_alphabet_order();
    } else if (args[5].compareTo("-G") == 0) { // checks for grade order
      print_grade_order();
    } else {
      error();
    }
    return;
  }

  private static void print_alphabet_order(String assignment) { // called by print_assignment
    for (Student std : roster.navigableKeySet()) { // print assignment information for each student
      if (book.gradebook.get(assignment).get(roster.get(std)).compareTo("NG") != 0) {
        System.out.println("(" + std.l_name + ", " + std.f_name + ", " + book.gradebook.get(assignment).get(roster.get(std)) + ")");
      }
    }
  }

  private static void print_grade_order(String assignment) { // called by print_assignment
    ArrayList<Integer> grades = new ArrayList<Integer>();
    ArrayList<Student> students = new ArrayList<Student>();
    int max_grade;
    int printed_name;
    int beginning_size;

    for (Student std : roster.keySet()) { // pair up students with their grades
      if (book.gradebook.get(assignment).get(roster.get(std)).compareTo("NG") != 0) {
        students.add(std);
        grades.add(Integer.parseInt(book.gradebook.get(assignment).get(roster.get(std))));
      }
    }

    beginning_size = students.size();
    for (int i = 0; i < beginning_size; i++) {  // finds max grade, prints name, deletes name, searches again
      max_grade = 0;
      printed_name = -1;
      for (int j = 0; j < students.size(); j++) {
        if (grades.get(j) >= max_grade) {
          max_grade = grades.get(j);
          printed_name = j;
        }
      }
      System.out.println("(" + students.get(printed_name).l_name + ", " + students.get(printed_name).f_name + ", " + max_grade + ")");
      students.remove(printed_name);
      grades.remove(printed_name);
    }
  }

  private static void print_alphabet_order() { // called by print_final
    for (Student std : final_grades.navigableKeySet()) { // print final grade information for each student
      System.out.println("(" + std.l_name + ", " + std.f_name + ", " + final_grades.get(std).toString() + ")");
    }
  }

  private static void print_grade_order() { // called by print_final
    ArrayList<Double> grades = new ArrayList<Double>();
    ArrayList<Student> students = new ArrayList<Student>();
    double max_grade;
    int printed_name;
    int beginning_size;

    for (Student std : roster.keySet()) { // pair up students with their grades
      students.add(std);
      grades.add(final_grades.get(std));
    }

    beginning_size = students.size();
    for (int i = 0; i < beginning_size; i++) {  // finds max grade, prints name, deletes name, searches again
      max_grade = 0;
      printed_name = -1;
      for (int j = 0; j < students.size(); j++) {
        if (grades.get(j) >= max_grade) {
          max_grade = grades.get(j);
          printed_name = j;
        }
      }
      System.out.println("(" + students.get(printed_name).l_name + ", " + students.get(printed_name).f_name + ", " + max_grade + ")");
      students.remove(printed_name);
      grades.remove(printed_name);
    }
  }

  private static void read_book (String filename, String key) { // retrieves gradebook from file given in commandline
    try {
      book = new Gradebook(filename, key);
      make_roster(book); // populates roster with Students
    } catch (Exception e) {
      File myObj = new File("decry.txt");  
  	  myObj.delete();
      //System.out.println("Exception: " + e);
      //e.printStackTrace();
      //error();
    }
  }

  private static void make_roster(Gradebook book) { // turns names into Student objects
    String[] name = null;
    roster = new TreeMap<Student, Integer>();
    for (int i = 0; i < book.numStuds(); i++) {
      name = book.gradebook.get("Student").get(i).split(" ");
      roster.put(new Student(name[0], name[1]), i);
    }
  }

  private static void calculate_final_grades() { // populates final_grades TreeMap while sorting names
    double final_grade;
    int points;
    double weight;
    int score;
    int assignment_num;
    final_grades = new TreeMap<Student, Double>();
    for (Student std : roster.navigableKeySet()) { // each student
      final_grade = 0.0;
      for (String str : book.gradebook.keySet()) { // each assignment for each student
        if (str.compareTo("Student") != 0) { // skips student column 
          assignment_num = book.assignments.get("Assignment").indexOf(str);
          points = Integer.parseInt(book.assignments.get("Points").get(assignment_num));
          weight = Double.parseDouble(book.assignments.get("Weight").get(assignment_num));
          if (book.gradebook.get(str).get(roster.get(std)).compareTo("NG") == 0) {
            score = 0;
          } else {
            score = Integer.parseInt(book.gradebook.get(str).get(roster.get(std)));
          }
          final_grade += ((double) score / points) * weight;
        }
      }
      final_grades.put(std, final_grade);
    }
  }

  private static void error() {
    System.out.println("Invalid");
    print_Gradebook(book);
		System.exit(255);
  }

  public static void main(String[] args) {

    //TODO Code this
    if(args.length >=6 ) { // impossible to have valid commandline with less than 6 args
      System.out.println("\nNumber Of Arguments Passed: " + args.length);
      System.out.println("----Following Are The Command Line Arguments Passed----");
      for(int counter = 0; counter < args.length; counter++) {
        System.out.println("args[" + counter + "]: " + args[counter]);
      }
      // Decide what is the setting we are in
      if (args[0].compareTo("-N") != 0) { // ensures proper order
        //System.out.println("Here1");
        error();
      }
      //System.out.println("Here1.25");
      File f = new File(args[1] + ".txt"); 
      //System.out.println("Here1.75");
      if (!f.exists()) { // ensures file exists
        //System.out.println("Here2");
        error();
      }
      //System.out.println("Here2.25");
      read_book(args[1] + ".txt", args[3]); // reads gradebook from file
      //System.out.println("Here2.75");
      if (args[2].compareTo("-K") != 0) { // ensures proper order
        //System.out.println("Here3");
        error();
      }
      //System.out.println("Here3.5");
//      if (args[3].compareTo(book.key()) != 0) { // ensures key is correct
//        //System.out.println("Here4");
//        error();
//      }
      //System.out.println("Here4.5");
      if (args[4].compareTo("-PA") == 0 && args.length >= 8) { // checks for valid print_assignment action
        //System.out.println("Here5");
        print_Assignment(args);
      } else if (args[4].compareTo("-PS") == 0 && args.length >= 9) { // checks for valid print_student action
        //System.out.println("Here6");
        print_Student(args);
      } else if (args[4].compareTo("-PF") == 0 && args.length >= 6) { // checks for valid print_final action
        //System.out.println("Here7");
        print_Final(args);
      } else { // no valid action found
        //System.out.println("Here8");
        error();
      }
    } else {
      System.out.println("\nWrong number of arguments passed.");
      error();
    }

  }
}