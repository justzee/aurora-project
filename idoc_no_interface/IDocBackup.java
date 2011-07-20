package aurora.plugin.sap.sync.idoc;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;

public class IDocBackup extends Thread {
	public static final String SYNC = "sync";
	public IDocServer iDocServer;
	public List errorIdocTypes = new LinkedList();
	public int header_id;
	public IdocType idocType;
	public IDocBackup(IDocServer iDocServer) {
		this.iDocServer = iDocServer;
	}
	public void run() {
		while (!isFinished()) {
			idocType = null;
			header_id = -1;
			IDocFile file = iDocServer.getBckupFile();
			if (file == null)
				continue;
			try {
				header_id = iDocServer.getDbUtil().existHeaders(file.getIdocId());
				insertInterface(file);
			} catch (Throwable e) {
				try {
					iDocServer.log(e);
					if (idocType != null) {
						errorIdocTypes.add(idocType);
					}
					String errorMessage= "interface failed";
					iDocServer.log("updateIdocStatus for idoc:" + file.getIdocId()+" "+errorMessage);
					iDocServer.getDbUtil().updateIdocsStatus(file.getIdocId(), errorMessage);
				} catch (Throwable e1) {
					iDocServer.log(e1);
				}
				continue;
			}
			iDocServer.log("idoc:" + file.getIdocId() + " backup successful !");
		}
	}
	private void insertInterface(IDocFile file) throws ApplicationException {
		try {
			if (header_id != -1) {
				iDocServer.log("parser " + file.getPath() + " file for backup ");
				CompositeLoader loader = new CompositeLoader();
				CompositeMap iDocData = loader.loadByFile(file.getPath());
				CompositeMap idoc_node = iDocData.getChild(IDocFile.IDOC_NODE);
				if (idoc_node == null || idoc_node.getChildIterator() == null || idoc_node.getChilds().size() < 2) {
					return;
				}
				CompositeMap control_node = (CompositeMap) idoc_node.getChilds().get(0);
				idocType = iDocServer.getDbUtil().getIdocType(control_node);
				if (isStop()) {
					throw new ApplicationException("This idocType:" + idocType + " has error before");
				}
				iDocServer.getDbUtil().getConnection().setAutoCommit(false);
				iDocServer.getDbUtil().updateIdocInfo(file.getIdocId(), control_node);
				for (int i = 1; i < idoc_node.getChilds().size(); i++) {
					CompositeMap content_node = (CompositeMap) idoc_node.getChilds().get(i);
					iDocServer.getDbUtil().registerInterfaceLine(header_id, content_node);
				}
				iDocServer.getDbUtil().getConnection().commit();
				iDocServer.getDbUtil().getConnection().setAutoCommit(true);
			}
		} catch (IOException e) {
			throw new ApplicationException(e);
		} catch (SAXException e) {
			throw new ApplicationException(e);
		} catch (SQLException e) {
			throw new ApplicationException(e);
		} finally {
			try {
				iDocServer.getDbUtil().getConnection().rollback();
				iDocServer.getDbUtil().getConnection().setAutoCommit(true);
			} catch (SQLException e) {
				iDocServer.log(e);
			}
		}
		if (iDocServer.isDeleteImmediately()) {
			File deleteFile = new File(file.getPath());
			if (deleteFile.exists()) {
				iDocServer.log("delete file " + file.getPath() + " " + deleteFile.delete());
			}
		}
	}
	public boolean isFinished() {
		return iDocServer.isShutDown();
	}
	private boolean isStop() throws SQLException, ApplicationException {
		String handleModel = iDocServer.getDbUtil().getHandleModel(idocType.getIdoctyp(), idocType.getCimtyp());
		if (SYNC.equals(handleModel) && errorIdocTypes.contains(idocType)) {
			return true;
		}
		return false;
	}
}
