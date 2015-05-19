/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-geomedit, a component of the software infrastructure 
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

package org.openflexo.fge.geomedit;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBTabPanel;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.Cloner;
import org.openflexo.xmlcode.DuplicateSerializationIdentifierException;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;

public class FIBInspector extends FIBPanel {

	private boolean superInspectorWereAppened = false;

	protected void appendSuperInspectors(FIBInspectorController controller) {
		if (getDataType() == null) {
			return;
		}
		if (getDataType() instanceof Class) {
			FIBInspector superInspector = controller.inspectorForClass(((Class) getDataType()).getSuperclass());
			if (superInspector != null) {
				superInspector.appendSuperInspectors(controller);
				appendSuperInspector(superInspector);
			}
		}
	}

	@Override
	public String toString() {
		return "Inspector[" + getDataType() + "]";
	}

	protected void appendSuperInspector(FIBInspector superInspector) {
		if (!superInspectorWereAppened) {
			// System.out.println("Append "+superInspector+" to "+this);
			/*try {
				System.out.println("Clone container:\n"+XMLCoder.encodeObjectWithMapping(superInspector, FIBLibrary.getFIBMapping(),StringEncoder.getDefaultInstance()));
				System.out.println("Found this:\n"+XMLCoder.encodeObjectWithMapping((XMLSerializable)Cloner.cloneObjectWithMapping(superInspector, FIBLibrary.getFIBMapping()), FIBLibrary.getFIBMapping(),StringEncoder.getDefaultInstance()));
			} catch (InvalidObjectSpecificationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AccessorInvocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DuplicateSerializationIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			append((FIBPanel) Cloner.cloneObjectWithMapping(superInspector, FIBLibrary.getFIBMapping()));
			superInspectorWereAppened = true;
		}
	}

	public FIBTabPanel getTabPanel() {
		return (FIBTabPanel) getSubComponents().firstElement();
	}

	public String getXMLRepresentation() {
		try {
			return XMLCoder.encodeObjectWithMapping(this, FIBLibrary.getFIBMapping(), StringEncoder.getDefaultInstance());
		} catch (InvalidObjectSpecificationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccessorInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DuplicateSerializationIdentifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Error ???";
	}

}
