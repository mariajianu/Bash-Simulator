/**
 * Clasa care implementeaza comanda LS
 * 
 * In metoda executa se va apela metoda ls() 
 * a obiectului LS instantiat in CommandFactory
 * 
 * @author Maria Jianu
 *
 */
public class LSCommand extends Command{

	LS command;
	String path;
	
	public LSCommand(LS ls, String path) {
		this.command = ls;
		this.path = path;
	}
	
	@Override
	public void execute() {
		FileSystem fs = null; 
		fs = Main.getFileSys();
		String errFile = Main.getErrFile();
		String outFile = Main.getOutFile();
		command.ls(fs, path, outFile, errFile); //args[1] out, args[2] errors
	}
	
}
