/**
 * Clasa care implementeaza comanda MV
 * 
 * In metoda executa se va apela metoda mv() 
 * a obiectului MV instantiat in CommandFactory
 * 
 * @author Maria Jianu
 *
 */
public class MVCommand extends Command {
	MV command;
	String source;
	String dest;
	
	public MVCommand(MV mv, String source, String dest) {
		command = mv;
		this.source = source;
		this.dest = dest;
	}
	
	@Override
	public void execute() {
		String errFile = Main.getErrFile();
		String outFile = Main.getOutFile();
		//metoda care muta fisierul sursa in fisierul destinatie
		command.mv(source, dest, outFile, errFile);
	}

}
