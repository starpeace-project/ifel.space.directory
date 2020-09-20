package space.ifel.directory;

public class NoSuchCommandException extends Exception {
    public NoSuchCommandException() {
        super("The command requested does not exist.");
    }
}
