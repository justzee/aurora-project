/*******************************************************************************
 * Copyright (c) 2003, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package aurora.ide.meta.gef.editors.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.Label;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

/**
 * @author hudsonr Created on Jul 16, 2003
 */
public class AuroraPartFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;
		if (model instanceof ViewDiagram)
			part = new ViewDiagramPart();
		else if (model instanceof Label)
			part = new LabelPart();
		else if (model instanceof Form) {
			part = new BoxPart();
		} else if (model instanceof Input) {
			part = new InputPart();
		} else if (model instanceof Grid) {
			part = new GridPart();
		} else {
			part = new BoxPart();

		}

		// else if (model instanceof SequentialActivity)
		// part = new SequentialActivityPart();
		// else if (model instanceof Activity)
		// part = new SimpleActivityPart();
		// else if (model instanceof Transition)
		// part = new TransitionPart();
		part.setModel(model);
		return part;
	}

}
