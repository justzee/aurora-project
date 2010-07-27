package aurora.database.actions;

import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class ModelJoin extends AbstractEntry{
    private String source;
    private String keyword;
    private  CompositeMap  resComposteMap= new CompositeMap();
	private String rootpath;
	private String sharefield;
	private String valuefield;
    public String getSharefield() {
		return sharefield;
	}
	public void setSharefield(String sharefiled) {
		this.sharefield = sharefiled;
	}
	public String getValuefield() {
		return valuefield;
	}
	public void setValuefield(String valuefiled) {
		this.valuefield = valuefiled;
	}
	public String getRootpath() {
		return rootpath;
	}
	public void setRootpath(String rootpath) {
		this.rootpath = rootpath;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}	

	public void run(ProcedureRunner runner) throws Exception {
		 CompositeMap cm = runner.getContext();
		 CompositeMap cmc =cm.createChildByTag(this.getRootpath());
		 String [] sourcelist = this.source.split(",");
		 String [] keylist = this.keyword.split(",");
		 for (int i =0;i<sourcelist.length;i++){
			 CompositeMap scc =(CompositeMap)cm.getObject(sourcelist[i]);
			 if(i==0){
				 this.resComposteMap=scc;
			 }else{
				// ModelJoinTest.join(this.resComposteMap, scc, keylist);
				 MatrixColumn.Matrix(this.resComposteMap, scc, keylist, this.getSharefield(), this.getValuefield());
			 }
		 }
		 //System.out.println(this.resComposteMap.toXML());
		// cmc.addChild(this.resComposteMap);
		// cmc.addChilds(this.resComposteMap.getChilds());
		 cmc.addChilds(this.resComposteMap.getChilds());
	}

}
