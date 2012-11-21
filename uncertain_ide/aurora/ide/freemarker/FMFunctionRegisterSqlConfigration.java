package aurora.ide.freemarker;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.SAXException;

import aurora.ide.helpers.AuroraConstant;
import aurora.ide.preferencepages.FunctionRegisterPreferencePage;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FMFunctionRegisterSqlConfigration extends FMConfigration {

	private Map<String, Template> templateMap = new HashMap<String, Template>();

	public static final String CONF_FOLDER = "functionRegisterSql";

	public FMFunctionRegisterSqlConfigration() {

	}


	public Template getTemplate() throws IOException, SAXException {
		File file = FunctionRegisterPreferencePage.getTemplateFile();
		Configuration cc = this.createConfigration(file.getParent());
		Template template = cc.getTemplate(file.getName(),
				AuroraConstant.ENCODING);
		return template;
	}

	// private Configuration createConfigration() throws IOException{
	//
	// IPath append =
	// AuroraPlugin.getDefault().getStateLocation().append("functionRegisterSql");
	// /* 创建和调整配置。 */
	// Configuration cfg = new Configuration();
	// cfg.setDirectoryForTemplateLoading(append.toFile());
	// cfg.setObjectWrapper(new DefaultObjectWrapper());
	// /* 在整个应用的生命周期中,这个工作你可以执行多次 */
	// /* 获取或创建模板 */
	// return cfg;
	// }

	// private Template createTemplate(String name) throws IOException {
	// if(cfg==null){
	// cfg = createConfigration();
	// }
	// return cfg.getTemplate(name);
	// }

}
