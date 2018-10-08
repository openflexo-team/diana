package org.openflexo.diana.geomedit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.openflexo.diana.control.DianaInteractiveViewer;
import org.openflexo.diana.geomedit.GeomEditApplication.SynchronizedMenuItem;
import org.openflexo.diana.geomedit.GeomEditApplication.WindowMenuItem;
import org.openflexo.exceptions.CopyException;
import org.openflexo.exceptions.CutException;
import org.openflexo.exceptions.PasteException;
import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.swing.utils.localization.LocalizedEditor;
import org.openflexo.gina.swing.utils.logging.FlexoLoggingViewer;
import org.openflexo.icon.ImageIconResource;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.model.undo.UndoManager;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.HasPropertyChangeSupport;

@SuppressWarnings("serial")
public class GeomEditMenuBar extends JMenuBar implements PreferenceChangeListener {

	private final GeomEditApplication geomEditApplication;

	private final JMenu fileMenu;
	private final JMenu editMenu;
	private final JMenu viewMenu;
	private final JMenu toolsMenu;
	private final JMenu helpMenu;

	private final JMenuItem newItem;
	private final JMenuItem loadItem;
	private final JMenuItem saveItem;
	private final JMenuItem saveAsItem;
	private final JMenuItem closeItem;
	private final JMenuItem quitItem;

	final SynchronizedMenuItem copyItem;
	final SynchronizedMenuItem cutItem;
	final SynchronizedMenuItem pasteItem;
	final SynchronizedMenuItem undoItem;
	final SynchronizedMenuItem redoItem;

	private final JMenuItem logsItem;
	private final JMenuItem localizedItem;

	private final JMenu openRecent;

	private final JMenuItem showPaletteItem;

	// Actions icons
	public static final ImageIcon UNDO_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Undo.png"));
	public static final ImageIcon REDO_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Redo.png"));
	public static final ImageIcon COPY_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Copy.png"));
	public static final ImageIcon PASTE_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Paste.png"));
	public static final ImageIcon CUT_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Cut.png"));
	public static final ImageIcon DELETE_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Delete.png"));
	public static final ImageIcon HELP_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Help.png"));
	public static final ImageIcon IMPORT_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Import.png"));
	public static final ImageIcon EXPORT_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Export.png"));
	public static final ImageIcon OPEN_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Open.png"));
	public static final ImageIcon NEW_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/New.png"));
	public static final ImageIcon PRINT_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Print.png"));
	public static final ImageIcon SAVE_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Save.png"));
	public static final ImageIcon SAVE_DISABLED_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Save-disabled.png"));
	public static final ImageIcon SAVE_AS_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Save-as.png"));
	public static final ImageIcon SAVE_ALL_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Save-all.png"));
	public static final ImageIcon NETWORK_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Network.png"));
	public static final ImageIcon INFO_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Info.png"));
	public static final ImageIcon INSPECT_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Inspect.png"));
	public static final ImageIcon REFRESH_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Refresh.png"));
	public static final ImageIcon REFRESH_DISABLED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/Refresh-disabled.png"));

