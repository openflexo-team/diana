package org.openflexo.diana.vaadin.widgetset.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.PostLayoutListener;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.shared.Connector;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.Connect;

@SuppressWarnings("serial")
//@Connect(org.openflexo.diana.vaadin.graphics.VFGEGraphics.class)
public class VaadinConnector extends AbstractComponentConnector implements
SimpleManagedLayout, PostLayoutListener {

	private boolean needsDraw = false;
	private final List<Command> commands;
	private final VaadinServerRpc rpc = RpcProxy.create(VaadinServerRpc.class,
			this);
	
	public VaadinConnector() {
		commands = new ArrayList<Command>();
	}
	@Override
	protected void init() {
		super.init();
		
		getWidget().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				MouseEventDetails med = MouseEventDetailsBuilder
						.buildMouseEventDetails(event.getNativeEvent(),
								getWidget().getElement());

				rpc.clicked(med);
			}
		});

		getWidget().addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				MouseEventDetails med = MouseEventDetailsBuilder
						.buildMouseEventDetails(event.getNativeEvent(),
								getWidget().getElement());

				rpc.pressed(med);
			}
		});
		
		registerRpc(VaadinClientRPC.class, new VaadinClientRPC() {
			private static final long serialVersionUID = -7521521510799765779L;

			private final Context2d ctx = getWidget().getContext2d();

			@Override
			public void fill() {
				runCommand(new Command() {
					@Override
					public void execute() {
						ctx.fill();
					}
				});			
			}

			@Override
			public void fillRect(final Double startX, final Double startY,
					final Double rectWidth, final Double rectHeight) {
				runCommand(new Command() {
					@Override
					public void execute() {
						ctx.fillRect(startX, startY, rectWidth, rectHeight);
					}
				});
				
			}

			@Override
			public void fillText(String text, Double x, Double y,
					Double maxWidth) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setFont(final String font) {
				runCommand(new Command() {
					@Override
					public void execute() {
						ctx.setFont(font);
					}
				});
			}

			@Override
			public void lineTo(final Double x, final Double y) {
				runCommand(new Command() {
					@Override
					public void execute() {
						ctx.lineTo(x, y);
					}
				});
			}

			@Override
			public void moveTo(final Double x, final Double y) {
				runCommand(new Command() {
					@Override
					public void execute() {
						ctx.moveTo(x, y);
					}
				});
			}

			@Override
			public void saveContext() {
				runCommand(new Command() {
					@Override
					public void execute() {
						ctx.save();
					}
				});
			}

			@Override
			public void clear() {
				ctx.clearRect(0, 0, getWidget().getCoordinateSpaceWidth(),
						getWidget().getCoordinateSpaceHeight());
				clearCommands();
			}

			@Override
			public void setFillStyle(String color) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void drawRect(final double x, final double y, final double width, final double height) {
				// TODO Auto-generated method stub
				runCommand(new Command() {
					@Override
					public void execute() {
						ctx.fillRect(x, y, width, height);
					}
				});
			}
		});
	}
	
	@Override
	protected Widget createWidget() {
		return Canvas.createIfSupported();
	}

	@Override
	public Canvas getWidget() {
		return (Canvas) super.getWidget();
	}
	
	
	@Override
	public void postLayout() {
		if (needsDraw) {
			for (Command cmd : commands)
				cmd.execute();
			needsDraw = false;
		}
	}

	public void runCommand(Command command) {
		if (commands.add(command))
			command.execute();
	}

	public void clearCommands() {
		commands.clear();
	}

	@Override
	public void layout() {
		int newHt = getWidget().getElement().getOffsetHeight();
		if (newHt != getWidget().getCoordinateSpaceHeight()) {
			getWidget().setCoordinateSpaceHeight(newHt);
			needsDraw = true;
		}

		int newWt = getWidget().getElement().getOffsetWidth();
		if (newWt != getWidget().getCoordinateSpaceWidth()) {
			getWidget().setCoordinateSpaceWidth(newWt);
			needsDraw = true;
		}
	}

}