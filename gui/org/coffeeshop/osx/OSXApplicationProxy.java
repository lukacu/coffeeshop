package org.coffeeshop.osx;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class OSXApplicationProxy {

	public enum UserAttentionRequest {INFORMATION, CRITICAL}
	
	private static OSXApplicationProxy singelton = null;
	
	private Object application;
    private Class<?> applicationListenerClass;

    Map<OSXApplicationListener, Object> listenerMap = Collections.synchronizedMap(new HashMap<OSXApplicationListener, Object>());
    private boolean enabledAboutMenu = true;
    private boolean enabledPreferencesMenu;
    private boolean aboutMenuItemPresent = true;
    private boolean preferencesMenuItemPresent;

    public static OSXApplicationProxy getInstance() {
    	if (singelton == null)
    		singelton = new OSXApplicationProxy();
    	
    	return singelton;
    }
    
    private OSXApplicationProxy() {
        try {
            final File file = new File("/System/Library/Java");
            if(file.exists()) {
                ClassLoader scl = ClassLoader.getSystemClassLoader();
                Class<?> clc = scl.getClass();
                if(URLClassLoader.class.isAssignableFrom(clc)) {
                    Method addUrl  = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] {URL.class});
                    addUrl.setAccessible(true);
                    addUrl.invoke(scl, new Object[] {file.toURI().toURL()});
                }
            }

            Class<?> appClass = Class.forName("com.apple.eawt.Application");
            application = appClass.getMethod("getApplication", new Class[0]).invoke(null, new Object[0]);
            applicationListenerClass = Class.forName("com.apple.eawt.ApplicationListener");
        } catch (ClassNotFoundException e) {
            application = null;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean isMac() {
        return application != null;
    }

    public void addAboutMenuItem() {
        if (isMac()) {
            callMethod(application, "addAboutMenuItem");
        } else {
            this.aboutMenuItemPresent = true;
        }
    }

    public void addApplicationListener(OSXApplicationListener applicationListener) {

        if (!Modifier.isPublic(applicationListener.getClass().getModifiers())) {
            throw new IllegalArgumentException("OSXApplicationListener must be a public class");
        }
        if (isMac()) {
            Object listener = Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class[]{applicationListenerClass},
                    new ApplicationListenerInvocationHandler(applicationListener));

            callMethod(application, "addApplicationListener", new Class[]{applicationListenerClass}, new Object[]{listener});
            listenerMap.put(applicationListener, listener);
        } else {
            listenerMap.put(applicationListener, applicationListener);
        }
    }

    public void addPreferencesMenuItem() {
        if (isMac()) {
            callMethod("addPreferencesMenuItem");
        } else {
            this.preferencesMenuItemPresent = true;
        }
    }

    public boolean getEnabledAboutMenu() {
        if (isMac()) {
            return callMethod("getEnabledAboutMenu").equals(Boolean.TRUE);
        } else {
            return enabledAboutMenu;
        }
    }

    public boolean getEnabledPreferencesMenu() {
        if (isMac()) {
            Object result = callMethod("getEnabledPreferencesMenu");
            return result.equals(Boolean.TRUE);
        } else {
            return enabledPreferencesMenu;
        }
    }

    public Point getMouseLocationOnScreen() {
        if (isMac()) {
            try {
                Method method = application.getClass().getMethod("getMouseLocationOnScreen", new Class[0]);
                return (Point) method.invoke(null, new Object[0]);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new Point(0, 0);
        }
    }

    public boolean isAboutMenuItemPresent() {
        if (isMac()) {
            return callMethod("isAboutMenuItemPresent").equals(Boolean.TRUE);
        } else {
            return aboutMenuItemPresent;
        }
    }

    public boolean isPreferencesMenuItemPresent() {
        if (isMac()) {
            return callMethod("isPreferencesMenuItemPresent").equals(Boolean.TRUE);
        } else {
            return this.preferencesMenuItemPresent;
        }
    }

    public void removeAboutMenuItem() {
        if (isMac()) {
            callMethod("removeAboutMenuItem");
        } else {
            this.aboutMenuItemPresent = false;
        }
    }

    public synchronized void removeApplicationListener(OSXApplicationListener applicationListener) {
        if (isMac()) {
            Object listener = listenerMap.get(applicationListener);
            callMethod(application, "removeApplicationListener", new Class[]{applicationListenerClass}, new Object[]{listener});

        }
        listenerMap.remove(applicationListener);
    }

    public void removePreferencesMenuItem() {
        if (isMac()) {
            callMethod("removeAboutMenuItem");
        } else {
            this.preferencesMenuItemPresent = false;
        }
    }

    public void setEnabledAboutMenu(boolean enabled) {
        if (isMac()) {
            callMethod(application, "setEnabledAboutMenu", new Class[]{Boolean.TYPE}, new Object[]{Boolean.valueOf(enabled)});
        } else {
            this.enabledAboutMenu = enabled;
        }
    }

    public void setEnabledPreferencesMenu(boolean enabled) {
        if (isMac()) {
            callMethod(application, "setEnabledPreferencesMenu", new Class[]{Boolean.TYPE}, new Object[]{Boolean.valueOf(enabled)});
        } else {
            this.enabledPreferencesMenu = enabled;
        }

    }

    public int requestUserAttention(UserAttentionRequest type) {

        try {
            Object application = getNSApplication();
            Field critical = application.getClass().getField("UserAttentionRequestCritical");
            Field informational = application.getClass().getField("UserAttentionRequestInformational");
            Field actual = type == UserAttentionRequest.CRITICAL ? critical : informational;

            return ((Integer) application.getClass().getMethod("requestUserAttention", new Class[]{Integer.TYPE}).invoke(application, new Object[]{actual.get(null)})).intValue();

        } catch (ClassNotFoundException e) {
            return -1;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public void cancelUserAttentionRequest(int request) {
        try {
            Object application = getNSApplication();
            application.getClass().getMethod("cancelUserAttentionRequest", new Class[]{Integer.TYPE}).invoke(application, new Object[]{new Integer(request)});
        } catch (ClassNotFoundException e) {
            // Nada
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getNSApplication() throws ClassNotFoundException {
        try {
            Class<?> applicationClass = Class.forName("com.apple.cocoa.application.NSApplication");
            return applicationClass.getMethod("sharedApplication", new Class[0]).invoke(null, new Object[0]);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void setApplicationIconImage(BufferedImage image) {
        if (isMac()) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try {
                ImageIO.write(image, "png", stream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                Class<?> nsDataClass = Class.forName("com.apple.cocoa.foundation.NSData");
                Constructor<?> constructor = nsDataClass.getConstructor(new Class[]{new byte[0].getClass()});

                Object nsData = constructor.newInstance(new Object[]{stream.toByteArray()});

                Class<?> nsImageClass = Class.forName("com.apple.cocoa.application.NSImage");
                Object nsImage = nsImageClass.getConstructor(new Class[]{nsDataClass}).newInstance(new Object[]{nsData});

                Object application = getNSApplication();

                application.getClass().getMethod("setApplicationIconImage", new Class[] {nsImageClass}).invoke(application, new Object[] {nsImage});

            } catch (ClassNotFoundException e) {

            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public BufferedImage getApplicationIconImage() {
        if (isMac()) {


            try {
                Class<?> nsDataClass = Class.forName("com.apple.cocoa.foundation.NSData");
                Class<?> nsImageClass = Class.forName("com.apple.cocoa.application.NSImage");
                Object application = getNSApplication();
                Object nsImage = application.getClass().getMethod("applicationIconImage", new Class[0]).invoke(application, new Object[0]);

                Object nsData = nsImageClass.getMethod("TIFFRepresentation", new Class<?>[0]).invoke(nsImage, new Object[0]);

                Integer length = (Integer) nsDataClass.getMethod("length", new Class[0]).invoke(nsData, new Object[0]);
                byte[] bytes = (byte[]) nsDataClass.getMethod("bytes", new Class[]{Integer.TYPE, Integer.TYPE}).invoke(nsData, new Object[]{Integer.valueOf(0), length});

                BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
                return image;

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        return null;
    }

    private Object callMethod(String methodname) {
        return callMethod(application, methodname, new Class[0], new Object[0]);
    }

    private Object callMethod(Object object, String methodname) {
        return callMethod(object, methodname, new Class[0], new Object[0]);
    }

    private Object callMethod(Object object, String methodname, Class<?>[] classes, Object[] arguments) {
        try {
            if (classes == null) {
                classes = new Class[arguments.length];
                for (int i = 0; i < classes.length; i++) {
                    classes[i] = arguments[i].getClass();

                }
            }
            Method addListnerMethod = object.getClass().getMethod(methodname, classes);
            return addListnerMethod.invoke(object, arguments);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    class ApplicationListenerInvocationHandler implements InvocationHandler {
        private OSXApplicationListener applicationListener;

        ApplicationListenerInvocationHandler(OSXApplicationListener applicationListener) {
            this.applicationListener = applicationListener;
        }

        public Object invoke(Object object, Method appleMethod, Object[] objects) throws Throwable {

            OSXApplicationEvent event = createApplicationEvent(objects[0]);
            try {
                Method method = applicationListener.getClass().getMethod(appleMethod.getName(), new Class[]{OSXApplicationEvent.class});
                return method.invoke(applicationListener, new Object[]{event});
            } catch (NoSuchMethodException e) {
                if (appleMethod.getName().equals("equals") && objects.length == 1) {
                    return Boolean.valueOf(object == objects[0]);
                }
                return null;
            }
        }
    }

    private OSXApplicationEvent createApplicationEvent(final Object appleApplicationEvent) {
        return (OSXApplicationEvent) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{OSXApplicationEvent.class}, new InvocationHandler() {
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                return appleApplicationEvent.getClass().getMethod(method.getName(), method.getParameterTypes()).invoke(appleApplicationEvent, objects);
            }
        });
    }
}