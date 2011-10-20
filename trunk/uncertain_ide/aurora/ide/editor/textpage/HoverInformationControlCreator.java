package aurora.ide.editor.textpage;

import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.swt.widgets.Shell;

public class HoverInformationControlCreator extends AbstractReusableInformationControlCreator {

    @Override
    protected IInformationControl doCreateInformationControl(Shell parent) {
        HoverInformationControl bic = new HoverInformationControl(parent, "sans-serif", "Press 'F2' for focus");
        return bic;
    }
}
