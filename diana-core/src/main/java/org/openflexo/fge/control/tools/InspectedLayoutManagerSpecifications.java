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
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGELayoutManager;
import org.openflexo.fge.FGELayoutManagerSpecification;
import org.openflexo.fge.FGELayoutManagerSpecification.LayoutManagerSpecificationType;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.GRProperty;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.toolbox.StringUtils;

/**
 * Proxy object manager by the controller of {@link LayoutManagerSpecification} inspector (this is the object represented by the inspector)<br>
 * 
 * Only one instance of {@link ContainerNode} might be accessed through this class (behaviour is here different from the other inspectors
 * managing a multiple selection)
 * 
 * @author sylvain
 * 
 */
public class InspectedLayoutManagerSpecifications extends InspectedStyle<ContainerGraphicalRepresentation> {

	public InspectedLayoutManagerSpecifications(DianaInteractiveViewer<?, ?, ?> controller) {
		super(controller, null);
	}

	@Override
	public List<ContainerNode<?, ?>> getSelection() {
		return getController().getSelectedContainers();
	}

	public ContainerNode<?, ?> getContainerNode() {
		if (getSelection().size() > 0) {
			return getSelection().get(0);
		}
		return null;
	}

	public boolean hasValidSelection() {
		return getSelection().size() == 1;
	}

	@Override
	public FGEModelFactory getFactory() {
		if (getContainerNode() != null) {
			return getContainerNode().getFactory();
		}
		return null;
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
		getPropertyChangeSupport().firePropertyChange("hasValidSelection", !hasValidSelection(), hasValidSelection());
	}

	public List<FGELayoutManager<?, ?>> getLayoutManagers() {
		// return getPropertyValue(ContainerGraphicalRepresentation.LAYOUT_MANAGER_SPECIFICATIONS);
		if (getContainerNode() != null) {
			return (List) getContainerNode().getLayoutManagers();
		}
		return null;
	}

	public LayoutManagerSpecificationType getDefaultLayoutType() {

		/*if (getContainerNode() != null) {
			for (ShapeNode<?> n : getContainerNode().getShapeNodes()) {
				System.out.println("> Node " + n + " lm=" + n.getGraphicalRepresentation().getLayoutManagerIdentifier());
			}
		}*/

		if (getDefaultLayoutManager() != null) {
			return getDefaultLayoutManager().getLayoutManagerSpecification().getLayoutManagerSpecificationType();
		}
		return LayoutManagerSpecificationType.NONE;
	}

	public void setDefaultLayoutType(LayoutManagerSpecificationType defaultLayoutType) {

		if (getDefaultLayoutType() != defaultLayoutType && hasValidSelection() && getContainerNode() != null && getFactory() != null) {

			FGELayoutManager<?, ?> oldLayoutManager = getDefaultLayoutManager();

			System.out.println("setDefaultLayoutType with " + defaultLayoutType);
			LayoutManagerSpecificationType oldValue = getDefaultLayoutType();
			ContainerGraphicalRepresentation gr = getContainerNode().getGraphicalRepresentation();

			if (getDefaultLayoutManager() != null) {
				FGELayoutManagerSpecification<?> spec = getDefaultLayoutManager().getLayoutManagerSpecification();
				gr.removeFromLayoutManagerSpecifications(spec);
				spec.delete();
			}

			if (defaultLayoutType != LayoutManagerSpecificationType.NONE) {
				FGELayoutManagerSpecification<?> newLayoutManagerSpecification = getFactory().makeLayoutManagerSpecification(
						defaultLayoutType.getDefaultLayoutManagerName(), defaultLayoutType.getLayoutManagerSpecificationClass());
				System.out.println("new layout manager: " + newLayoutManagerSpecification);
				gr.addToLayoutManagerSpecifications(newLayoutManagerSpecification);
				for (ShapeNode<?> n : getContainerNode().getShapeNodes()) {
					// For all ShapeNode contained in ContainerNode with no layout manager, assign this newly created layout manager
					if (StringUtils.isEmpty(n.getGraphicalRepresentation().getLayoutManagerIdentifier())) {
						n.getGraphicalRepresentation().setLayoutManagerIdentifier(newLayoutManagerSpecification.getIdentifier());
					}
				}
				// FGELayoutManager<?, ?> newLayoutManager = getContainerNode()
				// .getLayoutManager(newLayoutManagerSpecification.getIdentifier());

				if (getDefaultLayoutManager() != null) {
					getDefaultLayoutManager().invalidate();
					getDefaultLayoutManager().doLayout(true);
				}
			}

			getPropertyChangeSupport().firePropertyChange("defaultLayoutManager", oldLayoutManager, getDefaultLayoutManager());
			getPropertyChangeSupport().firePropertyChange("defaultLayoutType", oldValue, defaultLayoutType);
		}
	}

	/**
	 * Return default {@link FGELayoutManager} to display in the context of this inspector<br>
	 * 
	 * If selection is a container with some layout defined, return first one.<br>
	 * 
	 * @return
	 */
	public FGELayoutManager<?, ?> getDefaultLayoutManager() {
		/*if (hasValidSelection() && getContainerNode() instanceof ShapeNode && getContainerNode().getChildNodes().isEmpty()) {
			return ((ShapeNode<?>) getContainerNode()).getLayoutManager();
		}*/
		if (getLayoutManagers() != null && getLayoutManagers().size() > 0) {
			return getLayoutManagers().get(0);
		}
		return null;
	}

}
