import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

/**
 * Clasa MV contine metoda mv() care va muta fisierul/directorul sursa in
 * directorul destinatie
 * 
 * @author Maria Jianu
 *
 */
public class MV {
	/**
	 * @param source  Path-ul fisierlui/directorului sursa
	 * @param dest    Path-ul directorului destinatie
	 * @param outFile Numele fisierului de output
	 * @param errFile Numele fisierului de erori
	 */
	public void mv(String source, String dest, String outFile, String errFile) {
		PrintWriter writerOut = null;
		PrintWriter writerErr = null;
		CommandFactory cf = CommandFactory.getInstance();
		FileSystem fs = Main.getFileSys();
		int absolutePathSource = 0, isFile = 0, isDirectory = 0;
		int absolutePathDest = 0, invalidPath = 0;
		Directory sourceDir = new Directory();
		Directory destDirectory = new Directory();
		Directory fileDir = new Directory();
		Files sourceFile = new Files();
		try {
			writerOut = new PrintWriter(new FileWriter(outFile, true));
			writerErr = new PrintWriter(new FileWriter(errFile, true));
			String[] directory = source.split("/"); // un vector de nume de directoare
			for (int i = 0; i < directory.length; i++) {
				if (directory[i].equals(".") || directory[i].equals(".."))
					absolutePathSource = 1;
			}
			String[] directoryDest = dest.split("/"); // un vector de nume de directoare
			for (int i = 0; i < directoryDest.length; i++) {
				if (directoryDest[i].equals(".") || directoryDest[i].equals(".."))
					absolutePathDest = 1;
			}
			if (absolutePathSource == 0) {
				// path ul sursei nu contine . sau ..
				if (directory.length > 2) {
					// pe poz dir - 1 e numele dir/fis si pe poz dir - 2 e numele dir parinte
					Directory auxDir = new Directory();
					int valid = 0;
					for (int i = 1; i < directory.length - 1; i++) {
						valid = auxDir.findDirectory(fs.getRoot(), directory[i], 0);
						if (valid == 0)
							break;
					}
					if (valid == 1) {
						// verific daca pe ultima pozitie era un fisier sau un director
						auxDir = auxDir.findRightPath(fs.getRoot(), directory[directory.length - 2], auxDir);
						// verific daca era director sau fisier
						for (Directory d : auxDir.getDirectories()) {
							if (d.getDirName().equals(directory[directory.length - 1])) {
								sourceDir = d;
								isDirectory = 1;
								break;
							}
						}
						for (Files f : auxDir.getFiles()) {
							if (f.getFileName().equals(directory[directory.length - 1])) {
								sourceFile = f;
								fileDir = auxDir;
								isFile = 1;
								break;
							}
						}
						if (isFile == 1)
							auxDir.getFiles().remove(sourceFile);
						if (isFile == 0 && isDirectory == 0) {
							// fisierul/folderul nu exista
							invalidPath = 1;
						}
					} else {
						invalidPath = 1;
					}
				} else if (directory.length == 1) {
					// path-ul e doar numele directorului/fisierului
					Directory auxDir = new Directory();
					auxDir = auxDir.findRightPath(fs.getRoot(), fs.getCurrentDir(), auxDir);
					// verific daca era director sau fisier
					for (Directory d : auxDir.getDirectories()) {
						if (d.getDirName().equals(directory[directory.length - 1])) {
							sourceDir = d;
							isDirectory = 1;
							break;
						}
					}
					for (Files f : auxDir.getFiles()) {
						if (f.getFileName().equals(directory[directory.length - 1])) {
							sourceFile = f;
							fileDir = auxDir;
							isFile = 1;
							break;
						}
					}
					if (isFile == 1)
						auxDir.getFiles().remove(sourceFile);
					if (isFile == 0 && isDirectory == 0) {
						// fisierul/folderul nu exista
						invalidPath = 1;
					}
				} else {
					// path-ul e /dir
					Directory auxDir = new Directory();
					auxDir = auxDir.findRightPath(fs.getRoot(), "/", auxDir);
					// verific daca era director sau fisier
					for (Directory d : auxDir.getDirectories()) {
						if (d.getDirName().equals(directory[directory.length - 1])) {
							sourceDir = d;
							isDirectory = 1;
							break;
						}
					}
					for (Files f : auxDir.getFiles()) {
						if (f.getFileName().equals(directory[directory.length - 1])) {
							sourceFile = f;
							fileDir = auxDir;
							isFile = 1;
							break;
						}
					}
					if (isFile == 1)
						auxDir.getFiles().remove(sourceFile);
					if (isFile == 0 && isDirectory == 0) {
						// fisierul/folderul nu exista
						invalidPath = 1;
					}

				}
				if (invalidPath == 1) {
					// nu exista dir/fisier sursa
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
					writerErr.println("mv: cannot move " + source + ": No such file or directory");
					writerErr.close();
					writerOut.close();
					return;
				}
			} else {
				// path-ul sursei contine . sau ..
				String destDir = fs.getCurrentDir();
				Directory auxDir = new Directory();
				String parentDir = "";
				for (int i = 0; i < directory.length - 1; i++) {
					// caut directorul parinte al sursei in functie de . sau ..
					if (directory[i].equals("."))
						parentDir = destDir;
					else if (directory[i].equals("..")) {
						if (parentDir.equals("/")) {
							invalidPath = 1;
							parentDir = "invalid";
							break;
						}
						auxDir = fs.getRoot().findRightPath(fs.getRoot(), parentDir, auxDir);
						if (auxDir.getParentDir() == null) {
							invalidPath = 1;
							parentDir = "invalid";
							break;
						} else {
							auxDir = auxDir.getParentDir();
							parentDir = auxDir.getDirName();
						}
					} else
						parentDir = directory[i];
				}
				for (int i = 0; i < directory.length; i++) {
					//caut destDir in functie de . sau ..
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
					} else
						destDir = directory[i];
				}
				// destDir e numele fisierului/dir care trebuie mutat
				if (invalidPath == 0) {
					Files file = new Files(destDir);
					Directory dir = new Directory(destDir);
					//verific daca e fisier
					isFile = dir.findFile(fs.getRoot(), destDir, 0);
					//verific daca e director
					isDirectory = dir.findDirectory(fs.getRoot(), destDir, 0);
					if (isFile == 0 && isDirectory == 0) {
						// nu exista dir/fisier sursa
						invalidPath = 1;
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerErr.println("mv: cannot move " + source + ": No such file or directory");
						writerErr.close();
						writerOut.close();
						return;
					} else if (isFile == 1) {
						//era fisier
						sourceFile = file;
						Directory parentDirr = new Directory();
						parentDirr = parentDirr.findRightPath(fs.getRoot(), parentDir, parentDirr);
						parentDirr.getFiles().remove(sourceFile);

					} else {
						// era director
						sourceDir = sourceDir.findRightPath(fs.getRoot(), destDir, sourceDir);
					}
				} else {
					// nu exista sursa
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
					writerErr.println("mv: cannot move " + source + ": No such file or directory");
					writerErr.close();
					writerOut.close();
					return;
				}
			}
			if (absolutePathDest == 0 && invalidPath == 0) {
				//path-ul destinatiei nu contine . sau ..
				if (directoryDest.length > 2) {
					// pe poz dir - 1 e numele dir sursa si pe poz dir - 2 e numele dir parinte
					Directory auxDir = new Directory();
					int valid = 0;
					for (int i = 1; i < directoryDest.length - 1; i++) {
						valid = auxDir.findDirectory(fs.getRoot(), directoryDest[i], 0);
						if (valid == 0)
							break;
					}
					if (valid == 1) {
						destDirectory = fs.getRoot().findPath(fs.getRoot(), directoryDest);
					} else
						invalidPath = 1;
				} else if (directoryDest.length == 1) {
					// path-ul e doar numele directorului
					Directory auxDir = new Directory();
					int found = 0;
					auxDir = fs.getRoot().findRightPath(fs.getRoot(), fs.getCurrentDir(), auxDir);
					for (Directory d : auxDir.getDirectories()) {
						if (d.getDirName().equals(directoryDest[0])) {
							destDirectory = d;
							found = 1;
							break;
						}
					}
					if (found == 0)
						invalidPath = 1;

				} else {
					if (dest.equals("/") || dest.equals(".")) {
						destDirectory = fs.getRoot();
					} else {
						// path-ul e /dir
						int found = 0;
						for (Directory d : fs.getRoot().getDirectories()) {
							if (d.getDirName().equals(directoryDest[1])) {
								destDirectory = d;
								found = 1;
								break;
							}
						}
						if (found == 0)
							invalidPath = 1;
					}
				}
			} else if (invalidPath == 0) {
				// path-ul destinatiei contine . sau ..
				String destDir = fs.getCurrentDir();
				Directory auxDir = new Directory();
				Directory parentDir = new Directory();
				// pe pozitia directoryDest.length - 1 se afla numele directorului dest
				int i;
				if (dest.charAt(0) == '/') {
					i = 1;
				} else
					i = 0;
				for (; i < directoryDest.length; i++) {
					if (directoryDest[i].equals("."))
						destDir = destDir;
					else if (directoryDest[i].equals("..")) {
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
						destDir = directoryDest[i];
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
						valid = 1; // path is ok
					valid = auxDir.findDirectory(fs.getRoot(), destDir, valid);
				}
				if (valid == 1 && invalidPath == 0) {
					if (destDir.equals("/"))
						destDirectory = fs.getRoot();
					else {
						destDirectory = destDirectory.findRightPath(fs.getRoot(), destDir, fs.getRoot());
					}
				}
			}
			if (invalidPath == 0) {
				// verificam duplicatele
				String name = "";
				if (isFile == 1)
					name = sourceFile.getFileName();
				else
					name = sourceDir.getDirName();
				int duplicate = destDirectory.findDuplicates(destDirectory, name);
				if (duplicate == 1) {
					//exista duplicate deci nu se poate face mutarea
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
					writerErr.println("mv: cannot move " + source + ": Node exists at destination");
					writerErr.close();
					writerOut.close();
					return;
				}
				if (isFile == 1 && duplicate == 0) {
					// mut fisierul in directorul destinatie
					destDirectory.getFiles().add(sourceFile);
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
					writerOut.close();
					writerErr.close();
					return;

				}
				if (isDirectory == 1 && duplicate == 0) {
					// mut directorul sursa in directorul destinatie
					Directory newDir = new Directory(sourceDir.getDirName());
					newDir.setDirectories(sourceDir.getDirectories());
					newDir.setFiles(sourceDir.getFiles());
					newDir.setParentDir(destDirectory);
					destDirectory.getDirectories().add(newDir);
					Directory auxDir = new Directory();
					auxDir = sourceDir.getParentDir();
					auxDir.getDirectories().remove(sourceDir);
					Collections.sort(destDirectory.getDirectories(), new CompareDirNames());
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
					writerOut.close();
					writerErr.close();
					return;
				}

			} else {
				//nu exista folderul destinatie
				writerOut.println(cf.getCommandNr());
				writerErr.println(cf.getCommandNr());
				writerErr.println("mv: cannot move into " + dest + ": No such directory");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		writerOut.close();
		writerErr.close();
	}
}
