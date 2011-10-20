package aurora.ide.builder.processor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import uncertain.composite.XMLOutputter;
import uncertain.ocm.OCManager;
import uncertain.schema.Attribute;
import uncertain.schema.IType;
import uncertain.schema.SimpleType;
import aurora.bm.BusinessModel;
import aurora.ide.bm.ExtendModelFactory;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.validator.AbstractValidator;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.LogUtil;
import aurora.ide.search.core.AbstractSearchService;

public class LocalFieldProcessor extends AbstractProcessor {
    class LocalFieldCollect extends AbstractProcessor implements IterationHandle {

        private Set<String>  set = new HashSet<String>();
        private IFile        file;
        private CompositeMap map;

        public LocalFieldCollect(IFile file) {
            try {
                this.file = file;
                CompositeMap bm = AuroraResourceUtil.loadFromResource(file);
                BusinessModel r = createResult(bm, file);
                String str = XMLOutputter.defaultInstance().toXML(r.getObjectContext(), true);
                map = CompositeMapUtil.loaderFromString(str);
            } catch (Exception e) {
                LogUtil.getInstance().log(IStatus.ERROR, file.getName() + "解析异常", e);
                AuroraBuilder.addMarker(file, e.getMessage(), 1, IMarker.SEVERITY_ERROR, AuroraBuilder.FATAL_ERROR);
            }
        }

        public Set<String> collect() {
            if (map != null)
                map.iterate(this, true);
            return set;
        }

        private BusinessModel createResult(CompositeMap config, IFile file) {
            ExtendModelFactory factory = new ExtendModelFactory(OCManager.getInstance(), file);
            return factory.getModel(config);
        }

        public int process(CompositeMap map) {
            processMap(file, map, null);
            return 0;
        }

        @Override
        public void processComplete(IFile file, CompositeMap map, IDocument doc) {

        }

        @Override
        public void processMap(IFile file, CompositeMap map, IDocument doc) {
            processAttribute(file, map, doc);
        }

        @Override
        protected void visitAttribute(Attribute a, IFile file, CompositeMap map, IDocument doc) {
            if (isLocalFieldReference(a.getAttributeType())) {
                String name = a.getName();
                String value = map.getString(name);
                if (map.getName().equalsIgnoreCase("field") || map.getName().equalsIgnoreCase("ref-field")) {
                    if (name.equalsIgnoreCase("name")) {
                        set.add(value.toLowerCase());
                        return;
                    }
                }
            }
        }

    }

    private Set<Object[]> fieldTask = new HashSet<Object[]>();

    private boolean isLocalFieldReference(IType attributeType) {
        if (attributeType instanceof SimpleType) {
            return AbstractSearchService.localFieldReference.equals(((SimpleType) attributeType)
                    .getReferenceTypeQName());
        }
        return false;
    }

    @Override
    public void processComplete(IFile file, CompositeMap map1, IDocument doc) {
        Set<String> fieldSet = new LocalFieldCollect(file).collect();
        if (fieldSet.size() == 0)
            return;
        for (Object[] objs : fieldTask) {
            String name = (String) objs[0];
            String value = (String) objs[1];
            if (fieldSet.contains(value.toLowerCase()))
                continue;
            CompositeMap map = (CompositeMap) objs[2];
            int line = map.getLocationNotNull().getStartLine();
            IRegion region = AbstractValidator.getValueRegion(doc, line - 1, name, value);
            if ("bm".equals(file.getFileExtension()))
                AuroraBuilder.addMarker(file, name + " : " + value + " 未在BM中定义过", line, region, IMarker.SEVERITY_ERROR,
                        AuroraBuilder.UNDEFINED_LOCALFIELD);
        }
    }

    @Override
    public void processMap(IFile file, CompositeMap map, IDocument doc) {
        processAttribute(file, map, doc);
    }

    @Override
    public void visitAttribute(Attribute a, IFile file, CompositeMap map, IDocument doc) {
        if (isLocalFieldReference(a.getAttributeType())) {
            String name = a.getName();
            String value = map.getString(name);
            if (map.getName().equalsIgnoreCase("field") || map.getName().equalsIgnoreCase("ref-field")) {
                if (name.equalsIgnoreCase("name")) {
                    // fieldSet.add(value.toLowerCase());
                    return;
                }
            }
            fieldTask.add(new Object[] { name, value, map });
        }
    }

}
