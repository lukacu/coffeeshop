package org.coffeeshop.swing.viewers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import org.coffeeshop.awt.StackLayout;
import org.coffeeshop.swing.ImageStore;
import org.coffeeshop.swing.PersistentWindow;
import org.coffeeshop.swing.figure.FigurePanel;
import org.coffeeshop.swing.figure.LayeredFigure;
import org.coffeeshop.swing.figure.ToolTipAction;

public class LayeredFigureViewer extends PersistentWindow {

	private static final long serialVersionUID = 1L;

	private ToolTipAction zoomNormal = new ToolTipAction("Zoom 100%", "zoom-normal-16.png") {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent arg0) {

			if (panel != null)
				panel.setZoom(1);

		}

	};

	private ToolTipAction zoomIn = new ToolTipAction("Zoom in", "zoom-in-16.png") {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent arg0) {

			if (panel == null)
				return;

			double zoom = panel.getZoom();
			
			if (zoom < 1) {
				zoom = (1 / zoom) - 0.1;
				panel.setZoom(1 / zoom);
			} else {
				panel.setZoom(zoom + 0.1);
			}
		}

	};
	
	private ToolTipAction zoomOut = new ToolTipAction("Zoom out", "zoom-out-16.png") {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent arg0) {

			if (panel == null)
				return;

			double zoom = panel.getZoom();
			
			if (zoom < 1) {
				zoom = (1 / zoom) + 0.1;
				panel.setZoom(1 / zoom);
			} else {
				panel.setZoom(zoom - 0.1);
			}
		}

	};
	
	private class LayerSelector implements ItemListener {

		private int layer;
		
		public LayerSelector(int layer) {
			this.layer = layer;
		}
		
		public void itemStateChanged(ItemEvent e) {
			
		
			if (e.getStateChange() == ItemEvent.DESELECTED) 
				figure.setVisible(layer, false);

			if (e.getStateChange() == ItemEvent.SELECTED) 
				figure.setVisible(layer, true);
			
			panel.repaint();
			
		}
		
		
		
	}
	
	
	private FigurePanel panel = new FigurePanel();

	private JToolBar menu = new JToolBar(JToolBar.HORIZONTAL);
	
	private LayeredFigure figure;
	
	public LayeredFigureViewer(String title, LayeredFigure figure) {
		super("viewer.image", title);

		this.figure = figure;
		
		initGUI();
	}

	public LayeredFigureViewer(LayeredFigure figure) {
		super("viewer.image", figure.getName());

		this.figure = figure;
		
		initGUI();
	}
	
	private void initGUI() {

		setLayout(new BorderLayout());

		setIconImage(ImageStore.getImage("image-16.png"));

		menu.setFloatable(false);

		JSplitPane body = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		add(body, BorderLayout.CENTER);
		
		add(menu, BorderLayout.NORTH);
		body.setLeftComponent(panel);

		panel.setMinimumSize(new Dimension(100, 100));
		panel.setFigure(figure);
		
		JPanel layers = new JPanel(new StackLayout(StackLayout.Orientation.VERTICAL, true));
		
		for (int i = 0; i < figure.size(); i++) {
			
			JCheckBox check = new JCheckBox(figure.getName(i), figure.isVisible(i));
			check.addItemListener(new LayerSelector(i));
			
			layers.add(check);

		}
		
		body.setRightComponent(new JScrollPane(layers));
		body.setResizeWeight(0.9);
		installMenu();

	}
	
	private void installMenu() {

		menu.removeAll();

		menu.add(zoomIn);
		menu.add(zoomOut);
		menu.add(zoomNormal);
	
		menu.addSeparator();
	
	}
	
	@Override
	protected void defaultState() {
		
		
		setSize(500, 500);
		
		
	}

}
