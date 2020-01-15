/**
 * Clasa care implementeaza comanda RM
 * 
 * In metoda executa se va apela metoda rm() 
 * a obiectului RM instantiat in CommandFactory
 * 
 * @author Maria Jianu
 *
 */
public class RMCommand extends Command{
	RM command;
	String path;
	
	public RMCommand(RM rm, String path) {
		command = rm;
		this.path = path;
	}

	@Override
	public void execute() {
		String errFile = Main.getErrFile();
		String outFile = Main.getOutFile();
		//sterge fisierul/folderul de la path
		command.rm(path, outFile, errFile);
	}
	
}
