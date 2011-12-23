package aurora.ide.meta.gef.editors.models;

public class QueryDataSet extends Dataset {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4436804459187661221L;
	
	private Dataset resultDataset ;

	public void setResultDataset(Dataset rds) {
		resultDataset = rds;
	}
	
	public Dataset getResultDataset(){
		return this.resultDataset;
	}

}