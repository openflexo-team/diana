/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.diana;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.DefaultBindable;
import org.openflexo.connie.java.JavaBindingFactory;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.GRProvider.ConnectorGRProvider;
import org.openflexo.diana.GRProvider.ContainerGRProvider;
import org.openflexo.diana.GRProvider.DrawingGRProvider;
import org.openflexo.diana.GRProvider.GeometricGRProvider;
import org.openflexo.diana.GRProvider.ShapeGRProvider;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.graph.DianaGraph;

public abstract class GRBinding<O, GR extends GraphicalRepresentation> extends DefaultBindable {

	private static final Logger LOGGER = Logger.getLogger(GRBinding.class.getPackage().getName());

	private static final BindingFactory BINDING_FACTORY = new JavaBindingFactory();

	// Unused private final String name;
	private final GRProvider<O, GR> grProvider;
	private final List<GRStructureVisitor<O>> walkers;

	private final Map<GRProperty<?>, DynamicPropertyValue<?>> dynamicPropertyValues;
	protected BindingModel bindingModel;

	private BindingFactory bindingFactory = null;

	public static class DynamicPropertyValue<T> {
		public GRProperty<T> parameter;
		public DataBinding<T> dataBinding;

		public DynamicPropertyValue(GRProperty<T> parameter, DataBinding<T> dataBinding) {
			super();
			this.parameter = parameter;
			this.dataBinding = dataBinding;
		}

		public boolean isSettable() {
			return dataBinding.isSettable();
		}
	}

	protected GRBinding(String name, Class<?> drawableClass, GRProvider<O, GR> grProvider) {
		// Unused this.name = name;
		this.grProvider = grProvider;
		walkers = new ArrayList<>();
		dynamicPropertyValues = new Hashtable<>();
		bindingModel = new BindingModel();
		BindingVariable bv = new BindingVariable("drawable", drawableClass);
		bv.setCacheable(false);
		bindingModel.addToBindingVariables(bv);
	}

	public List<GRStructureVisitor<O>> getWalkers() {
		return walkers;
	}

	public void addToWalkers(GRStructureVisitor<O> walker) {
		walkers.add(walker);
	}

	public GRProvider<O, GR> getGRProvider() {
		return grProvider;
	}

	@SuppressWarnings("unchecked")
	public <T> DynamicPropertyValue<T> getDynamicPropertyValue(GRProperty<T> parameter) {
		return (DynamicPropertyValue<T>) dynamicPropertyValues.get(parameter);
	}

	public <T> DynamicPropertyValue<T> setDynamicPropertyValue(GRProperty<T> parameter, DataBinding<T> bindingValue) {
		return setDynamicPropertyValue(parameter, bindingValue, false);
	}

	public <T> DynamicPropertyValue<T> setDynamicPropertyValue(GRProperty<T> parameter, DataBinding<T> bindingValue, boolean settable) {
		bindingValue.setOwner(this);
		bindingValue.setBindingName(parameter.getName());
		bindingValue.setDeclaredType(parameter.getType());
		bindingValue.setBindingDefinitionType(settable ? BindingDefinitionType.GET_SET : BindingDefinitionType.GET);
		DynamicPropertyValue<T> returned = new DynamicPropertyValue<>(parameter, bindingValue);
		dynamicPropertyValues.put(parameter, returned);

		if (!bindingValue.isValid()) {
			LOGGER.warning("Invalid binding " + bindingValue + " reason " + bindingValue.invalidBindingReason());
		}

		return returned;
	}

	public boolean hasDynamicPropertyValue(GRProperty<?> parameter) {
		return dynamicPropertyValues.get(parameter) != null;
	}

	public Collection<DynamicPropertyValue<?>> getDynamicPropertyValues() {
		return dynamicPropertyValues.values();
	}

	@Override
	public BindingFactory getBindingFactory() {
		if (bindingFactory != null) {
			return bindingFactory;
		}
		return BINDING_FACTORY;
	}

	public void setBindingFactory(BindingFactory bindingFactory) {
		this.bindingFactory = bindingFactory;
	}

	@Override
	public BindingModel getBindingModel() {
		return bindingModel;
	}

	@Override
	public void notifiedBindingChanged(DataBinding<?> dataBinding) {
	}

	@Override
	public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
	}

	public List<? extends ControlArea<?>> makeControlAreasFor(DrawingTreeNode<O, GR> dtn) {
		return grProvider.makeControlAreasFor(dtn);
	}

	public static abstract class ContainerGRBinding<O, GR extends ContainerGraphicalRepresentation> extends GRBinding<O, GR> {

		// private final Map<String, DianaLayoutManagerSpecification<?>> layoutManagerSpecifications;

		public ContainerGRBinding(String name, Class<?> drawableClass, ContainerGRProvider<O, GR> grProvider) {
			super(name, drawableClass, grProvider);
			// layoutManagerSpecifications = new HashMap<String, DianaLayoutManagerSpecification<?>>();
		}

		/*public DianaLayoutManagerSpecification<?> addLayoutManager(DianaLayoutManagerSpecification<?> layoutManagerSpec) {
			layoutManagerSpecifications.put(layoutManagerSpec.getIdentifier(), layoutManagerSpec);
			return layoutManagerSpec;
		}
		
		public Map<String, DianaLayoutManagerSpecification<?>> getLayoutManagerSpecifications() {
			return layoutManagerSpecifications;
		}*/

	}

	public static class DrawingGRBinding<M> extends ContainerGRBinding<M, DrawingGraphicalRepresentation> {

		public DrawingGRBinding(String name, Class<?> drawableClass, DrawingGRProvider<M> grProvider) {
			super(name, drawableClass, grProvider);
			bindingModel.addToBindingVariables(new BindingVariable("gr", DrawingGraphicalRepresentation.class));
		}

	}

	public static class ShapeGRBinding<O> extends ContainerGRBinding<O, ShapeGraphicalRepresentation> {

		public ShapeGRBinding(String name, Class<? extends O> drawableClass, ShapeGRProvider<O> grProvider) {
			super(name, drawableClass, grProvider);
			bindingModel.addToBindingVariables(new BindingVariable("gr", ShapeGraphicalRepresentation.class));
		}

	}

	public static class ConnectorGRBinding<O> extends GRBinding<O, ConnectorGraphicalRepresentation> {

		public ConnectorGRBinding(String name, Class<? extends O> drawableClass, ConnectorGRProvider<O> grProvider) {
			super(name, drawableClass, grProvider);
			bindingModel.addToBindingVariables(new BindingVariable("gr", ConnectorGraphicalRepresentation.class));
		}
	}

	public static class GeometricGRBinding<O> extends GRBinding<O, GeometricGraphicalRepresentation> {

		public GeometricGRBinding(String name, Class<? extends O> drawableClass, GeometricGRProvider<O> grProvider) {
			super(name, drawableClass, grProvider);
			bindingModel.addToBindingVariables(new BindingVariable("gr", GeometricGraphicalRepresentation.class));
		}
	}

	public static class GraphGRBinding<O extends DianaGraph> extends ShapeGRBinding<O> {

		public GraphGRBinding(String name, Class<? extends O> graphClass, ShapeGRProvider<O> grProvider) {
			super(name, graphClass, grProvider);
			bindingModel.addToBindingVariables(new BindingVariable("gr", ShapeGraphicalRepresentation.class));
		}
	}

}
