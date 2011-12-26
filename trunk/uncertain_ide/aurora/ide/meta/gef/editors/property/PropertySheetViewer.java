package aurora.ide.meta.gef.editors.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.IPropertySheetEntryListener;

public class PropertySheetViewer extends Viewer {
	// The input objects for the viewer
	private Object[] input;

	// The root entry of the viewer
	private IPropertySheetEntry rootEntry;

	private PropertyViewer pv2;
	private HashMap<IPropertySheetEntry, PropertyItem> entryToItemMap = new HashMap<IPropertySheetEntry, PropertyItem>();

	private IPropertySheetEntryListener entryListener;

	// the property sheet sorter
	private PropertySheetSorter sorter = new PropertySheetSorter();

	public PropertySheetViewer(Composite parent) {
		pv2 = new PropertyViewer(parent, SWT.NONE);
		// configure the widget
		// tree.setLinesVisible(true);
		// tree.setHeaderVisible(true);

		// configure the columns
		// addColumns();

		// create the entry and editor listener
		createEntryListener();
		// createEditorListener();
	}

	/**
	 * Add columns to the tree and set up the layout manager accordingly.
	 */
	// private void addColumns() {
	//
	// // create the columns
	// TreeColumn[] columns = tree.getColumns();
	// for (int i = 0; i < columnLabels.length; i++) {
	// String string = columnLabels[i];
	// if (string != null) {
	// TreeColumn column;
	// if (i < columns.length) {
	// column = columns[i];
	// } else {
	// column = new TreeColumn(tree, 0);
	// }
	// column.setText(string);
	// }
	// }
	// }

	/**
	 * Creates a new property sheet entry listener.
	 */
	private void createEntryListener() {
		entryListener = new IPropertySheetEntryListener() {
			public void childEntriesChanged(IPropertySheetEntry entry) {
				// update the children of the given entry
				if (entry == rootEntry) {
					updateChildrenOf(entry, pv2);
				}
			}

			public void valueChanged(IPropertySheetEntry entry) {
				// update the given entry
				PropertyItem item = findItem(entry);
				if (item != null) {
					updateEntry(entry, item);
				}
			}

			public void errorMessageChanged(IPropertySheetEntry entry) {
			}
		};
	}

