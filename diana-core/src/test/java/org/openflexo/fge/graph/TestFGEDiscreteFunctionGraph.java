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

package org.openflexo.fge.graph;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.graph.FGEFunctionGraph.Orientation;
import org.openflexo.fge.graph.FGEGraph.GraphType;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

@RunWith(OrderedRunner.class)
public class TestFGEDiscreteFunctionGraph {

	private static FGEModelFactory FACTORY;

	private static FGEDiscreteFunctionGraph<Person> graph;
	private static FGENumericFunction<Integer> sizeFunction;
	private static FGENumericFunction<Double> weightFunction;

	private static Person martin;
	private static Person mary;
	private static Person john;
	private static Person martinJr;

	@BeforeClass
	public static void beforeClass() throws ModelDefinitionException {
		FACTORY = new FGEModelFactoryImpl();
	}

	@AfterClass
	public static void afterClass() {
		// FACTORY.delete();
	}

	@Before
	public void before() {
	}

	@After
	public void after() {
	}

	public static class Person {
		public String name;
		public int size;
		public double weight;

		public Person(String name, int size, double weight) {
			super();
			this.name = name;
			this.size = size;
			this.weight = weight;
		}
	}

	@Test
	@TestOrder(1)
	public void test1InitGraph() {
		List<Person> persons = new ArrayList<Person>();
		persons.add(martin = new Person("Martin", 173, 73.7));
		persons.add(mary = new Person("Mary", 165, 57.0));
		persons.add(john = new Person("John", 107, 26.3));
		persons.add(martinJr = new Person("Martin Jr", 97, 19.2));

		graph = new FGEDiscreteFunctionGraph<Person>();

		graph.setParameter("person", Person.class);
		graph.setDiscreteValues(persons);
		graph.setDiscreteValuesLabel(new DataBinding<String>("person.name"));
		graph.setParameterOrientation(Orientation.HORIZONTAL);
		// graph.setDiscreteValuesSpacing(80.0);

		sizeFunction = graph.addNumericFunction("size", Integer.class, new DataBinding<Integer>("person.size"), GraphType.BAR_GRAPH);
		sizeFunction.setRange(0, 100);
		sizeFunction.setForegroundStyle(FACTORY.makeForegroundStyle(Color.BLUE, 1.0f));
		sizeFunction.setBackgroundStyle(FACTORY.makeColorGradientBackground(Color.BLUE, Color.WHITE, ColorGradientDirection.NORTH_SOUTH));

		weightFunction = graph.addNumericFunction("weight", Double.class, new DataBinding<Double>("person.weight"), GraphType.POLYLIN);
		weightFunction.setForegroundStyle(FACTORY.makeForegroundStyle(Color.GREEN, 1.0f));
		weightFunction.setBackgroundStyle(FACTORY.makeColoredBackground(Color.YELLOW));

	}

	@Test
	@TestOrder(2)
	public void testEvaluation() throws TypeMismatchException, NullReferenceException, InvocationTargetException {
		assertEquals((Integer) 173, graph.evaluateFunction(sizeFunction, martin));
		assertEquals((Integer) 165, graph.evaluateFunction(sizeFunction, mary));
		assertEquals((Integer) 107, graph.evaluateFunction(sizeFunction, john));
		assertEquals((Integer) 97, graph.evaluateFunction(sizeFunction, martinJr));
		assertEquals((Double) 73.7, graph.evaluateFunction(weightFunction, martin));
		assertEquals((Double) 57.0, graph.evaluateFunction(weightFunction, mary));
		assertEquals((Double) 26.3, graph.evaluateFunction(weightFunction, john));
		assertEquals((Double) 19.2, graph.evaluateFunction(weightFunction, martinJr));
	}

	@Test
	@TestOrder(3)
	public void testIterate() throws TypeMismatchException, NullReferenceException, InvocationTargetException {
		int i = 0;
		Iterator<Person> it = graph.iterateParameter();
		while (it.hasNext()) {
			Person p = it.next();
			System.out.println("For " + p + " age is " + graph.evaluateFunction(sizeFunction, p) + " waight is "
					+ graph.evaluateFunction(weightFunction, p));
			i++;
		}
		assertEquals(4, i);

	}

}
