/**
 * Based on code from Dr. Xi (http://www.xinotes.org/)
 * 
 * 
 */

package org.coffeeshop.string;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class IniProperties {
    private Properties globalProperties;
    private Map<String,Properties> properties;

    private enum ParseState {
        NORMAL,
        ESCAPE,
        ESC_CRNL,
        COMMENT
    }

    public IniProperties() {

    }

    public IniProperties(InputStream in) throws IOException {
        this();
        load(in);
    }
    
    public IniProperties(File file) throws IOException {
        this(new FileInputStream(file));
    }

    public IniProperties(String file) throws IOException {
        this(new FileInputStream(file));
    }
    
    /**
     * Load ini as properties from input stream.
     */
    public void load(InputStream in) throws IOException {
        globalProperties = new Properties();
        properties = new HashMap<String,Properties>();
    	
        int bufSize = 4096;
        byte[] buffer = new byte[bufSize];
        int n = in.read(buffer, 0, bufSize);

        ParseState state = ParseState.NORMAL;
        boolean section_open = false;
        String current_section = null;
        String key = null, value = null;
        StringBuilder sb = new StringBuilder();
        while (n >= 0) {
            for (int i = 0; i < n; i++) {
                char c = (char) buffer[i];

                if (state == ParseState.COMMENT) { // comment, skip to end of line
                    if ((c == '\r') ||(c == '\n')) {
                        state = ParseState.NORMAL;
                    }
                    else {
                        continue;
                    }
                }

                if (state == ParseState.ESCAPE) {
                    sb.append(c);
                    if (c == '\r') {
                        // if the EOL is \r\n, \ escapes both chars
                        state = ParseState.ESC_CRNL; 
                    }
                    else {
                        state = ParseState.NORMAL;
                    }
                    continue;
                }

                switch (c) {
                    case '[': // start section
                        sb = new StringBuilder();
                        section_open = true;
                        break;
                    
                    case ']': // end section
                        if (section_open) {
                            current_section = sb.toString().trim();
                            sb = new StringBuilder();
                            properties.put(current_section, new Properties());
                            section_open = false;
                        }
                        else {
                            sb.append(c);
                        }
                        break;

                    case '\\': // escape char, take the next char as is
                        state = ParseState.ESCAPE;
                        break;

                    case '#': 
                    case ';': 
                        state = ParseState.COMMENT;
                        break;

                    case '=': // assignment operator
                    case ':':
                        if (key == null) {
                            key = sb.toString().trim();
                            sb = new StringBuilder();
                        }
                        else {
                            sb.append(c);
                        }
                        break;

                    case '\r':
                    case '\n':
                        if ((state == ParseState.ESC_CRNL) && (c == '\n')) {
                            sb.append(c);
                            state = ParseState.NORMAL;
                        }
                        else {
                            if (sb.length() > 0) {
                                value = sb.toString().trim();
                                sb = new StringBuilder();
                        
                                if (key != null) {
                                    if (current_section == null) {
                                        this.setProperty(key, value);
                                    }
                                    else {
                                        this.setProperty(current_section, key, value);
                                    }
                                }
                            }
                            key = null;
                            value = null;
                        }
                        break;

                    default: 
                        sb.append(c);
                }
            }
            n = in.read(buffer, 0, bufSize);
        }
    }

    /**
     * Get global property by name.
     */
    public String getProperty(String name) {
        return globalProperties.getProperty(name);
    }

    /**
     * Set global property.
     */
    public void setProperty(String name, String value) {
        globalProperties.setProperty(name, value);
    }

    /**
     * Return iterator of global properties.
     */
    @SuppressWarnings("unchecked")
    public Iterator<String> properties() {
        return new IteratorFromEnumeration<String>(
                   (Enumeration<String>)globalProperties.propertyNames());
    }

    /**
     * Get property value for specified section and name. Returns null
     * if section or property does not exist.
     */
    public String getProperty(String section, String name) {
        Properties p = properties.get(section);
        return p == null ? null : p.getProperty(name);
    }

    /**
     * Set property value for specified section and name. Creates section
     * if not existing.
     */
    public void setProperty(String section, String name, String value) {
        Properties p = properties.get(section);
        if (p == null) {
            properties.put(section, new Properties());
        }
        p.setProperty(name, value);
    }

    /**
     * Return property iterator for specified section. Returns null if
     * specified section does not exist.
     */
    @SuppressWarnings("unchecked")
    public Iterator<String> properties(String section) {
        Properties p = properties.get(section);
        if (p == null) {
            return null;
        }
        return new IteratorFromEnumeration<String>(
                   (Enumeration<String>)p.propertyNames());
    }

    /**
     * Return iterator of names of section.
     */
    public Iterable<String> sections() {
        return properties.keySet();
    }

    /**
     * Dumps properties to output stream.
     */
    public void dump(PrintStream out) throws IOException {
        // Global properties
        Iterator<String> props = this.properties();
        while (props.hasNext()) {
            String name = props.next();
            out.printf("%s = %s\n", name, dumpEscape(getProperty(name)));
        }

        // sections
        Iterator<String> sections = this.sections().iterator();
        while (sections.hasNext()) {
            String section = sections.next();
            out.printf("\n[%s]\n", section);
            props = this.properties(section);
            while (props.hasNext()) {
                String name = props.next();
                out.printf("%s = %s\n", name, dumpEscape(getProperty(section, name)));
            }
        }
    }

    private static String dumpEscape(String s) {
        return s.replaceAll("\\\\", "\\\\\\\\")
                .replaceAll(";", "\\\\;")
                .replaceAll("#", "\\\\#")
                .replaceAll("(\r?\n|\r)", "\\\\$1");
    }

    // private class used to coerce Enumerator to Iterator.
    private static class IteratorFromEnumeration<E> implements Iterator<E> {
        private Enumeration<E> e;

        public IteratorFromEnumeration(Enumeration<E> e) {
            this.e = e;
        }

        public boolean hasNext() {
            return e.hasMoreElements();
        }

        public E next() {
            return e.nextElement();
        }

        public void remove() {
            throw new UnsupportedOperationException("Can't change underlying enumeration");
        }
    }

}
