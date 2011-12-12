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
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.models.Input;

/**
 * A customized Label for SimpleActivities. Primary selection is denoted by
 * highlight and focus rectangle. Normal selection is denoted by highlight only.
 * 
 * @author Daniel Lee
 */
public class InputField extends Figure {

	private boolean selected;
	private boolean hasFocus;
	private int labelWidth;
	private Input model = null;

	public InputField() {
		// this.add(label);
		// label.setTextAlignment(PositionConstants.CENTER);
		// this.add(text);
		// text.setTextAlignment(PositionConstants.CENTER);
		// this.setLayoutManager(new FlowLayout());
		// init();
	}

	public void setModel(Input model) {
		this.model = model;
	}

	public int getLabelWidth() {
		return labelWidth;
	}

	public void setLabelWidth(int labelWidth) {
		this.labelWidth = labelWidth;
	}

	@Override
	public void setBounds(Rectangle rect) {
		super.setBounds(rect);
	}

	@Override
	public void handleFocusGained(FocusEvent event) {
		super.handleFocusGained(event);
	}

	/**
	 * @see org.eclipse.draw2d.Label#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure(Graphics graphics) {

		super.paintFigure(graphics);
		String prompt = model.getPrompt();
		Dimension textExtents = FigureUtilities.getTextExtents(prompt,
				getFont());
		Rectangle textRectangle = new Rectangle();
		int pWidth = this.getLabelWidth() - textExtents.width;
		if (pWidth < 0) {
			prompt = prompt.substring(0, 3) + "...";
			textExtents = FigureUtilities.getTextExtents(prompt, getFont());
			pWidth = this.getLabelWidth() - textExtents.width;
		}

		textRectangle.x = pWidth + getBounds().x;
		int i = getBounds().height - textExtents.height;
		textRectangle.y = i <= 0 ? getBounds().y : getBounds().y + i / 2;

		textRectangle.setSize(textExtents);

		graphics.drawText(prompt, textRectangle.getLocation());

		Rectangle inputRectangle = new Rectangle();

		inputRectangle.x = textRectangle.getTopRight().x + 1;
		inputRectangle.y = getBounds().y + 1;
		int j = getBounds().width - getLabelWidth() - 1;
		inputRectangle.width = j <= 0 ? 0 : j;
		inputRectangle.height = getBounds().height - 1;

		// FigureUtilities.paintEtchedBorder(graphics, inputRectangle);
		graphics.setForegroundColor(ColorConstants.BORDER);
		graphics.drawRectangle(inputRectangle.getResized(-1, -1));
		Rectangle r = inputRectangle.getTranslated(1, 1).getResized(-2, -2);
		Color bgColor = ColorConstants.WHITE;
		if (model.isRequired())
			bgColor = ColorConstants.REQUIRED;
		if (model.isReadOnly())
			bgColor = ColorConstants.READONLY;
		graphics.setBackgroundColor(bgColor);
		graphics.fillRectangle(r);

		Image image = getImage();

		if (image != null) {
			Rectangle imageR = inputRectangle.getCopy();
			graphics.drawImage(image, getImageLocation().x,
					getImageLocation().y, 16, 16, imageR.getTopRight().x - 18,
					imageR.getTopRight().y, 16, 16);
		}
	}

	private Image getImage() {
		if (Input.TEXT.equals(model.getType()))
			return null;
		return ImagesUtils.getImage("itembar");
	}

	private Point getImageLocation() {
		Point p = new Point(0, 0);
		String type = model.getType();
		if (Input.Combo.equals(type)) {
			return p.setY(0);
		}
		if (Input.CAL.equals(type)) {
			return p.setY(22);
		}
		if (Input.LOV.equals(type)) {
			return p.setY(42);
		}
		return p;
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
}
