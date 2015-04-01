package org.coffeeshop.swing.viewers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.RenderedImage;

import javax.swing.JToolBar;

import org.coffeeshop.swing.ImageStore;
import org.coffeeshop.swing.PersistentWindow;
import org.coffeeshop.swing.ToolTipAction;
import org.coffeeshop.swing.figure.Figure;
import org.coffeeshop.swing.figure.FigurePanel;
import org.coffeeshop.swing.figure.RenderedImageProvider;

public class FigureViewer extends PersistentWindow {

	private static final long serialVersionUID = 1L;

	private ToolTipAction zoomNormal = new ToolTipAction("Zoom 100%", "zoom-normal") {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent arg0) {

			if (panel != null)
				panel.setZoom(1);

		}

	};

	private ToolTipAction zoomIn = new ToolTipAction("Zoom in", "zoom-in") {

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
	
	private ToolTipAction zoomOut = new ToolTipAction("Zoom out", "zoom-out") {

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
	
	private FigurePanel panel = new FigurePanel();

	private JToolBar menu = new JToolBar(JToolBar.HORIZONTAL);
	
	public FigureViewer(String title, RenderedImage image) {
		super("viewer.image", title);

		initGUI();
		
		panel.setFigure(new RenderedImageFigure(image));
	}

	public FigureViewer(String title, Figure image) {
		super("viewer.image", title);

		initGUI();
		
		panel.setFigure(image);
	}
	
	public FigureViewer(String title, RenderedImageProvider image) {
		super("viewer.image", title);

		initGUI();
		
		panel.setFigure(new RenderedImageFigure(image.getImage()));
	}
	
	private void initGUI() {

		setLayout(new BorderLayout());

		setIconImage(ImageStore.getImage("viewer-icon", "icon"));

		menu.setFloatable(false);

		add(menu, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);

		panel.setMinimumSize(new Dimension(100, 100));
		
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
