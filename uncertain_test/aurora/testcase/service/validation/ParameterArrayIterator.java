/*
 * Created on 2010-9-8 下午01:30:22
 * $Id$
 */
package aurora.testcase.service.validation;

import aurora.service.validation.IParameter;
import aurora.service.validation.IParameterIterator;

public class ParameterArrayIterator implements IParameterIterator {
    
    /**
     * @param params
     */
    public ParameterArrayIterator(IParameter[] params) {
        super();
        mParams = params;
        id=0;
    }

    IParameter[]        mParams;
    int                 id=0;

    public boolean hasNext() {
        return id<mParams.length;
    }

    public IParameter next() {
        return mParams[id++];
    }

}
