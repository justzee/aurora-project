package aurora.ide.builder.processor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.validator.AbstractValidator;

public class SxsdProcessor extends AbstractProcessor {
    private Set<String> nameSet = new HashSet<String>();

    @Override
    public void processMap(IFile file, CompositeMap map, IDocument doc) {
        List<Attribute> list = getAttributesInSchemaNotNull(map);
        for (Attribute a : list) {
            nameSet.add(a.getName().toLowerCase());
        }
        for (Map.Entry entry : (Set<Map.Entry>) map.entrySet()) {
            String k = (String) entry.getKey();
            if (nameSet.contains(k.toLowerCase()))
                continue;
            int line = map.getLocationNotNull().getStartLine();
            if (line == 0)
                line = 1;
            IRegion region = AbstractValidator.getAttributeRegion(doc, map.getLocationNotNull().getStartLine() - 1, k);
            AuroraBuilder.addMarker(file, "属性 : " + k + " , 未在 [ " + map.getName() + " ] 的Schema中定义过", line, region,
                    IMarker.SEVERITY_WARNING, AuroraBuilder.UNDEFINED_ATTRIBUTE);
        }
    }

    @Override
    public void processComplete(IFile file, CompositeMap map, IDocument doc) {

    }
}
