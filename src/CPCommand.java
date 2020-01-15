/**
 * Clasa care implementeaza comanda CP
 * 
 * In metoda executa se va apela metoda cp() 
 * a obiectului CP instantiat in CommandFactory
 * 
 * @author Maria Jianu
 *
 */
public class CPCommand extends Command{
	private CP command;
	private String dest;
	private String source;
	
	public CPCommand(CP cp, String source, String dest) {
		command = cp;
		this.source = source;
		this.dest = dest;
	}

	/* 
	 * @see Command#execute()
	 */
	@Override
	public void execute() {
		String errFile = Main.getErrFile();
		String outFile = Main.getOutFile();
		//metoda care face copierea fisierului/directorului sursa in destinatie
		command.cp(source, dest, outFile, errFile);
	}
}
