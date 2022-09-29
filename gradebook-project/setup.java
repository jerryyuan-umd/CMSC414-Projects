import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Initialize gradebook with specified name and generate a key.
 */
public class setup {

	/* test whether the file exists */
	private boolean file_test(String filename) {
		File f = new File(filename + ".txt");
		return f.exists();
	}

	public static void main(String[] args) throws IOException {
		setup obj = new setup();
		String key = "invalid";
		if (args.length < 2) {
			System.out.println("Usage: setup <logfile pathname>");
			System.exit(1);
		} else if (args.length == 2 && args[0].compareTo("-N") == 0) {
			if (obj.file_test(args[1])) {
				System.out.println("Invalid");
				System.exit(255);
			}
			Gradebook newBook = new Gradebook();
			key = newBook.key();
			File file = new File(args[1] + ".txt");
			file.createNewFile();
			FileWriter fileWriter = new FileWriter(file);
		    PrintWriter printWriter = new PrintWriter(fileWriter);
		    printWriter.print(newBook.toString());
		    printWriter.close();
		}
    
		System.out.println("Key is: " + key);

		return;
	}
}
