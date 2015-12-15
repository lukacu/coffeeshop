package org.coffeeshop.swing;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.coffeeshop.string.parsers.ParseException;
import org.coffeeshop.string.parsers.StringParser;
import org.coffeeshop.swing.SettingsPanel.SettingsRenderer;
import org.coffeeshop.swing.SettingsPanel.ValueProxy;

public class KeyStrokeStringParser implements StringParser, SettingsRenderer {

	private static class KeyStrokeEditor extends JLabel {

		private static final long serialVersionUID = 1L;
		
		private ValueProxy value;
		
		private boolean recording = false;
		
		private KeyStrokeEditor(ValueProxy value) {
			super();
			
			//setA
			
			this.value = value;
			
			setHorizontalAlignment(SwingConstants.CENTER);
			
			setBorder(BorderFactory.createLineBorder(getForeground()));

			updateText();
			
			addMouseListener(new MouseAdapter() {
				
				@Override
				public void mouseClicked(MouseEvent e) {
					
					recording = true;
					
					requestFocusInWindow();
					
				}
				
			});
			
			addFocusListener(new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent arg0) {
					
					recording = false;
					
					updateText();
					
				}
				
				@Override
				public void focusGained(FocusEvent arg0) {
					
					recording = true;
					
					updateText();
				}
				
			});
			
			addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {
				}
				
				@Override
				public void keyReleased(KeyEvent e) {
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						
						recording = false;
					} else {
					
						if (e.getKeyCode() == KeyEvent.VK_CONTROL || e.getKeyCode() == KeyEvent.VK_ALT ||
								e.getKeyCode() == KeyEvent.VK_SHIFT || e.getKeyCode() == KeyEvent.VK_ALT_GRAPH) {
							
							
							
						} else {
						
							KeyStroke stroke = KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiersEx());
							
							KeyStrokeEditor.this.value.setValue(stroke.toString());
							
							recording = false;
						}
					}
					
					
					updateText();
				}
			});
			
			
		}
		
		protected void updateText() {
			
			if (recording) {
				setText("Press key to set shortcut ...");
				return;
			}
			
			KeyStroke stroke = null;
			
			try {
			
				stroke = (KeyStroke) getParser().parse(value.getValue());
			
			} catch (ParseException e) {
				
			}
			
			setText(stroke != null ? stroke.toString().replace("pressed ", "") : "unknown");
		}
		
		
	}
	
	private static final KeyStrokeStringParser INSTANCE = new KeyStrokeStringParser();	

    public static KeyStrokeStringParser getParser() {
		return INSTANCE;
	}
	
	@Override
	public Object parse(String arg) throws ParseException {
	
		KeyStroke stroke = KeyStroke.getKeyStroke(arg);

		if (stroke == null)
			throw new ParseException("Unable to parse keystroke");
		
		return stroke;
	}

	@Override
	public JComponent renderComponent(ValueProxy value) {

		return new KeyStrokeEditor(value);
	}

	@Override
	public void updateComponent(JComponent component) {
		
		if (component instanceof KeyStrokeEditor) {
		
			((KeyStrokeEditor) component).updateText();
			
		}
		
	}

}
