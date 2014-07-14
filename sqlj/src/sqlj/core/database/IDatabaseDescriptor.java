package sqlj.core.database;

public interface IDatabaseDescriptor {
	String name();
	boolean isOracle();
	boolean isMysql();
}
