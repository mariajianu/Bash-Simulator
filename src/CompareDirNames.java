import java.util.Comparator;
/**
 * @author Maria Jianu 321CB
 *
 */
public class CompareDirNames implements Comparator<Directory> {

	public int compare(Directory d1, Directory d2) {
		//compara doua obiecte Directory dupa numele lor
		//folosesc aceasta metoda cu functia de sortare deja imprementata sort
		return d1.getDirName().compareTo(d2.getDirName());
	}
}
