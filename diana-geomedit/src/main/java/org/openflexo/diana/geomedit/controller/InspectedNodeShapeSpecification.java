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

package org.openflexo.diana.geomedit.controller;

import java.util.List;

import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.GeometricNode;
import org.openflexo.diana.control.tools.InspectedShapeSpecification;
import org.openflexo.diana.geomedit.GeomEditDrawingController;
import org.openflexo.diana.geomedit.model.NodeConstruction;
import org.openflexo.diana.shapes.ShapeSpecification;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.pamela.undo.CompoundEdit;

/**
 * Specialize InspectedShapeSpecification by managing ShapeSpecification of {@link NodeConstruction} in GeomEdit
 * 
 * @author sylvain
 * 
 */
public class InspectedNodeShapeSpecification extends InspectedShapeSpecification {

	public InspectedNodeShapeSpecification(GeomEditDrawingController controller) {
		super(controller);
	}

	@Override
	public GeomEditDrawingController getController() {
		return (GeomEditDrawingController) super.getController();
	}

	@Override
	public List<GeometricNode<?>> getSelection() {
		return getController().getSelectedNodes();
	}

	@Override
	public ShapeSpecification getStyle(DrawingTreeNode<?, ?> node) {
		if (node instanceof GeometricNode && node.getDrawable() instanceof NodeConstruction) {
			return ((NodeConstruction) node.getDrawable()).getShapeSpecification();
		}
		return null;
	}

	/*@Override
	protected ShapeType getStyleType(ShapeSpecification style) {
		if (style != null) {
			return style.getShapeType();
		}
		return null;
	}*/

	@Override
	protected void applyNewStyle(ShapeType newShapeType, DrawingTreeNode<?, ?> node) {
		GeometricNode<?> n = (GeometricNode<?>) node;
		NodeConstruction construction = (NodeConstruction) node.getDrawable();
		ShapeSpecification oldShapeSpecification = construction.getShapeSpecification();
		CompoundEdit setValueEdit = startRecordEdit("Set ShapeType to " + newShapeType);
		ShapeSpecification newShapeSpecification = getStyleFactory().makeNewStyle(oldShapeSpecification);
		construction.setShapeSpecification(newShapeSpecification);
		// n.getPropertyChangeSupport().firePropertyChange(ShapeGraphicalRepresentation.BACKGROUND_STYLE_TYPE_KEY,
		// oldShapeSpecification.getShapeType(), newShapeType);
		stopRecordEdit(setValueEdit);
	}

	@Override
	public void fireSelectionUpdated() {
		super.fireSelectionUpdated();

		// Tricky code here: we detect the selection change to notify the factory of the potential change of "isSingleSelection" property
		getStyleFactory().getPropertyChangeSupport().firePropertyChange("isSingleSelection", !getStyleFactory().isSingleSelection(),
				getStyleFactory().isSingleSelection());
	}

}
