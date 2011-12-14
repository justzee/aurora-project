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
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;

import aurora.ide.meta.gef.editors.models.BOX;

/**
 * A customized Label for SimpleActivities. Primary selection is denoted by
 * highlight and focus rectangle. Normal selection is denoted by highlight only.
 * 
 * @author Daniel Lee
 */
public class ToolbarFigure extends Figure {

	private boolean selected;
	private boolean hasFocus;
	private int labelWidth;
	private String prompt = "prompt : ";

	private Label label = new Label();
	// private Text text = new Text();

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		if (BOX.HBOX.equals(type)) {
			this.setBorder(new VirtualBoxBorder("H"));
		}
		if (BOX.VBOX.equals(type)) {
			this.setBorder(new VirtualBoxBorder("V"));
		}
	}

	private Figure titleBar = new Label("敬请期待。。。。");
	private Figure bodyArea = new Figure();

	public ToolbarFigure() {
		// GridLayout gridLayout = new GridLayout();
		// gridLayout.numColumns = 1;
		// gridLayout.horizontalSpacing = 10;
		// gridLayout.marginHeight = 10;
		// gridLayout.marginWidth = 10;
		// gridLayout.verticalSpacing = 10;
		// this.setLayoutManager(gridLayout);
		// ViewDiagramLayout ly = new ViewDiagramLayout(false);
		this.setLayoutManager(new DummyLayout());
		// TitleBarBorder border2 = new TitleBarBorder("title");
		//
		// this.setBorder(border2);
		// this.setBorder(new FrameBorder("a"));
		// this.setBorder(new GroupBoxBorder("xx"));
		this.setBorder(new TitleBorder("大家好 ： 敬请期待。。。"));
		// titleBar.setBorder(new TitleBarBorder());

		// gridLayout = new GridLayout();
		// gridLayout.numColumns = 3;
		// gridLayout.horizontalSpacing = 10;
		// gridLayout.marginHeight = 10;
		// gridLayout.marginWidth = 10;
		// gridLayout.verticalSpacing = 10;
		// bodyArea.setLayoutManager(gridLayout);
		//
		// this.add(bodyArea);
		//
		//
		// titleBar.setSize(150, 35);
		// bodyArea.setSize(150,75);
		// this.setSize(160, 120);
		// this.setPreferredSize(260, 120);

		// TitleBarBorder
	}

	public int getLabelWidth() {
		return labelWidth;
	}

	public void setLabelWidth(int labelWidth) {
		this.labelWidth = labelWidth;
	}

	@Override
	public void handleFocusGained(FocusEvent event) {
		// TODO Auto-generated method stub
		super.handleFocusGained(event);
		this.getBounds();

	}

	/**
	 * @see org.eclipse.draw2d.Label#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure(Graphics graphics) {
		// if (selected) {
		// graphics.pushState();
		// graphics.setBackgroundColor(ColorConstants.menuBackgroundSelected);
		// graphics.fillRectangle(getSelectionRectangle());
		// graphics.popState();
		// graphics.setForegroundColor(ColorConstants.white);
		// }
		// if (hasFocus) {
		// graphics.pushState();
		// graphics.setXORMode(true);
		// graphics.setForegroundColor(ColorConstants.menuBackgroundSelected);
		// graphics.setBackgroundColor(ColorConstants.white);
		// graphics.drawFocus(getSelectionRectangle().resize(-1, -1));
		// graphics.popState();
		// }
		super.paintFigure(graphics);
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
