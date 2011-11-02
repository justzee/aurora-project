package aurora.ide.builder.validator;

import org.eclipse.core.resources.IFile;

import aurora.ide.builder.processor.AbstractProcessor;
import aurora.ide.builder.processor.BmProcessor;
import aurora.ide.builder.processor.DataSetProcessor;
import aurora.ide.builder.processor.ForeignFieldProcessor;
import aurora.ide.builder.processor.ScreenProcessor;
import aurora.ide.builder.processor.SxsdProcessor;

public class ScreenValidator extends AbstractValidator {
	private AbstractProcessor[] aps = new AbstractProcessor[] {
			new BmProcessor(), new DataSetProcessor(), new ScreenProcessor(),
			new ForeignFieldProcessor(), new SxsdProcessor() };

	public ScreenValidator(IFile file) {
		super(file);
	}

	public ScreenValidator() {
	}

	@Override
	public AbstractProcessor[] getMapProcessor() {
		return aps;
	}

}
