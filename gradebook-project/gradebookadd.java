import java.util.Arrays;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
/**
 * Allows the user to add a new student or assignment to a gradebook,
 * or add a grade for an existing student and existing assignment
 */
public class gradebookadd {
  
  private static boolean file_test(String filename) {
    File f = new File(filename);
    return f.exists();
  }

  private static void error(String msg) {
    System.out.println(msg);
    System.exit(255);
  }

  public static boolean nameValid(String s, boolean isAssignmentName){

    
    for (int i = 0; i < s.length(); i++) {

      if (isAssignmentName) {
        if (!Character.isLetterOrDigit(s.charAt(i))) {
          return false;
        }
      } else {
        if (!Character.isLetter(s.charAt(i))) {
          return false;
        }
      }
    }
    
    return true;
  }



  /* parses the cmdline to keep main method simplified */
  private static boolean parse_cmdline(String[] args) throws Exception {
	Gradebook book = null;
	  
    if (args.length == 0) {
      System.out.println("\nNo arguments found");
    } else if(args.length==1)
      System.out.println("\nOnly One Command Line Argument Passed");

    if(args.length>=7) {
      System.out.println("\nNumber Of Arguments Passed: " + args.length);
      System.out.println("----Following Are The Command Line Arguments Passed----");

      String name = "";
      String aName = "";
      String points = "";
      String weight = "";
      String fName = "";
      String lName = "";
      String fullName = "";
      String grade = "";


      for(int counter=0; counter < args.length; counter++) {
        System.out.println("args[" + counter + "]: " + args[counter]);
      }


 


      if (args[0].equals("-N") && file_test(args[1] + ".txt") && args[2].equals("-K")) { //filename and key

          
        name = args[1] + ".txt";
        try {
        	book = new Gradebook(name, args[3]); 
        } catch (Exception e) {
        	File myObj = new File("decry.txt");  
        	myObj.delete();
        }
//        key = book.key();
//        if (!args[3].equals(key)) {
//          error("Wrong Key.");
//        }

      } else {
        error("Options entered in wrong order, or file doesn't exist.");
      }

      



      if (args[4].equals("-AA")) { //add assignment

        if(Arrays.stream(args).anyMatch("-DA"::equals) || Arrays.stream(args).anyMatch("-AS"::equals)
          || Arrays.stream(args).anyMatch("-DS"::equals) || Arrays.stream(args).anyMatch("-AG"::equals)) {
          error("Wrong order.");
        }

        if(Arrays.stream(args).anyMatch("-FN"::equals) || Arrays.stream(args).anyMatch("-LN"::equals)
          || Arrays.stream(args).anyMatch("-G"::equals)) {
          error("Wrong order.");
        }

        if(!Arrays.stream(args).anyMatch("-AN"::equals) || !Arrays.stream(args).anyMatch("-P"::equals)
          || !Arrays.stream(args).anyMatch("-W"::equals)) {
          error("Wrong order.");
        }

        for(int i = 5; i < args.length; i++) {
          if (args[i].equals("-AN")) { //Assignment name

            if (i+1 < args.length) {
              if (!nameValid(args[i+1],true)){
                error("Invalid name.");
              }
              aName = args[i+1];

            } else {
              error("Wrong.");
            }
            
          } else if (args[i].equals("-P")){ //assignment points


            if (i+1 < args.length) {
              int in = Integer.parseInt(args[i+1]);
              if (in < 0) {
                error("Wrong.");
              }
              points = args[i+1];
            } else {
              error("Wrong");
            }
            
          } else if (args[i].equals("-W")) { //assignment weight


            if (i+1 < args.length) {
              double d = Double.parseDouble(args[i+1]);
              if (d > 1 || d < 0) {
                error("Invalid weight.");
              }
              weight = args[i+1];
            } else {
              error("Wrong.");
            }
          }
        }

        book.addAssignment(aName, points, weight);


      } else if (args[4].equals("-DA")) { //delete assignment

        if(Arrays.stream(args).anyMatch("-AA"::equals) || Arrays.stream(args).anyMatch("-AS"::equals)
          || Arrays.stream(args).anyMatch("-DS"::equals) || Arrays.stream(args).anyMatch("-AG"::equals)) {
        	error("Wrong.");
        }

        if(Arrays.stream(args).anyMatch("-FN"::equals) || Arrays.stream(args).anyMatch("-LN"::equals)
          || Arrays.stream(args).anyMatch("-P"::equals) || Arrays.stream(args).anyMatch("-W"::equals) 
          || Arrays.stream(args).anyMatch("-G"::equals)) {
        	error("Wrong.");
        }

        if(!Arrays.stream(args).anyMatch("-AN"::equals)) {
        	error("Wrong.");
        }

        for(int i = 5; i < args.length; i++) {
          if (args[i].equals("-AN")) { //Assignment name
            if (i+1 < args.length) {
              if (!nameValid(args[i+1],true)){
                error("Wrong.");
              }
              aName = args[i+1];
            } else {
              error("Wrong.");
            }
          }
        }

        book.deleteAssignment(aName);


      } else if (args[4].equals("-AS")) { //add student

        if(Arrays.stream(args).anyMatch("-DA"::equals) || Arrays.stream(args).anyMatch("-AA"::equals)
          || Arrays.stream(args).anyMatch("-DS"::equals) || Arrays.stream(args).anyMatch("-AG"::equals)) {
          error("rrrr Wrong.");
        }

        if(Arrays.stream(args).anyMatch("-AN"::equals) || Arrays.stream(args).anyMatch("-P"::equals)
          || Arrays.stream(args).anyMatch("-W"::equals) || Arrays.stream(args).anyMatch("-G"::equals)) {
          error("Uh Wrong.");
        }

        if(!Arrays.stream(args).anyMatch("-FN"::equals) || !Arrays.stream(args).anyMatch("-LN"::equals)) {
          error("Bitch Wrong.");
        }


        for(int i = 5; i < args.length; i++) {
          if (args[i].equals("-FN")) { //student first name

            if (i+1 < args.length) {
              if (!nameValid(args[i+1],false)){
                error("Oh Wrong.");
              }
              fName = args[i+1];
            } else {
              error("Oh hey Wrong.");
            }
            
          } else if (args[i].equals("-LN")){ //student last name

            if (i+1 < args.length) {
              if (!nameValid(args[i+1],false)){
                error("Bruh Wrong.");
              }
              lName = args[i+1];
            } else {
              error("Shit Wrong.");
            }
            
          } 
        }
        
        fullName = fName + " " + lName;
        book.addStudent(fullName); 


      } else if (args[4].equals("-DS")) { //delete student

        if(Arrays.stream(args).anyMatch("-DA"::equals) || Arrays.stream(args).anyMatch("-AS"::equals)
          || Arrays.stream(args).anyMatch("-AA"::equals) || Arrays.stream(args).anyMatch("-AG"::equals)) {
          error("Wrong.");
        }

        if(Arrays.stream(args).anyMatch("-AN"::equals) || Arrays.stream(args).anyMatch("-P"::equals)
          || Arrays.stream(args).anyMatch("-W"::equals) || Arrays.stream(args).anyMatch("-G"::equals)) {
          error("Wrong.");
        }

        if(!Arrays.stream(args).anyMatch("-FN"::equals) || !Arrays.stream(args).anyMatch("-LN"::equals)) {
          error("Wrong.");
        }

        for(int i = 5; i < args.length; i++) {
          if (args[i].equals("-FN")) { //student first name

            if (i+1 < args.length) {
              if (!nameValid(args[i+1],false)){
                error("Wrong.");
              }
              fName = args[i+1];
            } else {
              error("Wrong.");
            }
            
          } else if (args[i].equals("-LN")){ //student last name

            if (i+1 < args.length) {
              if (!nameValid(args[i+1],false)){
                error("Wrong.");
              }
              lName = args[i+1];
            } else {
              error("Wrong.");
            }
            
          } 
        }
        
        fullName = fName + " " + lName;
        book.deleteStudent(fullName); //**********EDIT!

      } else if (args[4].equals("-AG")) { //add grade

        if(Arrays.stream(args).anyMatch("-DA"::equals) || Arrays.stream(args).anyMatch("-AS"::equals)
          || Arrays.stream(args).anyMatch("-DS"::equals) || Arrays.stream(args).anyMatch("-AA"::equals)) {
          error("Wrong.");
        }

        if(Arrays.stream(args).anyMatch("-P"::equals) || Arrays.stream(args).anyMatch("-W"::equals)) {
          error("Wrong.");
        }

        if(!Arrays.stream(args).anyMatch("-FN"::equals) || !Arrays.stream(args).anyMatch("-LN"::equals)
          || !Arrays.stream(args).anyMatch("-AN"::equals) || !Arrays.stream(args).anyMatch("-G"::equals)) {
          error("Wrong.");
        }

        for(int i = 5; i < args.length; i++) {
          if (args[i].equals("-FN")) { //student first name

            if (i+1 < args.length) {
              if (!nameValid(args[i+1],false)){
                error("Wrong.");
              }
              fName = args[i+1];
            } else {
              error("Wrong.");
            }
            
          } else if (args[i].equals("-LN")){ //student last name

            if (i+1 < args.length) {
              if (!nameValid(args[i+1],false)){
                error("Wrong.");
              }
              lName = args[i+1];
            } else {
              error("Wrong.");
            }
            
          } else if (args[i].equals("-AN")) { //Assignment name

            if (i+1 < args.length) {
              if (!nameValid(args[i+1],true)){
                error("Wrong.");
              }
              aName = args[i+1];
            } else {
              error("Wrong.");
            }
          } else if (args[i].equals("-G")) { //grade received

            if (i+1 < args.length) {
              int g = Integer.parseInt(args[i+1]);
              if (g < 0) {
                error("Wrong.");
              }
              grade = args[i+1];
            } else {
              error("Wrong.");
            }
          }

        }
        
        fullName = fName + " " + lName;
        book.addGrade(fullName, aName, grade); //**********EDIT!


      } else {

        error("Wrong.");
      }

    } else {
      System.out.println("Need more arguments");
      error("Wrong.");
    }

    //updates the file upon success in running command line
    File file = new File(args[1] + ".txt");
	FileWriter fileWriter = new FileWriter(file);
    PrintWriter printWriter = new PrintWriter(fileWriter);
    printWriter.print(book.toString());
    printWriter.close();
    return true;
  }

  public static void main(String[] args) throws Exception {

    boolean valid = parse_cmdline(args);

    if(valid) {
	    System.out.println("Successful!");
    }

    return;
  }
}
