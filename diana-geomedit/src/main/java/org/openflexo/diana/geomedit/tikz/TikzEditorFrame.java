/**
 * 
 */
package org.openflexo.diana.geomedit.tikz;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

/**
 * @author Quentin
 *
 */
public class TikzEditorFrame {
	private TikzConnectorExtended tikzConnector;
	
	private JFrame frame;
	private JPanel mainPanel, headPanel, buttonPanel;
	private RSyntaxTextArea textArea;
	private JButton buttonTikzToGeomEdit, buttonGeomEditToTikz;
	
	public TikzEditorFrame(TikzConnectorExtended tc) {
		tikzConnector = tc;
		
		buttonTikzToGeomEdit = new JButton();
		buttonTikzToGeomEdit.setText("Push to constructions");
		buttonTikzToGeomEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tikzConnector.pushTikzToGeomEdit();
			}
		});
		
		buttonGeomEditToTikz = new JButton();
		buttonGeomEditToTikz.setText("Pull from constructions");
		buttonGeomEditToTikz.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tikzConnector.pushGeomEditToTikz();
			}
		});
		
		buttonPanel = new JPanel();
		buttonPanel.add(buttonTikzToGeomEdit);
		buttonPanel.add(buttonGeomEditToTikz);
		
		headPanel = new JPanel(new BorderLayout());
		headPanel.add(buttonPanel, BorderLayout.CENTER);
		
		textArea = new RSyntaxTextArea();
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_LATEX);
		textArea.setText("\\begin{tikzpicture}\n\n\\end{tikzpicture}");
		
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(headPanel, BorderLayout.NORTH);
		mainPanel.add(textArea, BorderLayout.CENTER);
		
		frame = new JFrame();
		frame.setTitle("Tikz Editor");
		frame.getContentPane().add(mainPanel);
		frame.validate();
		frame.setSize(600,  400);
	}
	
	public void setVisible(boolean b) {
		frame.setVisible(b);
	}
	
	public String getText() {
		return textArea.getText();
	}
	
	public void setText(String s) {
		textArea.setText(s);
	}
	
}
