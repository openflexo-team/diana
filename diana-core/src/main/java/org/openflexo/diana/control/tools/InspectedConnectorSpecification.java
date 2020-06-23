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

package org.openflexo.diana.control.tools;

import java.util.List;

import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.connectors.ConnectorSpecification;
import org.openflexo.diana.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.diana.control.DianaInteractiveViewer;
import org.openflexo.pamela.undo.CompoundEdit;

/**
 * Implementation of {@link ConnectorSpecification}, as a mutable container over {@link ConnectorSpecification} class hierarchy.<br>
 * It presents graphical properties synchronized with and reflecting a selection<br>
 * This is the object beeing represented in tool inspectors
 * 
 * @author sylvain
 * 
 */
public class InspectedConnectorSpecification extends
		InspectedStyleUsingFactory<ConnectorSpecificationFactory, ConnectorSpecification, ConnectorType> {

	public InspectedConnectorSpecification(DianaInteractiveViewer<?, ?, ?> controller) {
		super(controller, new ConnectorSpecificationFactory(controller));
	}

	@Override
	public List<ConnectorNode<?>> getSelection() {
		return getController().getSelectedConnectors();
	}

	@Override
	public ConnectorSpecification getStyle(DrawingTreeNode<?, ?> node) {
		if (node instanceof ConnectorNode) {
			return ((ConnectorNode<?>) node).getConnectorSpecification();
		}
		return null;
	}

	@Override
	protected ConnectorType getStyleType(ConnectorSpecification style) {
		if (style != null) {
			return style.getConnectorType();
		}
		return null;
	}

	@Override
	protected void applyNewStyleTypeToSelection(ConnectorType newStyleType) {
		for (DrawingTreeNode<?, ?> n : getSelection()) {
			ConnectorSpecification nodeStyle = getStyle(n);
			if (getStyleType(nodeStyle) != newStyleType) {
				applyNewStyle(newStyleType, n);
			}
		}
	}

	@Override
	protected void applyNewStyle(ConnectorType newConnectorType, DrawingTreeNode<?, ?> node) {
		ConnectorNode<?> n = (ConnectorNode<?>) node;
		ConnectorSpecification oldConnectorSpecification = n.getConnectorSpecification();
		CompoundEdit setValueEdit = startRecordEdit("Set ConnectorType to " + newConnectorType);
		ConnectorSpecification newConnectorSpecification = getStyleFactory().makeNewStyle(oldConnectorSpecification);
		n.setConnectorSpecification(newConnectorSpecification);
		// n.getPropertyChangeSupport().firePropertyChange(ShapeGraphicalRepresentation.BACKGROUND_STYLE_TYPE_KEY,
		// oldShapeSpecification.getShapeType(), newShapeType);
		stopRecordEdit(setValueEdit);
	}
}
