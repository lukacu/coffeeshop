package org.coffeeshop.swing.viewers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.coffeeshop.awt.StackLayout;
import org.coffeeshop.awt.StackLayout.Orientation;
import org.coffeeshop.swing.ImageStore;
import org.coffeeshop.swing.PersistentWindow;
import org.coffeeshop.swing.figure.AdvancedLayeredFigure;
import org.coffeeshop.swing.figure.Figure;
import org.coffeeshop.swing.figure.FigurePanel;
import org.coffeeshop.swing.figure.ToolTipAction;

public class AdvancedLayeredFigureViewer extends PersistentWindow {

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
				panel.setZoom(zoom + zoom);
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
				panel.setZoom(zoom - zoom);
			}
		}

	};
	
	private class LayerControl extends JPanel {

		private static final long serialVersionUID = 1L;

		private Figure layer;
		
		private JSpinner offsetX, offsetY;
		
		private JSlider alpha;
		
		public LayerControl(Figure l) {
		
			super(new StackLayout(Orientation.VERTICAL, 3, 3, true));
		
			setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2), BorderFactory.createLineBorder(Color.GRAY, 1)));
			
			this.layer = l;
			
			JCheckBox check = new JCheckBox(figure.getName(layer), figure.isVisible(layer));
			check.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					
					if (e.getStateChange() == ItemEvent.DESELECTED) 
						figure.setVisible(layer, false);

					if (e.getStateChange() == ItemEvent.SELECTED) 
						figure.setVisible(layer, true);
					
					panel.repaint();
					
				}
			});
			
			add(check);
			
			JPanel spinners = new JPanel(new StackLayout(Orientation.HORIZONTAL, 2, 0, true));
			
			alpha = new JSlider(0, 100, 0);
			offsetX = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
			offsetY = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
			
			alpha.setValue(Math.round(figure.getTransparency(layer) * 100));
			
			Point2D p = figure.getOffset(layer);
			offsetX.setValue(p.getX());
			offsetY.setValue(p.getY());
			
			alpha.addChangeListener(new ChangeListener() {

				public void stateChanged(ChangeEvent e) {
					int v = alpha.getValue();
					
					figure.setTransparency(layer, (float)v/100f);
					panel.repaint();
				}
				
			});
			
			ChangeListener offsetListener = new ChangeListener() {

				public void stateChanged(ChangeEvent e) {
					double x = ((Number)offsetX.getValue()).doubleValue();
					double y = ((Number)offsetY.getValue()).doubleValue();
					figure.setOffset(layer, x, y);
					panel.repaint();
				}
				
				
				
			};
			
			offsetX.addChangeListener(offsetListener);
			offsetY.addChangeListener(offsetListener);
			
			add(alpha);
			spinners.add(offsetX);
			spinners.add(offsetY);
			
			add(spinners);
			
		}
		
		
		
		
	}
	
	private FigurePanel panel = new FigurePanel();

	private JToolBar menu = new JToolBar(JToolBar.HORIZONTAL);
	
	private AdvancedLayeredFigure figure;
	
	public AdvancedLayeredFigureViewer(Map<String, String> parameters, AdvancedLayeredFigure figure) {
		super("viewer.image", parameters.containsKey("title") ? parameters.get("title") : figure.getName());

		this.figure = figure;
		
		initGUI();
		
		panel.configure(parameters);
	}

	public AdvancedLayeredFigureViewer(AdvancedLayeredFigure figure) {
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

			layers.add(new LayerControl(figure.getFigure(i)));

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
		
		
		setSize(800, 600);
		
	}

}
