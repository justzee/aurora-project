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
package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.models.Button;

/**
 * A customized Label for SimpleActivities. Primary selection is denoted by
 * highlight and focus rectangle. Normal selection is denoted by highlight only.
 * 
 * @author Daniel Lee
 */
public class ButtonFigure extends Figure {

	private boolean selected;
	private boolean hasFocus;
	static Image bgImg = ImagesUtils.getImage("btn.gif");
	private Button model = null;
	private String type;

	public ButtonFigure() {
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
	}

	@Override
	public void handleFocusGained(FocusEvent event) {
		super.handleFocusGained(event);
	}

	protected void paintFigure(Graphics g) {
		super.paintFigure(g);
		Rectangle rect = getBounds();
		Dimension dim = model.getSize();
		g.drawImage(bgImg, 0, 0, 3, 2, rect.x, rect.y, 3, 2);// tl
		g.drawImage(bgImg, 0, 6, 1, 2, rect.x + 3, rect.y, dim.width - 6, 2);// tc
		g.drawImage(bgImg, 3, 0, 3, 2, rect.x + dim.width - 3, rect.y, 3, 2);// tr
		g.drawImage(bgImg, 0, 24, 3, 1, rect.x, rect.y + 2, 3, dim.height - 4);// ml
		g.drawImage(bgImg, 3, 24, 3, 1, rect.x + dim.width - 3, rect.y + 2, 3,
				dim.height - 4);// mr
		g.drawImage(bgImg, 0, 1096, 1, dim.height - 4, rect.x + 3, rect.y + 2,
				dim.width - 6, dim.height - 4);// mc
		g.drawImage(bgImg, 0, 4, 3, 2, rect.x, rect.y + dim.height - 2, 3, 2);// bl
		g.drawImage(bgImg, 0, 16, 1, 2, rect.x + 3, rect.y + dim.height - 2,
				dim.width - 3, 2);// bc
		g.drawImage(bgImg, 3, 4, 3, 2, rect.x + dim.width - 3, rect.y
				+ dim.height - 2, 3, 2);// br

		String prompt = model.getText();
		Dimension textExtents = FigureUtilities.getTextExtents(prompt,
				getFont());
		Image icon = model.getImage();
		if (icon == null) {
			g.drawString(prompt, rect.x + (dim.width - textExtents.width) / 2,
					rect.y + (dim.height - textExtents.height) / 2);
		} else {
			Rectangle r1 = new Rectangle(icon.getBounds());
			Rectangle r2 = new Rectangle(rect.x
					+ (dim.width - textExtents.width - 16) / 2, rect.y
					+ (dim.height - r1.height) / 2, 16, 16);
			g.drawImage(icon, r1, r2);
			g.drawString(prompt, rect.x + (dim.width - textExtents.width) / 2
					+ 8, rect.y + (dim.height - textExtents.height) / 2);
		}
	}

	/**
	 * Sets the selection state of this SimpleActivityLabel
	 * 
	 * @param b
	 *            true will cause the label to appear selected
	 */
	public void setSelected(boolean b) {
		selected = b;
		repaint();
	}

	/**
	 * Sets the focus state of this SimpleActivityLabel
	 * 
	 * @param b
	 *            true will cause a focus rectangle to be drawn around the text
	 *            of the Label
	 */
	public void setFocus(boolean b) {
		hasFocus = b;
		repaint();
	}

	public void setModel(Button model) {
		this.model = model;
	}

}
