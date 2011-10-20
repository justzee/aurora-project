package aurora.ide.builder.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.validator.AbstractValidator;
import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.LoadSchemaManager;
import aurora.ide.search.core.Util;

public class SxsdProcessor extends AbstractProcessor {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void processMap(IFile file, CompositeMap map, IDocument doc) {
        if (map.getNamespaceURI() == null)
            return;
        CompositeMap parent = map.getParent();
        List<Element> childs = new ArrayList<Element>();
        if (parent != null) {
            childs = CompositeMapUtil.getAvailableChildElements(parent);
            if (childs == null)
                childs = new ArrayList<Element>();
            Element ele = LoadSchemaManager.getSchemaManager().getElement(parent);
            if (ele != null) {
                childs.addAll(ele.getAllArrays());
            }
        }
        boolean ok = false;
        String mapName = map.getName();
        for (Element ele : childs) {
            if (mapName.equalsIgnoreCase(ele.getQName().getLocalName())) {
                ok = true;
                break;
            }
        }
        if (!ok && parent != null) {
            int line = map.getLocationNotNull().getStartLine();
            if (line == 0)
                line = 1;
            IRegion region = null;
            try {
                region = Util.getDocumentRegion(doc.getLineOffset(line - 1), doc.getLineLength(line - 1),
                        map.getRawName(), doc, IColorConstants.TAG_NAME);
            } catch (BadLocationException e) {
                region = new Region(0, 0);
                e.printStackTrace();
            }
            AuroraBuilder.addMarker(file, "Tag : " + mapName + " , 不应该出现在 " + parent.getName() + " 下", line, region,
                    IMarker.SEVERITY_WARNING, AuroraBuilder.UNDEFINED_TAG);
        }

        Set<String> nameSet = new HashSet<String>();
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
