package org.coffeeshop;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public class ReferenceCollection<T> extends AbstractCollection<T> {
    
	private static class StrongReference<T> extends WeakReference<T> {
	    private final T referent;

	    StrongReference(T referent) {
	        super(null);
	        this.referent = referent;
	    }

	    @Override
	    public T get() {
	        return referent;
	    }

	}
	
    private Vector<Reference<T>> items;

    public ReferenceCollection() {
        items = new Vector<Reference<T>>();
    }
    
    public ReferenceCollection(Collection<T> c) {
        items = new Vector<Reference<T>>();
        addAll(c);
    }
    
    /**
     * Add a strong reference of an object
     */
    public synchronized boolean add(T element) {
    	
    	Reference<T> ref = new StrongReference<T>(element);
    	
        return items.add(ref);
        
    }

    /**
     * Add a weak reference of an object
     * 
     * @param element
     * @return
     */
    public synchronized boolean addWeak(T element) {
    	
    	WeakReference<T> ref = new WeakReference<T>(element);
    	
        return items.add(ref);
        
    }
    
    public synchronized Iterator<T> iterator() {
    	ArrayList<T> fixed = new ArrayList<T>(items.size());
    	
        for (Iterator<Reference<T>> it = items.iterator(); it.hasNext(); ) {
        	Reference<T> ref = it.next();
            T obj = ref.get();
            if (obj == null) 
            	it.remove();
            else
            	fixed.add(obj);
        }
    	
        return fixed.iterator();
    }
    
    @Override
    public synchronized boolean remove(Object o) {
        for (Iterator<Reference<T>> it = items.iterator(); it.hasNext(); ) {
            T obj = it.next().get();
            if (obj == null) 
            	it.remove();
            else if (obj.equals(o)) {
            	it.remove();
            	return true;
            }
        }
        return false;
    }
    
    public synchronized int size() {
        removeReleased();
        return items.size();
    }    
    
    private void removeReleased() {
        for (Iterator<Reference<T>> it = items.iterator(); it.hasNext(); ) {
        	Reference<T> ref = it.next();
            if (ref.get() == null) items.remove(ref);
        }
    }
    
}
