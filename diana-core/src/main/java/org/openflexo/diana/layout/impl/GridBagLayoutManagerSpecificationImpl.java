/**
 *
 * Copyright (c) 2024, Openflexo
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

package org.openflexo.diana.layout.impl;

import org.openflexo.diana.impl.DianaLayoutManagerSpecificationImpl;
import org.openflexo.diana.layout.GridBagLayoutManager;
import org.openflexo.diana.layout.GridBagLayoutManagerSpecification;

/**
 * Default implementation for the specification of a {@link GridBagLayoutManager} in DIANA.<br>
 *
 * @author sylvain
 *
 */
public abstract class GridBagLayoutManagerSpecificationImpl extends DianaLayoutManagerSpecificationImpl<GridBagLayoutManager<?>>
		implements GridBagLayoutManagerSpecification {

	@Override
	public LayoutManagerSpecificationType getLayoutManagerSpecificationType() {
		return LayoutManagerSpecificationType.GRIDBAG;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class<GridBagLayoutManager<?>> getLayoutManagerClass() {
		return (Class) GridBagLayoutManager.class;
	}

	/**
	 * A grid-bag layout is deterministic and re-lays its nodes on demand: it supports autolayout.
	 */
	@Override
	public boolean supportAutolayout() {
		return true;
	}

	/**
	 * A grid-bag layout paints no decoration (no grid lines, no guides).
	 */
	@Override
	public boolean supportDecoration() {
		return false;
	}

	/**
	 * Grid-bag-layouted nodes are placed by the manager; the layout must be recomputed whenever the container is resized (or a node
	 * added/removed). {@link DraggingMode#ContinuousLayout} enables that re-layout. Making the children themselves non-interactive (so the
	 * user resizes the container, not a cell) is achieved by setting them non-selectable / non-focusable, not by using {@code NoDragging} —
	 * which would also suppress the resize-driven re-layout.
	 */
	@Override
	public DraggingMode getDefaultDraggingMode() {
		return DraggingMode.ContinuousLayout;
	}
}
