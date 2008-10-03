
package org.coffeeshop.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JComponent;

/**
 * A panel widget that displays an image.
 * 
 * @author lukacu
 */
public class ImagePanel extends JComponent {

	/**
	 * Serialization...
	 */
	public static final long serialVersionUID = 1;
	
	private Image image = null;
	
	/**
	 * Construct a new component
	 *
	 */
	public ImagePanel() {
		super();
	}
	
	/**
	 * Construct new component with a given image
	 * 
	 * @param image
	 */
	public ImagePanel(Image image) {
		super();
		setImage(image);
	}	
	
	/**
	 * Get image
	 * 
	 * @return get image
	 */
	public Image getImage() {
		return image;
	}
	
	/**
	 * Set image
	 * 
	 * @param image new image
	 */
	public void setImage(Image image) {
		this.image = image;
		if (image != null)
			setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
		else setPreferredSize(new Dimension());
		
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(getBackground());

		Rectangle r = getBounds();
			
		// fill the background
		g.fillRect(r.x, r.y, r.width, r.height);

		if (image != null)
			g.drawImage(image, 0, 0, this);

	}
	
}
