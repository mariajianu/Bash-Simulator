import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

/**
 * Clasa CP contine metoda cp() care va copia 
 * fisierul/directorul sursa in directorul destinatie 
 * 
 * @author Maria Jianu
 *
 */
public class CP {

	/**
	 * @param source Path-ul fisierlui/directorului sursa
	 * @param dest Path-ul directorului destinatie
	 * @param outFile Numele fisierului de output
	 * @param errFile Numele fisierului de erori
	 */
	public void cp(String source, String dest, String outFile, String errFile) {
		PrintWriter writerOut = null;
		PrintWriter writerErr = null;
		CommandFactory cf = CommandFactory.getInstance();
		FileSystem fs = Main.getFileSys();
		int absolutePathSource = 0, isFile = 0, isDirectory = 0;
		int absolutePathDest = 0, invalidPath = 0;
		Directory sourceDir = new Directory();
		Directory destDirectory = new Directory();
		Files sourceFile = new Files();
		try {
			writerOut = new PrintWriter(new FileWriter(outFile, true));
			writerErr = new PrintWriter(new FileWriter(errFile, true));
			String[] directory = source.split("/"); // un vector de nume de directoare
			//pentru sursa
			for (int i = 0; i < directory.length; i++) {
				if (directory[i].equals(".") || directory[i].equals(".."))
					absolutePathSource = 1;
			}
			String[] directoryDest = dest.split("/"); // un vector de nume de directoare
			//pentru destinatie
			for (int i = 0; i < directoryDest.length; i++) {
				if (directoryDest[i].equals(".") || directoryDest[i].equals(".."))
					absolutePathDest = 1;
			}
			if (absolutePathSource == 0) {
				//path-ul sursei nu continea . sau ..
				if (directory.length > 2) {
					// pe poz dir - 1 e numele dir/fis si pe poz dir - 2 e numele dir parinte
					Directory auxDir = new Directory();
					int valid = 0;
					for (int i = 1; i < directory.length - 1; i++) {
						//verific daca numele exista
						valid = auxDir.findDirectory(fs.getRoot(), directory[i], 0);
						if (valid == 0)
							break;
					}
					if (valid == 1) {
						// verifici daca pe ultima pozitie era un fisier sau un director
						auxDir = auxDir.findRightPath(fs.getRoot(), directory[directory.length - 2], auxDir);
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
								isFile = 1;
								break;
							}
						}
						if (isFile == 0 && isDirectory == 0) {
							// fisierul/folderul nu exista
							invalidPath = 1;
						}
					} else {
						invalidPath = 1;
					}
				}
				else if(directory.length == 2 && !directory[0].equals("")) {
					//directory[0] e un dir din root si directory[1] e sursa
					Directory auxDir = new Directory();
					auxDir = auxDir.findRightPath(fs.getRoot(), directory[0], auxDir);
					//vad daca e fisier sau director
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
							isFile = 1;
							break;
						}
					}
					if (isFile == 0 && isDirectory == 0) {
						// fisierul/folderul nu exista
						invalidPath = 1;
					}
				}
				else if (directory.length == 1) {
					// path-ul e doar numele directorului/fisierului
					Directory auxDir = new Directory();
					auxDir = auxDir.findRightPath(fs.getRoot(), fs.getCurrentDir(), auxDir);
					//vad daca e fisier sau director
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
							isFile = 1;
							break;
						}
					}
					if (isFile == 0 && isDirectory == 0) {
						// fisierul/folderul nu exista
						invalidPath = 1;
					}
				} else {
					// path-ul e /dir
					Directory auxDir = new Directory();
					auxDir = auxDir.findRightPath(fs.getRoot(), "/", auxDir);
					//vad daca e fisier sau director
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
							isFile = 1;
							break;
						}
					}
					if (isFile == 0 && isDirectory == 0) {
						// fisierul/folderul nu exista
						invalidPath = 1;
					}

				}
				if (invalidPath == 1) {
					// nu exista dir/fisier sursa
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
					writerErr.println("cp: cannot copy " + source + ": No such file or directory");
					writerErr.close();
					writerOut.close();
					return;
				}
			} else {
				// path-ul sursei contine . sau ..
				String destDir = fs.getCurrentDir();
				Directory auxDir = new Directory();
				for (int i = 0; i < directory.length; i++) {
					//caut destDir in functie de . si ..
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
				// destDir e numele fisierului/dir care trebuie copiat
				if (invalidPath == 0) {
					//vad daca e fisier sau director si daca exista
					Files file = new Files(destDir);
					Directory dir = new Directory(destDir);
					isFile = dir.findFile(fs.getRoot(), destDir, 0);
					isDirectory = dir.findDirectory(fs.getRoot(), destDir, 0);
					if (isFile == 0 && isDirectory == 0) {
						// nu exista dir/fisier sursa
						invalidPath = 1;
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerErr.println("cp: cannot copy " + source + ": No such file or directory");
						writerErr.close();
						writerOut.close();
						return;
					} else if (isFile == 1) {
						//era fisier
						sourceFile = file;
					} else {
						// era director
						sourceDir = sourceDir.findRightPath(fs.getRoot(), destDir, fs.getRoot());
					}
				} else {
					// nu exista sursa
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
					writerErr.println("cp: cannot copy " + source + ": No such file or directory");
					writerErr.close();
					writerOut.close();
					return;
				}
			}
			if (absolutePathDest == 0 && invalidPath == 0) {
				if (directoryDest.length > 2) {
					// pe poz dir - 1 e numele dir sursa si pe poz dir - 2 e numele dir parinte
					Directory auxDir = new Directory();
					int valid = 0;
					//vad daca e fisier sau director
					for (int i = 1; i < directoryDest.length - 1; i++) {
						valid = auxDir.findDirectory(fs.getRoot(), directoryDest[i], 0);
						if (valid == 0)
							break;
					}
					if (valid == 1) {
						destDirectory = fs.getRoot().findRightPath(fs.getRoot(),
								directoryDest[directoryDest.length - 1], fs.getRoot());
					} else
						invalidPath = 1;
				} else if (directoryDest.length == 1) {
					// path-ul e doar numele directorului
					Directory auxDir = new Directory();
					int found = 0;
					auxDir = fs.getRoot().findRightPath(fs.getRoot(), fs.getCurrentDir(), auxDir);
					//vad daca e fisier sau director
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
			} else if(invalidPath == 0){
				// path-ul destinatiei contine . sau ..
				String destDir = fs.getCurrentDir();
				Directory auxDir = new Directory();
				Directory parentDir = new Directory();
				// pe pozitia directoryDest.length - 1 se afla numele directorului dest
				for (int i = 0; i < directoryDest.length; i++) {
					if (directoryDest[i].equals("."))
						destDir = destDir;
					else if (directoryDest[i].equals("..")) {
						if(destDir.equals("/")) {
							invalidPath = 1;
							destDir = "invalid";
							break;
						}
						auxDir = fs.getRoot().findRightPath(fs.getRoot(), destDir, auxDir);
						if (auxDir.getParentDir() == null) {
							invalidPath = 1;
							destDir = "invalid";
							break;
						}
						else {
							auxDir = auxDir.getParentDir();
							destDir = auxDir.getDirName();
							
						}
					} else {
						String prevDir = destDir;
						destDir = directoryDest[i];
						//verific daca directory[i] e valid
						int valid = 0;
						valid = auxDir.findDirectory(fs.getRoot(), destDir, valid);
						if(valid == 0) {
							invalidPath = 1;
							destDir = "invalid";
							break;
						}
						if(valid == 1) {
							int ok = 0;
							auxDir = auxDir.findRightPath(fs.getRoot(), prevDir, fs.getRoot());
							for(Directory d : auxDir.getDirectories()) {
								if(d.getDirName().equals(destDir))
									ok = 1;
							}
							if(ok == 0)
								invalidPath = 1;
						}
					}
				}
				int valid = 0;
				if(invalidPath == 0) {
					if(destDir.equals("/") && fs.getCurrentDir().equals("/"))
						valid = 1; //path e valid
					 valid = auxDir.findDirectory(fs.getRoot(), destDir, valid);
				}
				if(valid == 1 && invalidPath == 0) {
					if(destDir.equals("/"))
						destDirectory = fs.getRoot();
					else {
						destDirectory = destDirectory.findRightPath(fs.getRoot(), destDir, destDirectory);
					}
				}
			}
			if (invalidPath == 0) {
				//verific duplicatele
				String name = "";
				if(isFile == 1)
					name = sourceFile.getFileName();
				else
					name = sourceDir.getDirName();
				int duplicate = destDirectory.findDuplicates(destDirectory, name);
				if(duplicate == 1) {
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
					writerErr.println("cp: cannot copy " + source + ": Node exists at destination");
					writerErr.close();
					writerOut.close();
					return;
				}
				if(isFile == 1 && duplicate == 0) {
					//copiez fisierul sursa in directorul destinatie
					destDirectory.getFiles().add(sourceFile);
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
				}
				if(isDirectory == 1 && duplicate == 0) {
					//copiez directorul sursa in directorul destinatie
					Directory newDir = new Directory (sourceDir.getDirName());
					newDir.setDirectories(sourceDir.getDirectories());
					newDir.setFiles(sourceDir.getFiles());
					newDir.setParentDir(destDirectory);
					destDirectory.getDirectories().add(newDir);
					Collections.sort(destDirectory.getDirectories(), new CompareDirNames());
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
				}
				
			}
			else {
				//destinatia nu e valida
				writerOut.println(cf.getCommandNr());
				writerErr.println(cf.getCommandNr());
				writerErr.println("cp: cannot copy into " + dest + ": No such directory");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		writerOut.close();
		writerErr.close();
	}
}
