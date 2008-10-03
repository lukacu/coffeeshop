package org.coffeeshop.osx;

public interface OSXApplicationEvent {
    public String getFilename();
    public boolean isHandled();
    public void setHandled(boolean handled);
    public Object getSource();
    public String toString();
}