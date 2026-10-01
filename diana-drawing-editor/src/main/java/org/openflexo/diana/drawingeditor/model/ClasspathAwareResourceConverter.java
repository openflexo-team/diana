/**
 * 
 * Copyright (c) 2026, Openflexo
 * 
 * This file is part of Diana-drawing-editor, a component of the software infrastructure 
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

package org.openflexo.diana.drawingeditor.model;

import org.openflexo.pamela.converter.RelativePathResourceConverter;
import org.openflexo.pamela.factory.PamelaModelFactory;
import org.openflexo.rm.ClasspathResourceLocatorImpl;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

/**
 * A {@link RelativePathResourceConverter} which also handles resources of the classpath (such as the images of palette elements, shipped
 * in jars): they are stored by their classpath path, prefixed with {@value #CLASSPATH_PREFIX}, since no path relative to the diagram file
 * reaches them
 * 
 * @author sylvain
 */
public class ClasspathAwareResourceConverter extends RelativePathResourceConverter {

	public static final String CLASSPATH_PREFIX = "classpath:";

	public ClasspathAwareResourceConverter(Resource containerResource) {
		super(containerResource);
	}

	@Override
	public Resource convertFromString(String value, PamelaModelFactory factory) {
		if (value != null && value.startsWith(CLASSPATH_PREFIX)) {
			return ResourceLocator.locateResource(value.substring(CLASSPATH_PREFIX.length()));
		}
		return super.convertFromString(value, factory);
	}

	@Override
	public String convertToString(Resource value) {
		if (value != null && value.getLocator() instanceof ClasspathResourceLocatorImpl) {
			String classpathPath = value.getRelativePath();
			// Only when this path reaches the same resource (a resource of a classpath directory may hold an absolute path)
			Resource located = ResourceLocator.locateResource(classpathPath);
			if (located != null && located.getURI().equals(value.getURI())) {
				return CLASSPATH_PREFIX + classpathPath;
			}
		}
		return super.convertToString(value);
	}
}
