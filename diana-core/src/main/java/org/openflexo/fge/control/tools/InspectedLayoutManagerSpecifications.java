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

package org.openflexo.fge.control.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openflexo.fge.ContainerGraphicalRepresentation;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGELayoutManagerSpecification;
import org.openflexo.fge.GRProperty;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.layout.GridLayoutManagerSpecification;

/**
 * Implementation of {@link LayoutManagerSpecification}, as a container multiple {@link LayoutManagerSpecification<br>
 * This is the object beeing represented in tool inspectors
 * 
 * @author sylvain
 * 
 */
public class InspectedLayoutManagerSpecifications extends InspectedStyle<ContainerGraphicalRepresentation> {

	public InspectedLayoutManagerSpecifications(DianaInteractiveViewer<?, ?, ?> controller) {
		super(controller, null);
	}

	@Override
	public List<DrawingTreeNode<?, ?>> getSelection() {
		return getController().getSelectedObjects();
	}

	@Override
	public ContainerGraphicalRepresentation getStyle(DrawingTreeNode<?, ?> node) {
		if (node instanceof ContainerNode) {
			return ((ContainerNode<?, ?>) node).getGraphicalRepresentation();
		}
		return null;
	}

	@Override
	protected void fireChangedProperties() {
		// We replace here super code, because we have to fire changed properties for all properties
		// as the union of properties of all possible types
		List<GRProperty<?>> paramsList = new ArrayList<GRProperty<?>>();
		paramsList.addAll(GRProperty.getGRParameters(DrawingGraphicalRepresentation.class));
		paramsList.addAll(GRProperty.getGRParameters(ShapeGraphicalRepresentation.class));
		Set<GRProperty<?>> allParams = new HashSet<GRProperty<?>>(paramsList);
		for (GRProperty<?> p : allParams) {
			fireChangedProperty(p);
		}
	}

	@Override
	public void fireSelectionUpdated() {
		super.fireSelectionUpdated();
		/*getPropertyChangeSupport().firePropertyChange("areLocationPropertiesApplicable", !areLocationPropertiesApplicable(),
				areLocationPropertiesApplicable());*/
	}

	public List<FGELayoutManagerSpecification<?>> getLayoutManagerSpecifications() {
		return getPropertyValue(ContainerGraphicalRepresentation.LAYOUT_MANAGER_SPECIFICATIONS);
	}

	private FGELayoutManagerSpecification<?> defaultLayoutManagerSpecification;

	public FGELayoutManagerSpecification<?> getDefaultLayoutManagerSpecification() {
		System.out.println("getDefaultLayoutManagerSpecification???");

		if (getLayoutManagerSpecifications().size() > 0) {
			System.out.println(">> return " + defaultLayoutManagerSpecification);
			return getLayoutManagerSpecifications().get(0);
		}
		if (defaultLayoutManagerSpecification == null) {
			defaultLayoutManagerSpecification = getFactory().makeLayoutManagerSpecification("prout", GridLayoutManagerSpecification.class);
		}
		System.out.println("> return " + defaultLayoutManagerSpecification);
		return defaultLayoutManagerSpecification;
	}
}
