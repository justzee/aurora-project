package aurora.ide.preferencepages;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

public class FunctionRegisterPreferencePage extends BaseTemplatePreferencePage {

	public static String template_dir_name = "function.register"; //$NON-NLS-1$

	public FunctionRegisterPreferencePage() {
		super();
		setDescription(Messages.FunctionRegisterPreferencePage_1);
	}

	@Override
	protected String getTemplateDirName() {
		return template_dir_name;
	}

	@Override
	protected String getNewFileName(Template tpl) {
		return super.getNewFileName(tpl);
	}

	@Override
	protected String validateTemplate(String content) {
		return super.validateTemplate(content);
	}

	public static InputStream getTemplateContent() throws IOException,
			SAXException {
		return BaseTemplatePreferencePage.getTemplateContent(template_dir_name);
	}

}