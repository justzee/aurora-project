package aurora.ide.builder.validator;

import org.eclipse.core.resources.IFile;

import aurora.ide.builder.processor.AbstractProcessor;
import aurora.ide.builder.processor.BmProcessor;
import aurora.ide.builder.processor.SxsdProcessor;

public class SvcValidator extends AbstractValidator {
    private AbstractProcessor[] aps = new AbstractProcessor[] { new BmProcessor(), new SxsdProcessor() };

    public SvcValidator(IFile file) {
        super(file);
    }

    @Override
    public AbstractProcessor[] getMapProcessor() {
        return aps;
    }
}
