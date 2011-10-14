package aurora.ide.builder.validator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import aurora.ide.builder.processor.AbstractProcessor;
import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.Util;

public abstract class AbstractValidator implements IterationHandle {
    private IFile        file;
    private CompositeMap map;
    private IDocument    doc;

    public AbstractValidator(IFile file) {
        super();
        this.file = file;
        try {
            map = CacheManager.getCompositeMapCacher().getCompositeMap(file);
            doc = CacheManager.getDocumentCacher().getDocument(file);
        } catch (CoreException e) {
            e.printStackTrace();
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
    }

    public final void validate() {
        map.iterate(this, true);
        for (AbstractProcessor np : getMapProcessor()) {
            np.processComplete(file, map, doc);
        }
    }

    public int process(CompositeMap map) {
        for (AbstractProcessor np : getMapProcessor()) {
            np.processMap(file, map, doc);
        }
        return 0;
    }

    /**
     * 为了能使AbstractProcessor保存自己的数据,应确保此方法没次调用返回相同的值
     * 
     * @return
     */
    public abstract AbstractProcessor[] getMapProcessor();

    public static IRegion getAttributeRegion(IDocument doc, int line, String attrName) {
        try {
            int offset = doc.getLineOffset(line);
            int length = doc.getLineLength(line);
            return Util.getAttributeRegion(offset, length, attrName, doc);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 在doc的lineno行中查找属性名为name,值为value的属性
     * 
     * @param doc
     * @param lineno
     *            行号,从0开始
     * @param name
     *            属性名
     * @param value
     *            属性值 (不包含引号)
     * @return value的IRegion
     */
    public static IRegion getValueRegion(IDocument doc, int lineno, String name, String value) {
        try {
            int offset = doc.getLineOffset(lineno);
            int length = doc.getLineLength(lineno);
            IRegion aregion = Util.getAttributeRegion(offset, length, name, doc);
            if (aregion == null)
                return null;
            IRegion vregion = Util.getValuePartRegion(aregion.getOffset(), length + offset - aregion.getOffset(),
                    value, doc, IColorConstants.STRING);
            return vregion;
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return null;
    }

}