	public GeomEditMenuBar(GeomEditApplication geomEditApplication) {
		this.geomEditApplication = geomEditApplication;
		fileMenu = new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("file"));
		editMenu = new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("edit"));
		viewMenu = new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("view"));
		toolsMenu = new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("tools"));
		helpMenu = new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("help"));

		newItem = new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("new_diagram"), NEW_ICON);
		newItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GeomEditMenuBar.this.geomEditApplication.newDiagramEditor();
			}
		});

		loadItem = new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("open_diagram"), OPEN_ICON);
		loadItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GeomEditMenuBar.this.geomEditApplication.loadDiagramEditor();
			}
		});

		openRecent = new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("open_recent"));
		GeomEditPreferences.addPreferenceChangeListener(this);
		updateOpenRecent();
		saveItem = new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("save_diagram"), SAVE_ICON);
		saveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GeomEditMenuBar.this.geomEditApplication.saveDiagramEditor();
			}
		});

		saveAsItem = new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("save_diagram_as"), SAVE_AS_ICON);
		saveAsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GeomEditMenuBar.this.geomEditApplication.saveDrawingAs();
			}
		});

		closeItem = new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("close"));
		closeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GeomEditMenuBar.this.geomEditApplication.closeDrawing();
			}
		});

		quitItem = new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("quit"));
		quitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GeomEditMenuBar.this.geomEditApplication.quit();
			}
		});

		fileMenu.add(newItem);
		fileMenu.add(loadItem);
		fileMenu.add(openRecent);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.add(closeItem);
		fileMenu.addSeparator();

		fileMenu.add(quitItem);

		showPaletteItem = new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("show_palette"));
		showPaletteItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// paletteDialog.setVisible(true);
			}
		});
		logsItem = new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("logs"));
		logsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance(), ApplicationFIBLibraryImpl.instance(),
						GeomEditMenuBar.this.geomEditApplication.getFrame());
			}
		});

		localizedItem = new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("localized_editor"));
		localizedItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GeomEditMenuBar.this.geomEditApplication.localizedEditor == null) {
					GeomEditMenuBar.this.geomEditApplication.localizedEditor = new LocalizedEditor(
							GeomEditMenuBar.this.geomEditApplication.getFrame(), "localized_editor",
							GeomEditApplication.GEOMEDIT_LOCALIZATION, GeomEditApplication.GEOMEDIT_LOCALIZATION, true, false);
				}
				GeomEditMenuBar.this.geomEditApplication.localizedEditor.setVisible(true);
			}
		});

		copyItem = this.geomEditApplication.makeSynchronizedMenuItem("copy", COPY_ICON,
				KeyStroke.getKeyStroke(KeyEvent.VK_C, GeomEditApplication.META_MASK), new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println("copy");
						try {
							GeomEditMenuBar.this.geomEditApplication.getCurrentDiagramEditor().getController().copy();
						} catch (CopyException e1) {
							e1.printStackTrace();
						}
					}
				}, new GeomEditApplication.Synchronizer() {
					@Override
					public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
						if (observable instanceof GeomEditDrawingController) {
							menuItem.setEnabled(((GeomEditDrawingController) observable).isCopiable());
						}
					}
				});

		cutItem = this.geomEditApplication.makeSynchronizedMenuItem("cut", CUT_ICON,
				KeyStroke.getKeyStroke(KeyEvent.VK_X, GeomEditApplication.META_MASK), new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println("cut");
						try {
							GeomEditMenuBar.this.geomEditApplication.getCurrentDiagramEditor().getController().cut();
						} catch (CutException e1) {
							e1.printStackTrace();
						}
					}
				}, new GeomEditApplication.Synchronizer() {
					@Override
					public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
						if (observable instanceof GeomEditDrawingController) {
							menuItem.setEnabled(((GeomEditDrawingController) observable).isCutable());
						}
					}
				});

		pasteItem = this.geomEditApplication.makeSynchronizedMenuItem("paste", PASTE_ICON,
				KeyStroke.getKeyStroke(KeyEvent.VK_V, GeomEditApplication.META_MASK), new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println("paste");
						if (GeomEditMenuBar.this.geomEditApplication.getCurrentDiagramEditor() != null) {
							try {
								GeomEditMenuBar.this.geomEditApplication.getCurrentDiagramEditor().getController().paste();
							} catch (PasteException e1) {
								e1.printStackTrace();
							}
						}
					}
				}, new GeomEditApplication.Synchronizer() {
					@Override
					public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
						if (observable instanceof DianaInteractiveViewer) {
							menuItem.setEnabled(
									GeomEditMenuBar.this.geomEditApplication.getCurrentDiagramEditor().getController().isPastable());
						}
					}
				});

		undoItem = this.geomEditApplication.makeSynchronizedMenuItem("undo", UNDO_ICON,
				KeyStroke.getKeyStroke(KeyEvent.VK_Z, GeomEditApplication.META_MASK), new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println("undo");
						GeomEditMenuBar.this.geomEditApplication.getCurrentDiagramEditor().getController().undo();
					}
				}, new GeomEditApplication.Synchronizer() {
					@Override
					public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
						if (observable instanceof UndoManager) {
							menuItem.setEnabled(
									GeomEditMenuBar.this.geomEditApplication.getCurrentDiagramEditor().getController().canUndo());
							if (GeomEditMenuBar.this.geomEditApplication.getCurrentDiagramEditor().getController().canUndo()) {
								menuItem.setText(GeomEditMenuBar.this.geomEditApplication.getCurrentDiagramEditor().getController()
										.getFactory().getUndoManager().getUndoPresentationName());
							}
							else {
								menuItem.setText(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("undo"));
							}
						}
					}
				});

		redoItem = this.geomEditApplication.makeSynchronizedMenuItem("redo", REDO_ICON,
				KeyStroke.getKeyStroke(KeyEvent.VK_R, GeomEditApplication.META_MASK), new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println("redo");
						GeomEditMenuBar.this.geomEditApplication.getCurrentDiagramEditor().getController().redo();
					}
				}, new GeomEditApplication.Synchronizer() {
					@Override
					public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
						if (observable instanceof UndoManager) {
							menuItem.setEnabled(
									GeomEditMenuBar.this.geomEditApplication.getCurrentDiagramEditor().getController().canRedo());
							if (GeomEditMenuBar.this.geomEditApplication.getCurrentDiagramEditor().getController().canRedo()) {
								menuItem.setText(GeomEditMenuBar.this.geomEditApplication.getCurrentDiagramEditor().getController()
										.getFactory().getUndoManager().getRedoPresentationName());
							}
							else {
								menuItem.setText(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("redo"));
							}
						}
					}
				});

		editMenu.add(copyItem);
		editMenu.add(cutItem);
		editMenu.add(pasteItem);
		editMenu.addSeparator();
		editMenu.add(undoItem);
		editMenu.add(redoItem);

		WindowMenuItem foregroundInspectorItem = this.geomEditApplication.new WindowMenuItem(
				GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("foreground_inspector"),
				this.geomEditApplication.inspectors.getForegroundStyleInspector());
		WindowMenuItem backgroundInspectorItem = this.geomEditApplication.new WindowMenuItem(
				GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("background_inspector"),
				this.geomEditApplication.inspectors.getBackgroundStyleInspector());
		WindowMenuItem textInspectorItem = this.geomEditApplication.new WindowMenuItem(
				GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("text_inspector"),
				this.geomEditApplication.inspectors.getTextPropertiesInspector());
		WindowMenuItem shapeInspectorItem = this.geomEditApplication.new WindowMenuItem(
				GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("shape_inspector"),
				this.geomEditApplication.inspectors.getShapeInspector());
		WindowMenuItem connectorInspectorItem = this.geomEditApplication.new WindowMenuItem(
				GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("connector_inspector"),
				this.geomEditApplication.inspectors.getConnectorInspector());
		WindowMenuItem shadowInspectorItem = this.geomEditApplication.new WindowMenuItem(
				GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("shadow_inspector"),
				this.geomEditApplication.inspectors.getShadowStyleInspector());
		WindowMenuItem locationSizeInspectorItem = this.geomEditApplication.new WindowMenuItem(
				GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("location_size_inspector"),
				this.geomEditApplication.inspectors.getLocationSizeInspector());
		WindowMenuItem layoutManagerInspectorItem = this.geomEditApplication.new WindowMenuItem(
				GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("layout_manager_inspector"),
				this.geomEditApplication.inspectors.getLayoutManagersInspector());

		// WindowMenuItem paletteItem = new WindowMenuItem(GEOMEDIT_LOCALIZATION.localizedForKey("palette"), paletteDialog);

		viewMenu.add(foregroundInspectorItem);
		viewMenu.add(backgroundInspectorItem);
		viewMenu.add(textInspectorItem);
		viewMenu.add(shapeInspectorItem);
		viewMenu.add(connectorInspectorItem);
		viewMenu.add(shadowInspectorItem);
		viewMenu.add(locationSizeInspectorItem);
		viewMenu.add(layoutManagerInspectorItem);
		// viewMenu.addSeparator();
		// viewMenu.add(paletteItem);

		toolsMenu.add(showPaletteItem);
		toolsMenu.add(logsItem);
		toolsMenu.add(localizedItem);

		add(fileMenu);
		add(editMenu);
		add(viewMenu);
		add(toolsMenu);
		add(helpMenu);
	}

	private boolean willUpdate = false;

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		if (evt.getKey().startsWith(GeomEditPreferences.LAST_FILE)) {
			if (willUpdate) {
				return;
			}
			willUpdate = true;
			SwingUtilities.invokeLater(() -> {
				willUpdate = false;
				updateOpenRecent();
			});
		}
	}

	private void updateOpenRecent() {
		openRecent.removeAll();
		List<File> files = GeomEditPreferences.getLastFiles();
		openRecent.setEnabled(files.size() != 0);
		for (final File file : files) {
			JMenuItem item = new JMenuItem(file.getName());
			item.setToolTipText(file.getAbsolutePath());
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GeomEditMenuBar.this.geomEditApplication.loadDiagramEditor(file);
				}
			});
			openRecent.add(item);
		}
	}

}
