package aurora.ide.search.cache;

public class CacheManager {
	private static CompositeMapCacher mapCacher;
	private static DocumentCacher documentCacher;

	public static final CompositeMapCacher getCompositeMapCacher() {
		if (mapCacher == null) {
			mapCacher = new CompositeMapCacher();
		}
		return mapCacher;
	}
	public static final DocumentCacher getDocumentCacher() {
		if (documentCacher == null) {
			documentCacher = new DocumentCacher();
		}
		return documentCacher;
	}

}
