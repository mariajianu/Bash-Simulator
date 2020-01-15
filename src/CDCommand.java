
/**
 * Clasa care implementeaza comanda CD
 * 
 * In metoda executa se va apela metoda CD 
 * a obiectului CD instantiat in CommandFactory
 * 
 * @author Maria Jianu
 *
 */
public class CDCommand extends Command{
	
	CD command;
	String path;
	
	public CDCommand(CD cd, String path) {
		command = cd;
		this.path = path;
	}
	
	/* 
	 * @see Command#execute()
	 */
	@Override
	public void execute() {
		FileSystem fs = null;
		fs = Main.getFileSys(); //instanta unica a sistemului de fisiere
		String errFile = Main.getErrFile();
		String outFile = Main.getOutFile();
		//metoda care schimba directorul curent
		command.cd(fs, path, outFile, errFile);
	}
	
}
