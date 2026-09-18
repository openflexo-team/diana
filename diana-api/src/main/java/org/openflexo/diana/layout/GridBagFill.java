/**
 *
 * Copyright (c) 2024, Openflexo
 *
 * This file is part of Diana-api, a component of the software infrastructure
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

package org.openflexo.diana.layout;

/**
 * How a child shape fills its cell block in a {@link GridBagLayoutManager}, modelling {@link java.awt.GridBagConstraints#fill}.<br>
 *
 * Stored per child as the {@link org.openflexo.diana.ShapeGraphicalRepresentation#getLayoutFill() GR property}.
 *
 * @author sylvain
 *
 */
public enum GridBagFill {
	/** The child keeps its own size (only positioned, see {@link GridBagAnchor}). */
	NONE,
	/** The child is resized to fill the cell block width, keeping its own height. */
	HORIZONTAL,
	/** The child is resized to fill the cell block height, keeping its own width. */
	VERTICAL,
	/** The child is resized to fill the whole cell block. */
	BOTH
}
