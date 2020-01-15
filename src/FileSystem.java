
/**
 * Clasa care reprezina sistemul de fisiere
 * 
 * Are root-ul si un string care arata directorul curent
 * 
 * @author Maria Jianu
 *
 */
public class FileSystem{
	//componenta Client din Composite
	
	private Directory root;
	private String currentDir;
	
	
	/**
	 * Constructor fara parametrii care 
	 * seteaza directorul curent ca root 
	 */
	public FileSystem() {
		currentDir = "/";
	}

	public Directory getRoot() {
		return root;
	}

	public void setRoot(Directory root) {
		this.root = root;
	}

	public String getCurrentDir() {
		return currentDir;
	}

	public void setCurrentDir(String currentDir) {
		this.currentDir = currentDir;
	}

}
