package org.openflexo.diana.vaadin.view.widgetset.client.vdrawingview;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.PostLayoutListener;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.shared.ui.Connect;

import org.openflexo.diana.vaadin.view.VDrawingView;

import com.vaadin.client.communication.RpcProxy;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.client.MouseEventDetailsBuilder;

import org.openflexo.diana.vaadin.view.widgetset.client.vdrawingview.VDrawingViewClientRpc;
import org.openflexo.diana.vaadin.view.widgetset.client.vdrawingview.VDrawingViewServerRpc;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.geom.FGEGeneralShape;
import org.openflexo.fge.geom.FGERectangle;

@SuppressWarnings("serial")
@Connect(VDrawingView.class)
public class VDrawingViewConnector extends AbstractComponentConnector implements SimpleManagedLayout, PostLayoutListener {

	private boolean needsDraw = false;
	private final List<Command> commands;
	private final VDrawingViewServerRpc rpc = RpcProxy.create(VDrawingViewServerRpc.class,
			this);
	
	public VDrawingViewConnector() {
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
		
		registerRpc(VDrawingViewClientRpc.class, new VDrawingViewClientRpc() {
			
			private Context2d ctx = getWidget().getContext2d();
			//private WebGraphics g = new WebGraphics(ctx);

			
			@Override
			public void clear() {
				ctx.clearRect(0, 0, getWidget().getCoordinateSpaceWidth(),
						getWidget().getCoordinateSpaceHeight());
				clearCommands();
				
				}
			
			@Override
			public void beginPath() {
				runCommand(new Command() {
					@Override
					public void execute() {
						ctx.beginPath();
					}
				});
			}
			
			@Override
			public void closePath() {
				runCommand(new Command() {
					@Override
					public void execute() {
						ctx.closePath();
					}
				});
			}
			
			@Override
			public void setStrokeStyle(final String rgb) {
				runCommand(new Command() {
					@Override
					public void execute() {
						ctx.setStrokeStyle(rgb);
					}
				});
			}

			@Override
			public void setFillStyle(final String color) {
				runCommand(new Command() {
					@Override
					public void execute() {
						ctx.setFillStyle(color);
					}
				});	
			}
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
					final Double width, final Double height) {
				runCommand(new Command() {
					@Override
					public void execute() {
						ctx.fillRect(startX, startY, width, height);
					}
				});
			}

			@Override
			public void delete() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void createGraphics() {
				ctx = getWidget().getContext2d();		
			}

			@Override
			public void applyCurrentTextStyle() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void drawPoint(Double x, Double y) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void fillText(final String text, final Double x, final Double y) {
				runCommand(new Command() {
					@Override
					public void execute() {
					ctx.fillText(text, x, y);
					}
				
				});
			}
			
			@Override
			public void stroke() {
				runCommand(new Command() {
					@Override
					public void execute() {
						ctx.stroke();
					}
				});
			}
			@Override
			public void drawRect(final Double x, final Double y, 
				final Double width, final Double height) {
				runCommand(new Command() {
					@Override
					public void execute() {
						/*ctx.setStrokeStyle("red");
						ctx.setLineWidth(3);
						ctx.moveTo(x, y);
						ctx.rect(x, y, width, height);
						ctx.stroke();*/
						ctx.fillRect(x, y, width, height);
					}
				});
				
			}

			@Override
			public void fillRoundRect(Double x, Double y, Double width,
					Double height, Double arcwidth, Double archeight) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void drawRoundRect(Double x, Double y, Double width,
					Double height, Double arcwidth, Double archeight) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void drawLine(Double x1, Double y1, Double x2, Double y2) {
				
				
			}

			@Override
			public void drawCircle(Double x, Double y, Double width,
					Double height) {
				runCommand(new Command() {
					@Override
					public void execute() {
						//ctx.arc(x, y, radius, startAngle, endAngle);
					}
				});
			}

			@Override
			public void fillCircle(Double x, Double y, Double width,
					Double height) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void fillGeneralShape(FGEGeneralShape shape) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public FGERectangle drawString(String text, Double x, Double y,
					int orientation, HorizontalTextAlignment alignment) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void drawArc(final Double x, final Double y, final Double radius,
					final Double startAngle, final Double endAngle) {		
				runCommand(new Command() {
					@Override
					public void execute() {
						ctx.arc(x, y, radius, startAngle, endAngle);
						
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
			
//			@Override
//			public void drawImage2(final ImageElement image, final Double x, final Double y) {
//				runCommand(new Command() {
//					@Override
//					public void execute() {
//						ctx.drawImage(image, x, y);
//					}
//				});				
//			}
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
}