	/**
	 * Creates a new tree item, sets the given entry or category (node)in its
	 * user data field, and adds a listener to the node if it is an entry.
	 * 
	 * @param node
	 *            the entry or category associated with this item
	 * @param parent
	 *            the parent widget
	 * @param index
	 *            indicates the position to insert the item into its parent
	 */
	private void createItem(IPropertySheetEntry node, int index) {
		// create the item
		PropertyItem item = pv2.createItem(node, index);

		// Cache the entry <-> tree item relationship
		entryToItemMap.put(node, item);

		// Always ensure that if the tree item goes away that it's
		// removed from the cache
		item.getControl(pv2).addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				Object possibleEntry = e.widget.getData();
				if (possibleEntry != null)
					entryToItemMap.remove(possibleEntry);
			}
		});

		node.addPropertySheetEntryListener(entryListener);
		// updateEntry(node, item);

	}

	/**
	 * Sends out a selection changed event for the entry tree to all registered
	 * listeners.
	 */
	private void entrySelectionChanged() {
		SelectionChangedEvent changeEvent = new SelectionChangedEvent(this,
				getSelection());
		fireSelectionChanged(changeEvent);
	}

	/**
	 * Return a tree item in the property sheet that has the same entry in its
	 * user data field as the supplied entry. Return <code>null</code> if there
	 * is no such item.
	 * 
	 * @param entry
	 *            the entry to serach for
	 * @return the TreeItem for the entry or <code>null</code> if there isn't
	 *         one.
	 */
	private PropertyItem findItem(IPropertySheetEntry entry) {
		// Iterate through treeItems to find item
		PropertyItem[] items = pv2.getItems();
		for (int i = 0; i < items.length; i++) {
			PropertyItem item = items[i];
			PropertyItem findItem = findItem(entry, item);
			if (findItem != null) {
				return findItem;
			}
		}
		return null;
	}

	/**
	 * Return a tree item in the property sheet that has the same entry in its
	 * user data field as the supplied entry. Return <code>null</code> if there
	 * is no such item.
	 * 
	 * @param entry
	 *            the entry to search for
	 * @param item
	 *            the item look in
	 * @return the TreeItem for the entry or <code>null</code> if there isn't
	 *         one.
	 */
	private PropertyItem findItem(IPropertySheetEntry entry, PropertyItem item) {
		// If we can find the TreeItem in the cache, just return it
		Object mapItem = entryToItemMap.get(entry);
		if (mapItem != null && mapItem instanceof PropertyItem)
			return (PropertyItem) mapItem;

		// compare with current item
		if (entry == item.getData()) {
			return item;
		}

		return null;
	}

	/**
	 * Returns the sorted children of the given category or entry
	 * 
	 * @param node
	 *            a category or entry
	 * @return the children of the given category or entry (element type
	 *         <code>IPropertySheetEntry</code> or
	 *         <code>PropertySheetCategory</code>)
	 */
	private List<IPropertySheetEntry> getChildren(Object node) {
		// cast the entry or category
		IPropertySheetEntry entry = null;
		if (node instanceof IPropertySheetEntry) {
			entry = (IPropertySheetEntry) node;
		} else
			return Collections.emptyList();
		return getChildren(entry);
	}

	private List<IPropertySheetEntry> getChildren(IPropertySheetEntry entry) {
		// if the entry is the root and we are showing categories, and we have
		// more than the
		// defualt category, return the categories

		// return the sorted & filtered child entries
		return getSortedEntries(getFilteredEntries(entry.getChildEntries()));
	}

	/*
	 * (non-Javadoc) Method declared on Viewer.
	 */
	public Control getControl() {
		return pv2;
	}

	/**
	 * Returns the entries which match the current filter.
	 * 
	 * @param entries
	 *            the entries to filter
	 * @return the entries which match the current filter (element type
	 *         <code>IPropertySheetEntry</code>)
	 */
	private List<IPropertySheetEntry> getFilteredEntries(
			IPropertySheetEntry[] entries) {
		// if no filter just return all entries

		// check each entry for the filter
		List<IPropertySheetEntry> filteredEntries = new ArrayList<IPropertySheetEntry>(
				entries.length);
		for (int i = 0; i < entries.length; i++) {
			IPropertySheetEntry entry = entries[i];
			if (entry != null) {
				String[] filters = entry.getFilters();
				boolean expert = false;
				if (filters != null) {
					for (int j = 0; j < filters.length; j++) {
						if (filters[j]
								.equals(IPropertySheetEntry.FILTER_ID_EXPERT)) {
							expert = true;
							break;
						}
					}
				}
				if (!expert) {
					filteredEntries.add(entry);
				}
			}
		}
		return filteredEntries;
	}

	/**
	 * Returns a sorted list of <code>IPropertySheetEntry</code> entries.
	 * 
	 * @param unsortedEntries
	 *            unsorted list of <code>IPropertySheetEntry</code>
	 * @return a sorted list of the specified entries
	 */
	private List<IPropertySheetEntry> getSortedEntries(
			List<IPropertySheetEntry> unsortedEntries) {
		IPropertySheetEntry[] propertySheetEntries = (IPropertySheetEntry[]) unsortedEntries
				.toArray(new IPropertySheetEntry[unsortedEntries.size()]);
		sorter.sort(propertySheetEntries);
		return Arrays.asList(propertySheetEntries);
	}

	/**
	 * The <code>PropertySheetViewer</code> implementation of this method
	 * declared on <code>IInputProvider</code> returns the objects for which the
	 * viewer is currently showing properties. It returns an
	 * <code>Object[]</code> or <code>null</code>.
	 */
	public Object getInput() {
		return input;
	}

	/**
	 * Returns the root entry for this property sheet viewer. The root entry is
	 * not visible in the viewer.
	 * 
	 * @return the root entry or <code>null</code>.
	 */
	public IPropertySheetEntry getRootEntry() {
		return rootEntry;
	}

	/**
	 * The <code>PropertySheetViewer</code> implementation of this
	 * <code>ISelectionProvider</code> method returns the result as a
	 * <code>StructuredSelection</code>.
	 * <p>
	 * Note that this method only includes <code>IPropertySheetEntry</code> in
	 * the selection (no categories).
	 * </p>
	 */
	public ISelection getSelection() {
		// if (tree.getSelectionCount() == 0) {
		// return StructuredSelection.EMPTY;
		// }
		// TreeItem[] sel = tree.getSelection();
		List<IPropertySheetEntry> entries = new ArrayList<IPropertySheetEntry>(
				0);
		// for (int i = 0; i < sel.length; i++) {
		// TreeItem ti = sel[i];
		// Object data = ti.getData();
		// if (data instanceof IPropertySheetEntry) {
		// entries.add((IPropertySheetEntry) data);
		// }
		// }
		return new StructuredSelection(entries);
	}

	/**
	 * Updates all of the items in the tree.
	 * <p>
	 * Note that this means ensuring that the tree items reflect the state of
	 * the model (entry tree) it does not mean telling the model to update
	 * itself.
	 * </p>
	 */
	public void refresh() {
		if (rootEntry != null) {
			updateChildrenOf(rootEntry, pv2);
		}
	}

	/**
	 * Remove the given item from the tree. Remove our listener if the item's
	 * user data is a an entry then set the user data to null
	 * 
	 * @param item
	 *            the item to remove
	 */
	private void removeItem(PropertyItem item) {
		IPropertySheetEntry data = item.getData();
		data.removePropertySheetEntryListener(entryListener);

		// We explicitly remove the entry from the map since it's data has been
		// null'd
		entryToItemMap.remove(data);
		pv2.removeItem(item);

	}

	/**
	 * Reset the selected properties to their default values.
	 */
	public void resetProperties() {
		// Determine the selection
		IStructuredSelection selection = (IStructuredSelection) getSelection();

		// Iterate over entries and reset them
		Iterator<?> itr = selection.iterator();
		while (itr.hasNext()) {
			((IPropertySheetEntry) itr.next()).resetPropertyValue();
		}
	}

	/**
	 * The <code>PropertySheetViewer</code> implementation of this method
	 * declared on <code>Viewer</code> method sets the objects for which the
	 * viewer is currently showing properties.
	 * <p>
	 * The input must be an <code>Object[]</code> or <code>null</code>.
	 * </p>
	 * 
	 * @param newInput
	 *            the input of this viewer, or <code>null</code> if none
	 */
	public void setInput(Object newInput) {
		// need to save any changed value when user clicks elsewhere
		// applyEditorValue();

		// set the new input to the root entry
		input = (Object[]) newInput;
		if (input == null) {
			input = new Object[0];
		}

		if (rootEntry != null) {
			rootEntry.setValues(input);
			// ensure first level children are visible
			updateChildrenOf(rootEntry, pv2);
		}

		// Clear any previous StatusLine messages
		// updateStatusLine(null);
		// for (TreeItem ti : tree.getItems()) {
		// Object data = ti.getData();
		// if (data instanceof PropertySheetEntry) {
		// Control ctrl = ((PropertySheetEntry) data).getEditor(tree)
		// .getControl();
		// ctrl.addDisposeListener(new DisposeListener() {
		//
		// public void widgetDisposed(DisposeEvent e) {
		// System.out.println();
		// }
		//
		// });
		// TreeEditor te = new TreeEditor(tree);
		// te.minimumHeight = 20;
		// te.minimumWidth = 1000;
		// te.setEditor(ctrl, ti, columnToEdit);
		// }
		//
		// }
	}

	/**
	 * Sets the root entry for this property sheet viewer. The root entry is not
	 * visible in the viewer.
	 * 
	 * @param root
	 *            the root entry
	 */
	public void setRootEntry(IPropertySheetEntry root) {
		// If we have a root entry, remove our entry listener
		if (rootEntry != null) {
			rootEntry.removePropertySheetEntryListener(entryListener);
		}

		rootEntry = root;

		// Set the root as user data on the tree
		// tree.setData(rootEntry);

		// Add an IPropertySheetEntryListener to listen for entry change
		// notifications
		rootEntry.addPropertySheetEntryListener(entryListener);

		// Pass our input to the root, this will trigger entry change
		// callbacks to update this viewer
		setInput(input);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.Viewer#setSelection(org.eclipse.jface.viewers
	 * .ISelection, boolean)
	 */
	public void setSelection(ISelection selection, boolean reveal) {
		// Do nothing by default
	}

	/**
	 * Sets the sorter for this viewer.
	 * <p>
	 * The default sorter sorts categories and entries alphabetically. A viewer
	 * update needs to be triggered after the sorter has changed.
	 * </p>
	 * 
	 * @param sorter
	 *            the sorter to set (<code>null</code> will reset to the default
	 *            sorter)
	 * @since 3.1
	 */
	public void setSorter(PropertySheetSorter sorter) {
		if (null == sorter) {
			sorter = new PropertySheetSorter();
		}
		this.sorter = sorter;
	}

	/**
	 * Update the child entries or categories of the given entry or category. If
	 * the given node is the root entry and we are showing categories then the
	 * child entries are categories, otherwise they are entries.
	 * 
	 * @param node
	 *            the entry or category whose children we will update
	 * @param widget
	 *            the widget for the given entry, either a
	 *            <code>TableTree</code> if the node is the root node or a
	 *            <code>TreeItem</code> otherwise.
	 */
	private void updateChildrenOf(Object node, PropertyViewer widget) {

		// get the current child tree items
		PropertyItem[] childItems = pv2.getItems();

		List<IPropertySheetEntry> children = getChildren(node);

		// remove items
		Set<IPropertySheetEntry> set = new HashSet<IPropertySheetEntry>(
				childItems.length * 2 + 1);

		for (int i = 0; i < childItems.length; i++) {
			IPropertySheetEntry data = childItems[i].getData();
			if (data != null) {
				IPropertySheetEntry e = data;
				int ix = children.indexOf(e);
				if (ix < 0) { // not found
					removeItem(childItems[i]);
				} else { // found
					set.add(e);
				}
			} else if (data == null) { // the dummy
				removeItem(childItems[i]);
			}
		}

		// WORKAROUND
		int oldCnt = pv2.getItemCount();

		// add new items
		int newSize = children.size();
		for (int i = 0; i < newSize; i++) {
			Object el = children.get(i);
			if (!set.contains(el)) {
				createItem((IPropertySheetEntry) el, i);
			}
		}

		// // WORKAROUND
		// if (oldCnt == 0 && pv2.getItemCount() == 1) {
		//
		// }
		pv2.setRedraw(false);
		pv2.setRedraw(true);

		// get the child tree items after our changes
		childItems = pv2.getItems();

		// update the child items
		// This ensures that the children are in the correct order
		// are showing the correct values.
		// for (Control ctrl : pv2.getChildren())
		// ctrl.dispose();
		// for (int i = 0; i < newSize; i++) {
		// Object el = children.get(i);
		// if (el instanceof IPropertySheetEntry) {
		// IPropertySheetEntry pse = (IPropertySheetEntry) el;
		// updateEntry(pse, childItems[i]);
		// try {
		// Control ce = pse.getEditor(pv2).getControl();
		// ce.setSize(60, 20);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// }
		// }
		// The tree's original selection may no longer apply after the update,
		// so fire the selection changed event.
		entrySelectionChanged();
	}

	/**
	 * Update the given entry (but not its children or parent)
	 * 
	 * @param entry
	 *            the entry we will update
	 * @param item
	 *            the tree item for the given entry
	 */
	private void updateEntry(IPropertySheetEntry entry, PropertyItem item) {
		// ensure that backpointer is correct
		item.setData(entry);

		// update the map accordingly
		entryToItemMap.put(entry, item);

		// update the name and value columns
		// item.setText(0, entry.getDisplayName());
		// // item.setText(1, entry.getValueAsString());
		// Image image = entry.getImage();
		// if (item.getImage(1) != image) {
		// item.setImage(1, image);
		// }

		// update the "+" icon
		// updatePlus(entry, item);
	}
}
