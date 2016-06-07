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

package org.openflexo.fge;

import org.openflexo.connie.Bindable;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.layout.BalloonLayoutManagerSpecification;
import org.openflexo.fge.layout.FlowLayoutManagerSpecification;
import org.openflexo.fge.layout.ForceDirectedGraphLayoutManagerSpecification;
import org.openflexo.fge.layout.GraphBasedLayoutManagerSpecification;
import org.openflexo.fge.layout.GridLayoutManagerSpecification;
import org.openflexo.fge.layout.ISOMGraphLayoutManagerSpecification;
import org.openflexo.fge.layout.OutlineLayoutManagerSpecification;
import org.openflexo.fge.layout.RadialTreeLayoutManagerSpecification;
import org.openflexo.fge.layout.TreeBasedLayoutManagerSpecification;
import org.openflexo.fge.layout.TreeLayoutManagerSpecification;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.factory.KeyValueCoding;

/**
 * Represents the specification of a LayoutManager in DIANA<br>
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@Imports({ @Import(GridLayoutManagerSpecification.class), @Import(OutlineLayoutManagerSpecification.class),
		@Import(FlowLayoutManagerSpecification.class), @Import(GraphBasedLayoutManagerSpecification.class),
		@Import(TreeBasedLayoutManagerSpecification.class) })
public interface FGELayoutManagerSpecification<LM extends FGELayoutManager<?, ?>> extends FGEObject, Bindable, KeyValueCoding {

	/**
	 * Exhaustive list of all available {@link FGELayoutManagerSpecification}
	 * 
	 * @author sylvain
	 */
	public static enum LayoutManagerSpecificationType {
		NONE {
			@Override
			public Class<FGELayoutManagerSpecification<?>> getLayoutManagerSpecificationClass() {
				return null;
			}

			@Override
			public String getDefaultLayoutManagerName() {
				return null;
			}
		},
		GRID {
			@Override
			public Class<GridLayoutManagerSpecification> getLayoutManagerSpecificationClass() {
				return GridLayoutManagerSpecification.class;
			}

			@Override
			public String getDefaultLayoutManagerName() {
				return "grid";
			}
		},
		OUTLINE {
			@Override
			public Class<OutlineLayoutManagerSpecification> getLayoutManagerSpecificationClass() {
				return OutlineLayoutManagerSpecification.class;
			}

			@Override
			public String getDefaultLayoutManagerName() {
				return "outline";
			}
		},
		FORCE_DIRECTED_GRAPH {
			@Override
			public Class<ForceDirectedGraphLayoutManagerSpecification> getLayoutManagerSpecificationClass() {
				return ForceDirectedGraphLayoutManagerSpecification.class;
			}

			@Override
			public String getDefaultLayoutManagerName() {
				return "fd-graph";
			}
		},
		ISOM_GRAPH {
			@Override
			public Class<ISOMGraphLayoutManagerSpecification> getLayoutManagerSpecificationClass() {
				return ISOMGraphLayoutManagerSpecification.class;
			}

			@Override
			public String getDefaultLayoutManagerName() {
				return "isom-graph";
			}
		},
		TREE_LAYOUT {
			@Override
			public Class<TreeLayoutManagerSpecification<?>> getLayoutManagerSpecificationClass() {
				return (Class) TreeLayoutManagerSpecification.class;
			}

			@Override
			public String getDefaultLayoutManagerName() {
				return "tree";
			}
		},
		BALLOON_LAYOUT {
			@Override
			public Class<BalloonLayoutManagerSpecification<?>> getLayoutManagerSpecificationClass() {
				return (Class) BalloonLayoutManagerSpecification.class;
			}

			@Override
			public String getDefaultLayoutManagerName() {
				return "balloon";
			}
		},
		RADIAL_TREE {
			@Override
			public Class<RadialTreeLayoutManagerSpecification<?>> getLayoutManagerSpecificationClass() {
				return (Class) RadialTreeLayoutManagerSpecification.class;
			}

			@Override
			public String getDefaultLayoutManagerName() {
				return "radial-tree";
			}
		},
		FLOW {
			@Override
			public Class<FlowLayoutManagerSpecification> getLayoutManagerSpecificationClass() {
				return FlowLayoutManagerSpecification.class;
			}

			@Override
			public String getDefaultLayoutManagerName() {
				return "flow";
			}
		};

		public abstract Class<? extends FGELayoutManagerSpecification<?>> getLayoutManagerSpecificationClass();

		public abstract String getDefaultLayoutManagerName();
	}

	@PropertyIdentifier(type = String.class)
	public static final String IDENTIFIER_KEY = "identifier";
	@PropertyIdentifier(type = FGEModelFactory.class)
	public static final String FACTORY = "factory";
	@PropertyIdentifier(type = Boolean.class)
	public static final String PAINT_DECORATION_KEY = "paintDecoration";
	@PropertyIdentifier(type = DraggingMode.class)
	public static final String DRAGGING_MODE_KEY = "draggingMode";
	@PropertyIdentifier(type = Boolean.class)
	public static final String ANIMATE_LAYOUT_KEY = "animateLayout";
	@PropertyIdentifier(type = Integer.class)
	public static final String ANIMATION_STEPS_NUMBER_KEY = "animationStepsNumber";

	public static GRProperty<String> IDENTIFIER = GRProperty.getGRParameter(FGELayoutManagerSpecification.class, IDENTIFIER_KEY,
			String.class);
	public static GRProperty<Boolean> PAINT_DECORATION = GRProperty.getGRParameter(FGELayoutManagerSpecification.class,
			PAINT_DECORATION_KEY, Boolean.class);
	public static GRProperty<DraggingMode> DRAGGING_MODE = GRProperty.getGRParameter(FGELayoutManagerSpecification.class, DRAGGING_MODE_KEY,
			DraggingMode.class);
	public static GRProperty<Boolean> ANIMATE_LAYOUT = GRProperty.getGRParameter(FGELayoutManagerSpecification.class, ANIMATE_LAYOUT_KEY,
			Boolean.class);
	public static GRProperty<Integer> ANIMATION_STEPS_NUMBER = GRProperty.getGRParameter(FGELayoutManagerSpecification.class,
			ANIMATION_STEPS_NUMBER_KEY, Integer.class);

	/**
	 * Return identifier (a String) for this layout manager specification<br>
	 * This string should be unique regarding the layout managers defined for container
	 * 
	 * @return
	 */
	@Getter(IDENTIFIER_KEY)
	@XMLAttribute
	public String getIdentifier();

	/**
	 * Sets identifier for this layout manager specification<br>
	 * 
	 * @param identifier
	 */
	@Setter(IDENTIFIER_KEY)
	public void setIdentifier(String identifier);

	public Class<? extends LM> getLayoutManagerClass();

	/**
	 * Return {@link FGEModelFactory} to use when creating new layout manager conform to this specification
	 */
	@Override
	@Getter(value = FACTORY, ignoreType = true)
	public FGEModelFactory getFactory();

	/**
	 * Sets {@link FGEModelFactory} to use when creating new layout manager conform to this specification
	 */
	@Override
	@Setter(FACTORY)
	public void setFactory(FGEModelFactory aFactory);

	/**
	 * Return flag indicating whether layout manager decoration is to be paint<br>
	 * Note that this is relevant only if this layout manager supports decoration painting
	 * 
	 * @return
	 */
	@Getter(PAINT_DECORATION_KEY)
	@XMLAttribute
	public Boolean paintDecoration();

	/**
	 * Sets flag indicating whether layout manager decoration is to be paint<br>
	 * Note that this is relevant only if this layout manager supports decoration painting
	 * 
	 * @param paintDecoration
	 */
	@Setter(PAINT_DECORATION_KEY)
	public void setPaintDecoration(Boolean paintDecoration);

	/**
	 * Return Dragging-mode to use
	 * 
	 * @return
	 */
	@Getter(DRAGGING_MODE_KEY)
	@XMLAttribute
	public DraggingMode getDraggingMode();

	/**
	 * Sets Dragging-mode to use
	 * 
	 * @param draggingMode
	 */
	@Setter(DRAGGING_MODE_KEY)
	public void setDraggingMode(DraggingMode draggingMode);

	/**
	 * Return flag indicating whether layout should be performed using animation
	 * 
	 * @return
	 */
	@Getter(value = ANIMATE_LAYOUT_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean animateLayout();

	/**
	 * Sets flag indicating whether layout should be performed using animation
	 * 
	 * @param paintDecoration
	 */
	@Setter(ANIMATE_LAYOUT_KEY)
	public void setAnimateLayout(boolean animateLayout);

	/**
	 * Return number of steps to be performed for animations
	 * 
	 * @return
	 */
	@Getter(value = ANIMATION_STEPS_NUMBER_KEY, defaultValue = "10")
	@XMLAttribute
	public int getAnimationStepsNumber();

	/**
	 * Sets number of steps to be performed for animations
	 * 
	 * @param stepsNumber
	 */
	@Setter(ANIMATION_STEPS_NUMBER_KEY)
	public void setAnimationStepsNumber(int stepsNumber);

	/**
	 * Build and return a new {@link FGELayoutManager} conform to this {@link FGELayoutManagerSpecification}
	 * 
	 * @param containerNode
	 * @return
	 */
	public LM makeLayoutManager(ContainerNode<?, ?> containerNode);

	/**
	 * Return flag indicating whether this layout manager supports autolayout
	 * 
	 * @return
	 */
	public boolean supportAutolayout();

	/**
	 * Return flag indicating whether this layout manager supports decoration painting<br>
	 * 
	 * @return
	 */
	public boolean supportDecoration();

	public LayoutManagerSpecificationType getLayoutManagerSpecificationType();

	/**
	 * Represents Dragging-mode to use
	 * 
	 * @author sylvain
	 *
	 */
	public enum DraggingMode {
		/**
		 * Let the user freely drag, perform no layout (but invalidate node)
		 */
		FreeDraggingNoLayout {
			@Override
			public boolean relayoutOnDrag() {
				return false;
			}

			@Override
			public boolean relayoutAfterDrag() {
				return false;
			}

			@Override
			public boolean allowsDragging() {
				return true;
			}
		},
		/**
		 * Let the user freely drag, and perform layout at the end of drag (mouse-released event)
		 */
		FreeDiaggingAndLayout {
			@Override
			public boolean relayoutOnDrag() {
				return false;
			}

			@Override
			public boolean relayoutAfterDrag() {
				return true;
			}

			@Override
			public boolean allowsDragging() {
				return true;
			}
		},
		/**
		 * Let the user freely drag, perform continuous layout
		 */
		ContinuousLayout {
			@Override
			public boolean relayoutOnDrag() {
				return true;
			}

			@Override
			public boolean relayoutAfterDrag() {
				return true;
			}

			@Override
			public boolean allowsDragging() {
				return true;
			}
		},
		/**
		 * Let the user drag on a constrained area (defined in layout manager implementation)
		 */
		ConstrainedDragging {
			@Override
			public boolean relayoutOnDrag() {
				return false;
			}

			@Override
			public boolean relayoutAfterDrag() {
				return true;
			}

			@Override
			public boolean allowsDragging() {
				return true;
			}
		},
		/**
		 * Do not allow dragging
		 */
		NoDragging {
			@Override
			public boolean relayoutOnDrag() {
				return false;
			}

			@Override
			public boolean relayoutAfterDrag() {
				return false;
			}

			@Override
			public boolean allowsDragging() {
				return false;
			}
		};

		public abstract boolean relayoutOnDrag();

		public abstract boolean relayoutAfterDrag();

		public abstract boolean allowsDragging();
	}

}
