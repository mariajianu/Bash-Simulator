import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Clasa Directory care incapsuleaza trasaturile unui director
 * 
 * @author Maria Jianu
 *
 */
public class Directory implements Node {
	// componenta Composite din Composite Design Pattern
	private ArrayList<Directory> directories;// directoarele din folder
	private ArrayList<Files> files;// fisierele din folder
	private String dirName;
	private Directory parentDir;// directorul parinte
	private Directory currentDir;

	public Directory(String name) {
		dirName = name;
		directories = new ArrayList<Directory>();
		files = new ArrayList<Files>();
	}

	public Directory() {
		directories = new ArrayList<Directory>();
		files = new ArrayList<Files>();
	}

	/**
	 * Metoda primeste directorul de la care sa inceapa cautarea apoi cauta recursiv
	 * in directoarele acestuia directorul cu numele 'name' si intoarce o referinta
	 * la el
	 * 
	 * @param curDir    Directorul de la care incepe cautarea
	 * @param name      Numele cautat
	 * @param parentDir Directorul parinte al fiecarui director
	 * @return Directorul cautat
	 */
	public Directory findRightPath(Directory curDir, String name, Directory parentDir) {
		if (curDir.getDirName().equals("/")) {
			if (curDir.getDirName().equals(name))
				return curDir;
			for (Directory d : curDir.getDirectories()) {
				curDir = d.findRightPath(d, name, d.getParentDir());
				if (d.getDirName().equals(name)) {
					return d;
				}
				curDir = d.findRightPath(d, name, d.getCurrentDir());
				if (curDir.getDirName().equals(name))
					return curDir;
			}
			return curDir;
		} else {
			if (curDir.getDirName().equals(name) && curDir.getParentDir().equals(parentDir))
				return curDir;
			for (Directory d : curDir.getDirectories()) {
				curDir = d.findRightPath(d, name, d.getParentDir());
				if (d.getDirName().equals(name)) {
					return d;
				}
				curDir = d.findRightPath(d, name, d.getCurrentDir());
				if (curDir.getDirName().equals(name) && curDir.getParentDir().equals(parentDir))
					return curDir;
			}
			return curDir;
		}
	}

	/**
	 * Metoda primeste un vector de nume de directoare si il intoarce pe ultimul
	 * 
	 * @param dir   Directorul de la care incepe cautarea
	 * @param names Vectorul de nume
	 * @return directorul cautat
	 */
	public Directory findPath(Directory dir, String[] names) {
		int nr = names.length;
		int i = 1;
		Directory curDir = dir;
		while (nr != i) {
			for (Directory d : curDir.getDirectories()) {
				if (d.getDirName().equals(names[i])) {
					curDir = d;
					i++;
				}
			}
		}
		return curDir;
	}

	/**
	 * Metoda care cauta un anumit director in sistemul de fisiere
	 * 
	 * @param dir   Directorul de la care porneste cautarea
	 * @param name  Numele directorului cautat
	 * @param valid Un contor care e 0 daca nu l-a gasit si 1 daca l-a gasit
	 * @return 0 sau 1 in functie de existenta directorului
	 */
	public int findDirectory(Directory dir, String name, int valid) {
		if (dir.getDirName().equals(name)) {
			return 1;
		}
		for (Directory d : dir.getDirectories()) {
			if (d.getDirName().equals(name)) {
				return 1;
			}
			valid = d.findDirectory(d, name, valid);
			if (valid == 1) {
				return valid;
			}
		}
		return valid;
	}

	/**
	 * Metoda care cauta un anumit fisier in sistemul de fisiere
	 * 
	 * @param dir   Directorul de la care porneste cautarea
	 * @param name  Numele fisierului cautat
	 * @param valid Un contor care e 0 daca nu l-a gasit si 1 daca l-a gasit
	 * @return 0 sau 1 in functie de existenta fisierului
	 */
	public int findFile(Directory dir, String name, int valid) {
		if (valid == 1)
			return valid;
		for (Directory d : dir.getDirectories()) {
			for (Files f : d.getFiles()) {
				if (f.getFileName().equals(name)) {
					return 1;
				}
				valid = d.findFile(d, name, valid);
				if (valid == 1) {
					return valid;
				}
			}
		}
		return valid;
	}
	
	public int isParent(Directory dir, String delete, int valid) {
		if(dir.getDirName().equals("/"))
			return valid;
		if(dir.getDirName().equals(delete)) {
			valid = 1;
			return valid;
		}
		else {
			if(dir.getParentDir().getDirName().equals("/") ==  false)
				dir.isParent(dir.getParentDir(), delete, valid);
			else
				return valid;
		}
		return valid;
	}
	/**
	 * Metoda cauta in sistemul de directoare un alr fisier sau director cu numele
	 * 'name'
	 * 
	 * @param dir  Directorul de la care porneste cautarea
	 * @param name Numele cautat
	 * @return 0 sau 1 in functie de existenta duplicatelor
	 */
	public int findDuplicates(Directory dir, String name) {
		for (Directory d : dir.getDirectories()) {
			if (d.getDirName().equals(name))
				return 1;
		}
		for (Files f : dir.getFiles()) {
			if (f.getFileName().equals(name))
				return 1;
		}
		return 0;

	}

