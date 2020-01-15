import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Maria Jianu
 *
 */
public class Main {
	private static String errFile = null;
	private static String outFile = null;
	private static FileSystem fileSys;

	public static void main(String args[]) {
		setErrFile(args[2]);
		setOutFile(args[1]);
		// instanta sistemului de fisiere
		FileSystem fs = new FileSystem();
		setFileSys(fs);
		// setez root-ul
		Directory dir = new Directory("/");
		fs.setRoot(dir);
		// golesc fisierele de output si erori
		emptyFile(args[1]);
		emptyFile(args[2]);
		CommandFactory commands = null;
		commands = CommandFactory.getInstance(); // instanta unica
		commands.createCommand(args[0]);
	}

	public static String getErrFile() {
		return errFile;
	}

	public static void setErrFile(String errFile2) {
		errFile = errFile2;
	}

	public static String getOutFile() {
		return outFile;
	}

	public static void setOutFile(String outFile2) {
		outFile = outFile2;
	}

	public static FileSystem getFileSys() {
		return fileSys;
	}

	public static void setFileSys(FileSystem fileSys) {
		Main.fileSys = fileSys;
	}

	public static void emptyFile(String file) {
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(file));
			writer.print("");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
