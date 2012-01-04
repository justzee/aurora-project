package aurora.ide.meta.gef.editors.source.gen;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.QueryDataSet;
import aurora.ide.meta.gef.editors.models.ResultDataSet;

public class DatasetMap extends AbstractComponentMap {

	private AuroraComponent c;

	public DatasetMap(AuroraComponent c) {
		this.c = c;
	}

	@Override
	public CompositeMap toCompositMap() {
		// TODO Auto-generated method stub
		return null;
//		if (c instanceof QueryDataSet) {
//			return toQueryDataSetMap((QueryDataSet) c);
//		}
//		if (c instanceof ResultDataSet) {
//			return toResultDataSetMap((ResultDataSet) c);
//		}
	}

}
