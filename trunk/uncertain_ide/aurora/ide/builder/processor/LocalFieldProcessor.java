package aurora.ide.builder.processor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import uncertain.schema.IType;
import uncertain.schema.SimpleType;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.validator.AbstractValidator;
import aurora.ide.search.core.AbstractSearchService;

public class LocalFieldProcessor extends AbstractProcessor {
    private Set<String>   fieldSet  = new HashSet<String>();
    private Set<Object[]> fieldTask = new HashSet<Object[]>();

    @Override
    public void processMap(IFile file, CompositeMap map, IDocument doc) {
        processAttribute(file, map, doc);
    }

    @Override
    public void processComplete(IFile file, CompositeMap map1, IDocument doc) {
        for (Object[] objs : fieldTask) {
            String name = (String) objs[0];
            String value = (String) objs[1];
            if (fieldSet.contains(value.toLowerCase()))
                continue;
            CompositeMap map = (CompositeMap) objs[2];
            int line = map.getLocationNotNull().getStartLine();
            IRegion region = AbstractValidator.getValueRegion(doc, line - 1, name, value);
            if ("bm".equals(file.getFileExtension()))
                AuroraBuilder.addMarker(file, name + " : " + value + " 未在当前BM中定义过", line, region,
                        IMarker.SEVERITY_ERROR, AuroraBuilder.UNDEFINED_LOCALFIELD);
        }
    }

    private boolean isLocalFieldReference(IType attributeType) {
        if (attributeType instanceof SimpleType) {
            return AbstractSearchService.localFieldReference.equals(((SimpleType) attributeType)
                    .getReferenceTypeQName());
        }
        return false;
    }

    @Override
    public void visitAttribute(Attribute a, IFile file, CompositeMap map, IDocument doc) {
        if (isLocalFieldReference(a.getAttributeType())) {
            String name = a.getName();
            String value = map.getString(name);
            if (map.getName().equalsIgnoreCase("field")) {
                if (name.equalsIgnoreCase("name")) {
                    fieldSet.add(value.toLowerCase());
                    return;
                }
            }
            fieldTask.add(new Object[] { name, value, map });
        }
    }

}
