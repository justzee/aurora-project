package aurora.search.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import aurora.search.core.AuroraSearchQuery;

public class SearchPage extends DialogPage implements ISearchPage {

	public static final String ID = "aurora.search.SearchPage";
	private Button[] fIncludeMasks;
	private Button[] fSearchFor;
	private Combo fPattern;
	private Button fCaseSensitive;
	private ISearchPageContainer fContainer;

	public SearchPage() {

	}

	public SearchPage(String title) {
		super(title);

	}

	public SearchPage(String title, ImageDescriptor image) {
		super(title, image);
	}

	public void createControl(Composite parent) {

		initializeDialogUnits(parent);

		Composite result = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout(2, true);
		layout.horizontalSpacing = 10;
		result.setLayout(layout);

		Control expressionComposite = createExpression(result);
		expressionComposite.setLayoutData(new GridData(GridData.FILL,
				GridData.CENTER, true, false, 2, 1));

		Label separator = new Label(result, SWT.NONE);
		separator.setVisible(false);
		GridData data = new GridData(GridData.FILL, GridData.FILL, false,
				false, 2, 1);
		data.heightHint = convertHeightInCharsToPixels(1) / 3;
		separator.setLayoutData(data);

		Control searchFor = createSearchFor(result);
		searchFor.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false, 2, 1));

		Control includeMask = createIncludeMask(result);
		includeMask.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false, 2, 1));

		// createParticipants(result);

		setControl(result);

		Dialog.applyDialogFont(result);
		// PlatformUI.getWorkbench().getHelpSystem()
		// .setHelp(result, IJavaHelpContextIds.JAVA_SEARCH_PAGE);
	}

	private Control createIncludeMask(Composite parent) {
		Group result = new Group(parent, SWT.NONE);
		result.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2,
				1));
		result.setText("search in");
		result.setLayout(new GridLayout(5, false));
		fIncludeMasks = new Button[] {

		createButton(result, SWT.CHECK, "screen", 1, true),
				createButton(result, SWT.CHECK, "bm", 2, true),
				createButton(result, SWT.CHECK, "config", 3, false),
				createButton(result, SWT.CHECK, "svc", 4, false),
				createButton(result, SWT.CHECK, "xml", 4, false) };

		return result;
	}

	private Control createSearchFor(Composite parent) {
		Group result = new Group(parent, SWT.NONE);
		result.setText("search for");
		result.setLayout(new GridLayout(5, true));

		fSearchFor = new Button[] {
				createButton(result, SWT.RADIO, "all", 1, true),
				createButton(result, SWT.RADIO, "javascript", 2, false),
				createButton(result, SWT.RADIO, "sql", 3, false),
				createButton(result, SWT.RADIO, "attribute", 4, false),
				createButton(result, SWT.RADIO, "attribute value", 5, false),
				createButton(result, SWT.RADIO, "tagname", 6, false),
				createButton(result, SWT.RADIO, "namespace", 7, false),
				createButton(result, SWT.RADIO, "cdata", 8, false) };

		// Fill with dummy radio buttons
		Label filler = new Label(result, SWT.NONE);
		filler.setVisible(false);
		filler.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));

		return result;
	}

	private Button createButton(Composite parent, int style, String text,
			int data, boolean isSelected) {
		Button button = new Button(parent, style);
		button.setText(text);
		button.setData(new Integer(data));
		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		button.setSelection(isSelected);
		return button;
	}

	private Control createExpression(Composite parent) {
		Composite result = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		result.setLayout(layout);
 
		// Pattern text + info
		Label label = new Label(result, SWT.LEFT);
		label.setText("Search String(* = any string,? = any character)");
		label.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false,
				false, 2, 1));
		
		fPattern = new Combo(result, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData(GridData.FILL, GridData.FILL, true, false,
				1, 1);
		data.widthHint = convertWidthInCharsToPixels(50);
		
		
		fPattern.setLayoutData(data);

		// Ignore case checkbox
		fCaseSensitive = new Button(result, SWT.CHECK);
		fCaseSensitive.setText("Case sensitive");

		fCaseSensitive.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				false, false, 1, 1));

		return result;
	}

	public boolean performAction() {
		return performNewSearch();
	}

	private boolean performNewSearch() {
		AuroraSearchQuery textSearchJob= new AuroraSearchQuery();
		NewSearchUI.runQueryInBackground(textSearchJob);
		return true;
	}

	public void setContainer(ISearchPageContainer container) {
		fContainer= container;

	}

}
