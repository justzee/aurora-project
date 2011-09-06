package aurora.ide.refactoring.ui;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import aurora.ide.refactor.screen.ScreenCustomerRefactoring;

public class AuroraRefactoringWizard extends RefactoringWizard {

	public AuroraRefactoringWizard(Refactoring refactoring, int flags) {
		super(refactoring, flags);
	}

	public AuroraRefactoringWizard(ScreenCustomerRefactoring refactoring) {
		this(refactoring, WIZARD_BASED_USER_INTERFACE);
	}

	@Override
	protected void addUserInputPages() {
		
	}

}
