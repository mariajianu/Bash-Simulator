import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
/**
 * @author Maria Jianu
 * 
 * Clasa CD contine metoda cd() care va schimba
 * directorul curent 
 */
public class CD {
	
	/**
	 * @param fs Instanta sistemului de fisiere
	 * @param path Path-ul directorului care trebuie setat ca directoru curent
	 * @param outFile Numele fisierului de output
	 * @param errFile Numele fisierului de erori
	 */
	public void cd(FileSystem fs, String path, String outFile, String errFile) {
		PrintWriter writerOut = null;
		PrintWriter writerErr = null;
		CommandFactory cf = CommandFactory.getInstance();
		int absolutePath = 0;
		try {
			writerOut = new PrintWriter(new FileWriter(outFile, true));
			writerErr = new PrintWriter(new FileWriter(errFile, true));
			String[] directory = path.split("/");
			for (int i = 0; i < directory.length; i++) {
				if (directory[i].equals(".") || directory[i].equals(".."))
					absolutePath = 1;
			}
			if (absolutePath == 0) {
				if (path.equals("/")) {
					fs.setCurrentDir("/");
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
				}
				else if(directory.length == 2 && path.charAt(0) == '/') {
					//path-ul e /dir
					Directory auxDir = new Directory();
					String currentDir = "";
					int ok = 0;
					auxDir = fs.getRoot();
					for(Directory d : auxDir.getDirectories()) {
						if(d.getDirName().equals(directory[1])) {
							currentDir = d.getDirName();
							ok = 1;
						}
					}
					if(ok == 0) {
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerErr.println("cd: " + path + ": No such directory");
					}
					else {
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						fs.setCurrentDir(currentDir);
					}
				}
				else if (directory.length > 1) {
					// un vector de nume de directoare
					int nr = fs.getRoot().getDirectories().size(), i = 1, ok = 0, j = 0;
					// j < i cu 1, j e pt directoare si i pt vectorul de nume
					while (nr != 0) {
						if (fs.getRoot().getDirectories().get(j).getDirName().equals(directory[i])) {
							ok = 1;
							break;
						}
						j++;
						nr--;
					}
					int valid = 0;
					if (ok == 1 && directory.length > 2) {
						Directory dir = new Directory();
						valid = dir.findDirectory(fs.getRoot(), directory[2], valid);
					}
					if (ok == 1 && valid == 1) {
						fs.setCurrentDir(directory[directory.length - 1]);
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
					} else {
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerErr.println("cd: " + path + ": No such directory");
					}
				} else {
					// path-ul e doar numele directorului
					Directory dir = new Directory();
					int ok = 0;
					dir = fs.getRoot().findRightPath(fs.getRoot(), fs.getCurrentDir(), dir);
					for (Directory d : dir.getDirectories()) {
						if (d.getDirName().equals(directory[0])) {
							ok = 1;
							break;
						}
					}
					if (ok == 1) {
						fs.setCurrentDir(directory[0]);
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
					} else {
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerErr.println("cd: " + path + ": No such directory");
					}
				}
			} else {
				// path contine . sau ..
				String destDir = "";
				Directory auxDir = new Directory();
				int invalidPath = 0;
				// pe pozitia directory.length - 1 se afla numele noului director
				for (int i = 0; i < directory.length - 1; i++) {
					if (directory[i].equals("."))
						destDir = fs.getCurrentDir();
					else if (directory[i].equals("..")) {
						if (destDir.equals("/") || fs.getCurrentDir().equals("/")) {
							invalidPath = 1;
							destDir = "invalid";
							break;
						}
						auxDir = fs.getRoot().findRightPath(fs.getRoot(), fs.getCurrentDir(), auxDir);
						if (auxDir.getParentDir() == null) {
							invalidPath = 1;
							destDir = "invalid";
							break;
						} else {
							auxDir = auxDir.getParentDir();
							destDir = auxDir.getDirName();
						}
					} else
						destDir = directory[i];
				}
				// daca e valid -> current dir e destDir
				int valid = 0;
				if (invalidPath == 0) {
					if (destDir.equals("/") && fs.getCurrentDir().equals("/"))
						valid = 1; // path is ok
					valid = auxDir.findDirectory(fs.getRoot(), destDir, valid);
				}
				if (invalidPath == 0 && valid == 1) {
					fs.setCurrentDir(destDir);
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
				}
				// daca nu e valid -> eroare
				else {
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
					writerErr.println("cd: " + path + ": No such directory");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		writerOut.close();
		writerErr.close();
	}
}
