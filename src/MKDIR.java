import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.*;
import java.lang.*;

/**
 * @author Maria Jianu
 * 
 *         Clasa MKDIR contine metoda mkdir() care va face un director in
 *         directorul determinat de path
 */
public class MKDIR {

	/**
	 * Face un director nou si il adauga in lista de dircetoare a direcorului
	 * parinte determinat de path
	 * 
	 * @param path    Path-ul la care trbeuie facut noul director
	 * @param outFile Fisierul de output
	 * @param errFile Fisierul de erori
	 */
	public void mkdir(String path, String outFile, String errFile) {
		PrintWriter writerOut = null;
		int error = 0;
		PrintWriter writerErr = null;
		CommandFactory cf = CommandFactory.getInstance();
		FileSystem fs = Main.getFileSys();
		int absolutePath = 0;
		try {
			writerOut = new PrintWriter(new FileWriter(outFile, true));
			writerErr = new PrintWriter(new FileWriter(errFile, true));
			String[] directory = path.split("/"); // un vector de nume de directoare
			for (int i = 0; i < directory.length; i++) {
				if (directory[i].equals(".") || directory[i].equals(".."))
					absolutePath = 1;
			}
			if (absolutePath == 0) {
				//path nu contine . sau ..
				if (directory.length == 1) {
					/* path e doar numele directorului
					 * adaug un director in lista de directoare a directorului curent
					 */
					Directory dir = new Directory();
					// referinta la directorul curent
					dir = dir.findRightPath(fs.getRoot(), fs.getCurrentDir(), dir);
					//verific daca exista duplicate
					int duplicate = dir.findDuplicates(dir, directory[0]);
					if (duplicate == 0) {
						//nu exista duplicate deci fac directorul
						Directory newDir = new Directory(directory[0]);
						newDir.setParentDir(dir);
						dir.getDirectories().add(newDir);
						Collections.sort(dir.getDirectories(), new CompareDirNames());
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
					} else {
						//exista un folder sau fisier cu acelasi nume
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerErr.println("mkdir: cannot create directory /" + directory[0] + ": Node exists");
					}
				} else if (directory.length == 2 && path.charAt(0) == '/') {
					//path-ul este /dir1
					Directory dir = new Directory(directory[1]);
					//verific daca exista duplicate
					int duplicate = dir.findDuplicates(fs.getRoot(), directory[1]);
					if (duplicate == 0) {
						//nu sunt, deci fac directorul in root
						dir.setParentDir(fs.getRoot());
						fs.getRoot().getDirectories().add(dir);
						Collections.sort(fs.getRoot().getDirectories(), new CompareDirNames());
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
					} else {
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerErr.println("mkdir: cannot create directory /" + directory[1] + ": Node exists");
					}
				} else if (directory.length >= 2) {
					// pe poz directory - 1 e numele noului director
					Directory dir = fs.getRoot().findRightPath(fs.getRoot(), directory[directory.length - 2],
							fs.getRoot());
					// verfic daca folderul exista deja, sau daca e un fisier cu acelasi nume
					int valid = 0;
					valid = dir.findDirectory(fs.getRoot(), directory[directory.length - 2], 0);
					for (Directory d : dir.getDirectories()) {
						if (d.getDirName().equals(directory[directory.length - 1]))
							error = 1;
					}
					for (Files f : dir.getFiles()) {
						if (f.getFileName().equals(directory[directory.length - 1]))
							error = 1;
					}
					if (valid == 0) {
						// nu era un path valid
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerErr.print("mkdir: ");
						for (int i = 1; i <= directory.length - 2; i++) {
							writerErr.print("/" + directory[i]);
						}
						writerErr.println(": No such directory");
					} else if (error == 1) {
						//exista duplicate
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerErr.print("mkdir: cannot create directory ");
						for (int i = 0; i < directory.length; i++) {
							if (!directory[i].equals("") && i == 0)
								writerErr.print("/");
							writerErr.print(directory[i]);
							if (i != directory.length - 1)
								writerErr.print("/");
						}
						writerErr.println(": Node exists");
					} else {
						//directorul parinte
						Directory newDir = new Directory(directory[directory.length - 1]);
						newDir.setParentDir(dir);
						//adaug noul director in directorul parinte
						dir.getDirectories().add(newDir);
						Collections.sort(dir.getDirectories(), new CompareDirNames());
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
					}
				} else {
					// path-ul e /dir, deci facem un director imediat dupa root
					Directory dir = new Directory(directory[1]);
					dir.setParentDir(fs.getRoot());
					fs.getRoot().getDirectories().add(dir);
					Collections.sort(fs.getRoot().getDirectories(), new CompareDirNames());
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
				}
			} else {
				// path-ul contine . sau ..
				String destDir = fs.getCurrentDir();
				Directory auxDir = new Directory();
				Directory parentDir = new Directory();
				int invalidPath = 0;
				// pe pozitia directory.length - 1 se afla numele noului fisier
				for (int i = 0; i < directory.length - 1; i++) {
					if (directory[i].equals("."))
						destDir = destDir;
					else if (directory[i].equals("..")) {
						if (destDir.equals("/")) {
							invalidPath = 1;
							destDir = "invalid";
							break;
						}
						auxDir = fs.getRoot().findRightPath(fs.getRoot(), destDir, auxDir);
						if (auxDir.getParentDir() == null) {
							invalidPath = 1;
							destDir = "invalid";
							break;
						} else {
							auxDir = auxDir.getParentDir();
							destDir = auxDir.getDirName();
						}
					} else {
						destDir = directory[i];
						// verific daca directory[i] e valid
						int valid = 0;
						valid = auxDir.findDirectory(fs.getRoot(), destDir, valid);
						if (valid == 0) {
							invalidPath = 1;
							destDir = "invalid";
							break;
						}
					}
				}
				int valid = 0;
				if (invalidPath == 0) {
					if (destDir.equals("/") && fs.getCurrentDir().equals("/"))
						valid = 1; // path-ul e valid
					valid = auxDir.findDirectory(fs.getRoot(), destDir, valid);
				}
				int duplicate = 0;
				if (destDir.equals("/") && valid == 1 && invalidPath == 0) {
					for (Directory d : fs.getRoot().getDirectories())
						if (d.getDirName().equals(directory[directory.length - 1]))
							duplicate = 1;
					if (duplicate == 0) {
						for (Files f : fs.getRoot().getFiles())
							if (f.getFileName().equals(directory[directory.length - 1]))
								duplicate = 1;
					}

				} else if (invalidPath == 0) {
					auxDir = auxDir.findRightPath(fs.getRoot(), destDir, auxDir);
					for (Directory d : auxDir.getDirectories())
						if (d.getDirName().equals(directory[directory.length - 1]))
							duplicate = 1;
					if (duplicate == 0) {
						for (Files f : auxDir.getFiles())
							if (f.getFileName().equals(directory[directory.length - 1]))
								duplicate = 1;
					}
				}
				if (valid == 1 && invalidPath == 0 && duplicate == 0) {
					if (destDir.equals("/")) {
						// adaugam director in root
						Directory newDir = new Directory(directory[directory.length - 1]);
						newDir.setParentDir(fs.getRoot());
						fs.getRoot().getDirectories().add(newDir);
						Collections.sort(fs.getRoot().getDirectories(), new CompareDirNames());
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
					} else {
						//adaug director intr-un folder care nu e root
						Directory newDir = new Directory(directory[directory.length - 1]);
						Directory dir = new Directory();
						dir = dir.findRightPath(fs.getRoot(), destDir, dir);
						newDir.setParentDir(dir);
						dir.getDirectories().add(newDir);
						Collections.sort(dir.getDirectories(), new CompareDirNames());
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
					}
				} else {
					if (duplicate == 1) {
						// eroarea 2
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerErr.print("mkdir: cannot create directory ");
						for (int i = 0; i < directory.length; i++) {
							if (!directory[i].equals("") && i == 0)
								writerErr.print("/");
							writerErr.print(directory[i]);
							if (i != directory.length - 1)
								writerErr.print("/");
						}
						writerErr.println(": Node exists");
					} else {
						// eroarea 1
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerErr.print("mkdir: ");
						for (int i = 0; i < directory.length - 1; i++) {
							if (path.charAt(0) != '/' && i == 0)
								writerErr.print(directory[i]);
							else
								writerErr.print("/" + directory[i]);
						}
						writerErr.println(": No such directory");
					}

				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		writerOut.close();
		writerErr.close();
	}
}
