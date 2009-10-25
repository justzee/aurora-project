package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;


public class PropertyActions {
	IViewerDirty dirtyObject;
	private IPropertyCategory mCategoryObject;
	public PropertyActions(IViewerDirty dirtyObject,IPropertyCategory categoryObject){
		this.dirtyObject = dirtyObject;
		mCategoryObject = categoryObject;
	}
	
	/**
	 * 自定义方法。生成六个Action对象，并通过工具栏管理器ToolBarManager填充进工具栏
	 */
	public void fillActionToolBars(ToolBarManager actionBarManager) {
		// 生成按钮，按钮就是一个个的Action
		Action addAction = new AddPropertyAction(dirtyObject);

		Action removeAction = new RemovePropertyAction(dirtyObject);
		Action refreshAction = new RefreshAction(dirtyObject);

		CategroyAction categroyAction = new CategroyAction(mCategoryObject);
		CharSortAction charSortAction = new CharSortAction(mCategoryObject);
		/*
		 * 将按钮通过工具栏管理器ToolBarManager填充进工具栏,如果用add(action)
		 * 也是可以的，只不过只有文字没有图像。要显示图像需要将Action包装成
		 * ActionContributionItem，在这里我们将包装的处理过程写成了一个方法。
		 * 
		 */
		actionBarManager.add(createActionContributionItem(removeAction));
		actionBarManager.add(createActionContributionItem(refreshAction));
		actionBarManager.add(createActionContributionItem(addAction));
		actionBarManager.add(createActionContributionItem(categroyAction));
		actionBarManager.add(createActionContributionItem(charSortAction));

		// 更新工具栏。没有这一句，工具栏上会没有任何显示
		actionBarManager.update(true);
	}
	ActionContributionItem createActionContributionItem(IAction action) {
		ActionContributionItem aci = new ActionContributionItem(action);
		aci.setMode(ActionContributionItem.MODE_FORCE_TEXT);// 显示图像+文字
		return aci;
	}
}
