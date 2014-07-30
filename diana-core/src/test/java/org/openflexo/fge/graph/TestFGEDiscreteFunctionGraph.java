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
	private static FGENumericFunction<Integer> ageFunction;
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
		public int age;
		public double weight;

		public Person(String name, int age, double weight) {
			super();
			this.name = name;
			this.age = age;
			this.weight = weight;
		}
	}

	@Test
	@TestOrder(1)
	public void test1InitGraph() {
		List<Person> persons = new ArrayList<Person>();
		persons.add(martin = new Person("Martin", 39, 73.7));
		persons.add(mary = new Person("Mary", 41, 57.0));
		persons.add(john = new Person("John", 9, 26.3));
		persons.add(martinJr = new Person("Martin Jr", 5, 19.2));

		graph = new FGEDiscreteFunctionGraph<Person>();

		graph.setParameter("person", Person.class);
		graph.setDiscreteValues(persons);
		graph.setDiscreteValuesLabel(new DataBinding<String>("person.name"));
		graph.setParameterOrientation(Orientation.HORIZONTAL);
		// graph.setDiscreteValuesSpacing(80.0);

		ageFunction = graph.addNumericFunction("age", Integer.class, new DataBinding<Integer>("person.age"), GraphType.BAR_GRAPH);
		ageFunction.setRange(0, 100);
		ageFunction.setForegroundStyle(FACTORY.makeForegroundStyle(Color.RED, 1.0f));
		ageFunction.setBackgroundStyle(FACTORY.makeColorGradientBackground(Color.BLUE, Color.WHITE, ColorGradientDirection.NORTH_SOUTH));

		weightFunction = graph.addNumericFunction("weight", Double.class, new DataBinding<Double>("person.weight"), GraphType.POLYLIN);
		weightFunction.setForegroundStyle(FACTORY.makeForegroundStyle(Color.GREEN, 1.0f));
		weightFunction.setBackgroundStyle(FACTORY.makeColoredBackground(Color.YELLOW));

	}

	@Test
	@TestOrder(2)
	public void testEvaluation() throws TypeMismatchException, NullReferenceException, InvocationTargetException {
		assertEquals((Integer) 39, graph.evaluateFunction(ageFunction, martin));
		assertEquals((Integer) 41, graph.evaluateFunction(ageFunction, mary));
		assertEquals((Integer) 9, graph.evaluateFunction(ageFunction, john));
		assertEquals((Integer) 5, graph.evaluateFunction(ageFunction, martinJr));
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
			System.out.println("For " + p + " age is " + graph.evaluateFunction(ageFunction, p) + " waight is "
					+ graph.evaluateFunction(weightFunction, p));
			i++;
		}
		assertEquals(4, i);

	}

}
