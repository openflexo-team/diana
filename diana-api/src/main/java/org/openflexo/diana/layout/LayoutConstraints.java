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

import org.openflexo.diana.DianaObject;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;

/**
 * Per-child layout constraints carried by a {@link org.openflexo.diana.ShapeGraphicalRepresentation} for the
 * {@link org.openflexo.diana.DianaLayoutManager} it opts into.<br>
 *
 * This is the polymorphic, manager-specific data object (the analogue of {@link java.awt.GridBagConstraints} /
 * {@link java.awt.BorderLayout} region constraint): the shape holds a <b>single</b> {@code layoutConstraints} slot whose concrete subtype
 * matches the manager — {@link BoxLayoutConstraints}, {@link BorderLayoutConstraints} or {@link GridBagLayoutConstraints}. This keeps each
 * manager's constraint data encapsulated in its own class instead of being spread as flat properties over every
 * {@code ShapeGraphicalRepresentation}.
 *
 * @author sylvain
 *
 */
@ModelEntity(isAbstract = true)
@Imports({ @Import(BoxLayoutConstraints.class), @Import(BorderLayoutConstraints.class), @Import(GridBagLayoutConstraints.class) })
public interface LayoutConstraints extends DianaObject {

}
