package aurora.search.ui;

public interface ISearchContentProvider {
	public abstract void elementsChanged(Object[] updatedElements);

	public abstract void clear();

}
