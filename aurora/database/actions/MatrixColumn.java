package aurora.database.actions;

import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;

public class MatrixColumn {
	public static  void Matrix(CompositeMap cm1,CompositeMap cm2,Object[] keylist,Object sharefield,Object valuefield){
		List cl1 = cm1.getChilds();
		List cl2 = cm2.getChilds();
		if(null==cl1 || null==cl2) return;
		
		Iterator it1 = cl1.iterator();	
		while (it1.hasNext()){
			CompositeMap itc1 = (CompositeMap)it1.next();
			Iterator it2 = cl2.iterator();
			while (it2.hasNext()){
				CompositeMap itc2 = (CompositeMap)it2.next();
				boolean flag=true;
				for (int i=0,l=keylist.length;i<l;i++){
					Object ob=keylist[i];
					if(!(itc1.get(ob).toString().equals(itc2.get(ob).toString()))){
						flag=false;
					}	
				}
				if (flag){
					Object key = itc2.get(sharefield);
					Object value = itc2.get(valuefield);
					
						itc1.put(key, value);					
				}
			}
		}
		
		
	}
}
