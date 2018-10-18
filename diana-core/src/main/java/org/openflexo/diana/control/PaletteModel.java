/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.diana.control;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.DianaModelFactoryImpl;
import org.openflexo.diana.PaletteElementSpecification;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.rm.Resource;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * A {@link PaletteModel} is the abstraction of a palette associated to a drawing<br>
 * A {@link PaletteModel} is composed of {@link PaletteElement}
 * 
 * @author sylvain
 * 
 */
public abstract class PaletteModel implements HasPropertyChangeSupport {

	private static final Logger logger = Logger.getLogger(PaletteModel.class.getPackage().getName());

	protected List<PaletteElement> elements;

	private final int paletteWidth;
	private final int paletteHeight;
	private final int elementWidth;
	private final int elementHeight;
	private final int marginWidth;
	private final int marginHeight;
	private final String title;

	private boolean drawWorkingArea = false;

	private final PropertyChangeSupport pcSupport;

	/**
	 * This factory is the one used to build palettes, NOT THE ONE which is used in the related drawing editor
	 */
	public static DianaModelFactory FACTORY;

	static {
		try {
			FACTORY = new DianaModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
	}

	public PaletteModel(String title, int paletteWidth, int paletteHeight, int elementWidth, int elementHeight, int marginWidth,
			int marginHeight) {
		try {
			FACTORY = new DianaModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		pcSupport = new PropertyChangeSupport(this);
		this.paletteWidth = paletteWidth;
		this.paletteHeight = paletteHeight;
		this.elementWidth = elementWidth;
		this.elementHeight = elementHeight;
		this.marginWidth = marginWidth;
		this.marginHeight = marginHeight;
		this.title = title;
		elements = new ArrayList<>();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Build palette " + title + " " + Integer.toHexString(hashCode()) + " of " + getClass().getName());
		}
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

	public void delete() {
		for (PaletteElement element : elements) {
			element.delete();
		}
		elements = null;
	}

	public String getTitle() {
		return title;
	}

	public int getPaletteWidth() {
		return paletteWidth;
	}

	public int getPaletteHeight() {
		return paletteHeight;
	}

	public int getElementWidth() {
		return elementWidth;
	}

	public int getElementHeight() {
		return elementHeight;
	}

	public int getMarginWidth() {
		return marginWidth;
	}

	public int getMarginHeight() {
		return marginHeight;
	}

	public List<? extends PaletteElement> getElements() {
		return elements;
	}

	public void addElement(PaletteElement element) {
		if (element != null) {
			elements.add(element);
			ShapeGraphicalRepresentation gr = element.getGraphicalRepresentation();
			if (gr != null) {
				// Try to perform some checks and initialization of
				// expecting behaviour for a PaletteElement
				/*element.getGraphicalRepresentation().setIsFocusable(false);
				element.getGraphicalRepresentation().setIsSelectable(false);
				element.getGraphicalRepresentation().setIsReadOnly(true);
				element.getGraphicalRepresentation().setLocationConstraints(LocationConstraints.UNMOVABLE);*/
				// element.getGraphicalRepresentation().addToMouseDragControls(mouseDragControl)
			}
			else {
				logger.warning("Adding an element with empty GR ! in palette:  " + this.getTitle());
			}
			pcSupport.firePropertyChange("elements", null, element);
		}
		else {

			logger.warning("Adding a null element in palette:  " + this.getTitle());
		}
	}

	public void removeElement(PaletteElement element) {
		elements.remove(element);
		pcSupport.firePropertyChange("elements", element, null);
	}

	public boolean getDrawWorkingArea() {
		return drawWorkingArea;
	}

	public void setDrawWorkingArea(boolean drawWorkingArea) {
		this.drawWorkingArea = drawWorkingArea;
	}

	protected void readFromDirectory(Resource directory) {

		Vector<PaletteElementSpecification> paletteElementSpecifications = new Vector<>();

		for (Resource paletteElementResource : directory.getContents()) {
			try {
				PaletteElementSpecification paletteElementSpecification = (PaletteElementSpecification) FACTORY
						.deserialize(paletteElementResource.openInputStream());
				paletteElementSpecifications.add(paletteElementSpecification);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Collections.sort(paletteElementSpecifications, new Comparator<PaletteElementSpecification>() {
			@Override
			public int compare(PaletteElementSpecification o1, PaletteElementSpecification o2) {
				return o1.getIndex() - o2.getIndex();
			}
		});

		for (PaletteElementSpecification paletteElementSpecification : paletteElementSpecifications) {
			addElement(makePaletteElement(paletteElementSpecification));
		}

	}

	protected PaletteElement makePaletteElement(PaletteElementSpecification paletteElement) {
		int index = elements.size();

		int colSize = (getPaletteWidth() - getMarginWidth()) / (getElementWidth() + getMarginWidth());

		int rawIndex = index / colSize;
		int colIndex = index % colSize;

		ShapeGraphicalRepresentation gr = paletteElement.getGraphicalRepresentation();
		gr.setWidth(getElementWidth());
		gr.setHeight(getElementHeight());
		gr.setX(colIndex * (getElementWidth() + getMarginWidth()) + getMarginWidth());
		gr.setY(rawIndex * (getElementHeight() + getMarginHeight()) + getMarginHeight());

		return buildPaletteElement(paletteElement);
	}

	protected abstract PaletteElement buildPaletteElement(PaletteElementSpecification paletteElement);

}
