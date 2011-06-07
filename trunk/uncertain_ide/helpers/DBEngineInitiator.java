package helpers;

import java.io.File;

import uncertain.core.UncertainEngine;

public class DBEngineInitiator{

    File mHomeDir;
    File mConfigPath;
    UncertainEngine uncertainEngine;
	
	public DBEngineInitiator(File homeDir, File configPath) {
		mHomeDir = homeDir;
        mConfigPath = configPath;
	}
	
    public void init() throws Exception {
//      String pattern = ".*\\.config";
      String pattern = "0.datasource.config";
      uncertainEngine = new UncertainEngine(mConfigPath, "uncertain.xml");
      uncertainEngine.scanConfigFiles(pattern);
      pattern = "aurora.config";
      uncertainEngine.scanConfigFiles(pattern);
      pattern = "database-config.config";
      uncertainEngine.scanConfigFiles(pattern);
  }
    /**
     * @return the mHomeDir
     */
    public File getHomeDir() {
        return mHomeDir;
    }

    /**
     * @return the uncertainEngine
     */
    public UncertainEngine getUncertainEngine() {
        return uncertainEngine;
    }

}
