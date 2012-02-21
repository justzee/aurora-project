package aurora.statistics.map;

import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import uncertain.composite.QualifiedName;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import uncertain.schema.ISchemaManager;
import uncertain.schema.IType;
import uncertain.schema.SimpleType;
import aurora.ide.search.core.Util;
import aurora.statistics.IStatisticsReporter;
import aurora.statistics.Statistician;
import aurora.statistics.model.ProjectObject;

public class ProjectObjectIteator implements IterationHandle {
	private ProjectObject po;
	private IStatisticsReporter reporter;
	private Statistician statistician;
	public final static QualifiedName bmReference = new QualifiedName(
			"http://www.aurora-framework.org/schema/bm", "model");
	public final static QualifiedName screenReference = new QualifiedName(
			"http://www.aurora-framework.org/application", "screen");

	public final static QualifiedName urlReference = new QualifiedName(
			"http://www.aurora-framework.org/application", "screenBm");
	public final static QualifiedName rawSql = new QualifiedName(
			"http://www.aurora-framework.org/application", "RawSql");

	public ProjectObjectIteator(Statistician statistician, ProjectObject po) {
		this.statistician = statistician;
		this.po = po;
	}

	public void process(IStatisticsReporter reporter) {
		this.reporter = reporter;
		CompositeMap rootMap = po.getRootMap();
		rootMap.iterate(this, true);
	}

	public int process(CompositeMap map) {
		StatisticsMap sm = new StatisticsMap(map);
		report(sm);
		return IterationHandle.IT_CONTINUE;
	}

	private void report(StatisticsMap sm) {
		if (sm.isRoot()) {
			this.reporter.reportRoot(po, sm);
		}
		if (sm.isTag()) {
			this.reporter.reportTag(po, sm);
		}
		if (isDependency(po, sm)) {
			this.reporter.reportDependency(po, sm);
		}
		if (isScript(po, sm)) {
			this.reporter.reportScript(po, sm);
		}
	}

	private boolean isScript(ProjectObject po, StatisticsMap sm) {
		if ("script".equalsIgnoreCase(sm.getName())) {
			return true;
		}
		Element element = getElement(sm.getMap());
		if (element == null) {
			return false;
		}
		if (element.getType() != null
				&& element.getElementType() instanceof SimpleType
				&& rawSql.equals(element.getElementType().getQName())) {
			return true;
		}

		return isAttributeType(sm, rawSql);
	}

	public Element getElement(CompositeMap map) {
		try {
			ISchemaManager schemaManager = statistician.getSchemaManager();
			return schemaManager.getElement(map);
		} catch (IllegalArgumentException e) {
		}
		return null;
	}

	private boolean isAttributeType(StatisticsMap sm, QualifiedName qName) {
		Element element = getElement(sm.getMap());
		if (element != null) {
			List attrib_list = element.getAllAttributes();
			for (Iterator it = attrib_list.iterator(); it.hasNext();) {
				Attribute attrib = (Attribute) it.next();
				IType attributeType = attrib.getAttributeType();
				if (attributeType != null
						&& qName.equals(attributeType.getQName())
						// && sm.getMap().getBoolean(attrib.getName()) != null
						&& Util.getValueIgnoreCase(attrib, sm.getMap()) != null) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isDependency(ProjectObject po, StatisticsMap sm) {
		if (this.isAttributeType(sm, bmReference)
				|| this.isAttributeType(sm, screenReference)
				|| this.isAttributeType(sm, urlReference)) {
			return true;
		}
		if (statistician.isDependecyContainJS()
				&& sm.getMap().getName() == null) {
			return true;
		}
		return false;
	}

}