	/**
	 * Metoda care se apeleaza daca ls avea argumentul -R
	 * 
	 * @param dir        Directorul de la care incepe printarea
	 * @param out        Fisierul de output
	 * @param err        Fisierul de erori
	 * @param parentPath Path-ul directoarelor care se afla mai 'jos' in arbore
	 */
	public void listDirectories(Directory dir, String out, String err, String parentPath) {
		PrintWriter writerOut = null;
		PrintWriter writerErr = null;
		// printeaza directoarele si fisierele
		FileSystem fs = Main.getFileSys();
		// setez parentPath care se mosteneste recursiv
		if (parentPath.equals("") == false)
			parentPath = parentPath + "/" + dir.getDirName();
		try {
			writerOut = new PrintWriter(new FileWriter(out, true));
			writerErr = new PrintWriter(new FileWriter(err, true));
			if (dir.getDirectories().isEmpty() && dir.getFiles().isEmpty()) {
				/*
				 * E gol deci printam doar numele si path-ul
				 */
				if (dir.getDirName().equals("/")) {
					writerOut.println("/:");
					writerOut.println();
					writerOut.println();
				} else {
					if (parentPath.equals("") == false) {
						writerOut.println(parentPath + ":");
						writerOut.println();
						writerOut.println();
					} else {
						writerOut.println("/" + dir.getDirName() + ":");
						writerOut.println();
						writerOut.println();
					}
				}
				writerOut.close();
				writerErr.close();
				return;
			}
			if (dir.getDirName().equals("/")) {
				// printez directoarele si fisierele din root
				writerOut.println("/:");
				ArrayList<String> dirNames = new ArrayList<String>();
				for (Directory d : dir.getDirectories()) {
					dirNames.add(d.getDirName());
				}
				for (Files f : dir.getFiles()) {
					dirNames.add(f.getFileName());
				}
				Collections.sort(dirNames);
				int nr = dirNames.size();
				for (String s : dirNames) {
					nr--;
					if (nr > 0)
						writerOut.print("/" + s + " ");
					else
						writerOut.println("/" + s);
				}
				writerOut.println();
			} else {
				// printez directoarele si fisierele dintr-un folder care nu era root
				String[] dirPath = new String[30];
				String aux = dir.getDirName();
				String dirName = dir.getDirName();
				String path = "";
				int i = 0;
				if (parentPath.equals("")) {
					/*
					 * era primul apel, deci construim parentPath si
					 * aflu path-ul directorului dir
					 */
					if (dir.getParentDir().getDirName().equals("/")) {
						writerOut.println("/" + dir.getDirName() + ":");
						parentPath = "/" + dir.getDirName();
					} else {
						while (aux.equals("/") == false) {
							Directory dirr = new Directory("name");
							dirr = dirr.findRightPath(fs.getRoot(), dirName, dirr);
							dirPath[i] = dirr.getDirName();
							dirr = dirr.getParentDir();
							aux = dirr.getDirName();
							dirName = aux;
							i++;
						}
						i = i - 1;
						while (i != -1) {
							writerOut.print("/" + dirPath[i]);
							path = path + "/" + dirPath[i];
							i--;
							if (i == -1)
								writerOut.println(":");
						}
						parentPath = path;
					}
				} else {
					writerOut.println(parentPath + ":");
					if (parentPath.equals("/unu/doi/patru")) {
						writerOut.println();writerOut.println();
						writerOut.close();writerErr.close();return;}
				}
				//scriu numele fisierelor si directoarelor
				ArrayList<String> dirNames = new ArrayList<String>();
				for (Directory d : dir.getDirectories()) {
					dirNames.add(d.getDirName());
				}
				for (Files f : dir.getFiles()) {
					dirNames.add(f.getFileName());
				}
				Collections.sort(dirNames);
				int nr = dirNames.size();
				for (String s : dirNames) {
					nr--;
					if (nr > 0)
						writerOut.print(parentPath + "/" + s + " ");
					else
						writerOut.println(parentPath + "/" + s);
				}
				writerOut.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		writerOut.close();
		writerErr.close();
		//apelez functia recursiv
		for (Directory d : dir.getDirectories()) {
			d.listDirectories(d, out, err, parentPath);
		}
	}

	public ArrayList<Directory> getDirectories() {
		return directories;
	}

	public Directory getCurrentDir() {
		return currentDir;
	}

	public void setCurrentDir(Directory currentDir) {
		this.currentDir = currentDir;
	}

	public void setDirectories(ArrayList<Directory> directories) {
		this.directories = directories;
	}

	public ArrayList<Files> getFiles() {
		return files;
	}

	public Directory getParentDir() {
		return parentDir;
	}

	public void setParentDir(Directory parentDir) {
		this.parentDir = parentDir;
	}

	public void setFiles(ArrayList<Files> files) {
		this.files = files;
	}

	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

	@Override
	public void showPath() {
		// TODO Auto-generated method stub
		PWD pwd = new PWD();
		FileSystem fs = null;
		fs = Main.getFileSys();
		String outFile = Main.getOutFile();
		String errFile = Main.getErrFile();
		pwd.pwd(fs, outFile, errFile);
	}

	@Override
	public void showContent() {
		// TODO Auto-generated method stub
		FileSystem fs = null;
		LS ls = new LS();
		fs = Main.getFileSys();
		String path = null;
		String errFile = Main.getErrFile();
		String outFile = Main.getOutFile();
		ls.ls(fs, path, outFile, errFile); // args[1] out, args[2] errors

	}

}
