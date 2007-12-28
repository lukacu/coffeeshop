/* THIS FILE IS A MEMBER OF THE COFFEESHOP LIBRARY
 * 
 * License:
 * 
 * Coffeeshop is a conglomerate of handy general purpose Java classes.  
 * 
 * Copyright (C) 2006-2008 Luka Cehovin
 * This library is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as 
 *  published by the Free Software Foundation; either version 2.1 of 
 *  the License, or (at your option) any later version.
 *  
 *  This library is distributed in the hope that it will be useful, 
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 *  GNU Lesser General Public License for more details. 
 *  
 *  http://www.opensource.org/licenses/lgpl-license.php
 *  
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the 
 *  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA 
 * 
 * 
 * This code is based on JSAP project from Martian Software, Inc.
 * http://www.martiansoftware.com/
 */

package org.coffeeshop.arguments;

import java.util.List;

import org.coffeeshop.string.parsers.ParseException;


/**
 * <p>Top-level abstraction of a <b>parameter</b>.  A <b>parameter</b> consists
 * of one or more <b>arguments</b> (command line tokens) that have a special
 * meaning when taken together.
 * For example, a command-line switch "-v" is a parameter consisting of a single
 * argument, whereas a command-line option "--file somefile.txt" is a parameter
 * consisting of two arguments.  Some parameters can be quite large, such as an
 * option for a file compression utility that allows you to specify any number
 * of files to comporess.</p>
 * 
 * <p>This is an abstract class.  See its subclasses {@link org.coffeeshop.arguments.Switch}, 
 * {@link org.coffeeshop.arguments.FlaggedOption}, and {@link org.coffeeshop.arguments.UnflaggedOption}
 * for details on the various types of parameters. Functionality common to all three
 * types of Parameters is described below.</p>
 * 
 * <p>Each parameter has a unique ID assigned in its constructor.  This ID is
 * used to retrieve values from the parser after the command line is parsed.
 * You  can set the ID to any String value you wish, although in general you'll
 * want them to be brief and descriptive to provide a degree of documentation.
 * A "-h" switch, for example, might have the ID "help".
 * The calling program can then determine if the user specified the -h switch on
 * the command line with the simple call <code>getBoolean("help")</code> against
 * the JSAPResult object produced by JSAP.parse().
 *
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see org.coffeeshop.arguments.Switch
 * @see org.coffeeshop.arguments.FlaggedOption
 * @see org.coffeeshop.arguments.UnflaggedOption
 * @see org.coffeeshop.arguments.ArgumentsResult#getBoolean(String)
 * @see org.coffeeshop.arguments.Arguments#parse(String[])
 */

public abstract class Parameter {

    /**
     * This parameter's unique ID.
     */
    private String id = null;

    /**
     * The name to use for this AbstractParameter when generating
     * usage information.  If null, the id will be used.
     */
    private String usageName = null;
    
    /**
     * If true, this parameter is "locked" (i.e., cannot be modified).
     * Parameters are locked when they are stored in a JSAPConfiguration.
     */
    private boolean locked = false;

    /**
     * The default values for this parameter.  Multiple default values
     * are permitted to accommodate parameters that allow lists or
     * multiple declarations.
     */
    private String[] defaultValue = null;

    /**
     * This parameter's help text.
     */
    private String help = null;

    /**
     * Creates a new Parameter.  Subclasses should call this
     * constructor.
     *
     * @param id   The ID for this argument.  All arguments MUST have
     * a unique ID.
     */
    public Parameter(String id) {
        this.id = id;
    }

    /**
     * Returns this parameter's unique ID.
     *
     * @return this parameter's unique ID.
     */
    public String getID() {
        return (id);
    }

    /**
     * Locks or unlocks this parameter.  Locked parameters cannot be
     * modified.  This is necessary because the JSAP object with
     * which parameters are registered performs certain validation
     * routines at the time of registration.  See
     * JSAP.registerParameter(Parameter) for more information.
     *
     * @param locked if <code>TRUE</code>, locks this parameter.  if
     * <code>FALSE</code>, unlocks it.
     * @see
     *    org.coffeeshop.arguments.Arguments#registerParameter(Parameter)
     */
    protected final void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Returns a boolean indicating whether this parameter is locked.
     *
     * @return a boolean indicating whether this parameter is currently
     * locked.
     */
    protected final boolean locked() {
        return (locked);
    }

    /**
     * Helper method that can be called by any methods that modify this
     * parameter (or its subclasses).
     * If the parameter is currently locked, an IllegalStateException
     * is thrown.
     */
    protected final void enforceParameterLock() {
        if (locked) {
            throw (
                new IllegalStateException(
                    "Parameter '" + getID() + "' may not be changed."));
        }
    }

    /**
     * Sets a default value for this parameter.  The default is specified
     * as a String, and is parsed as a single value specified on the
     * command line.  In other words, default values for "list"
     * parameters or parameters allowing multiple declarations should be
     * set using setDefault(String[]), as JSAP
     * would otherwise treat the entire list of values as a single value.
     *
     * @param defaultValue the default value for this parameter.
     */
    protected final void _setDefault(String defaultValue) {
        if (defaultValue == Arguments.NO_DEFAULT) {
            this.defaultValue = null;
        } else {
            this.defaultValue = new String[1];
            this.defaultValue[0] = defaultValue;
        }
    }

    /**
     * Sets one or more default values for this parameter.  This method
     * should be used whenever a parameter has more than one default
     * value.
     * @param defaultValues the default values for this parameter.
     */
    protected final void _setDefault(String[] defaultValues) {
        this.defaultValue = defaultValues;
    }

    /**
     * Adds a single default value to any currently defined for this
     * parameter.
     * @param defaultValue the default value to add to this parameter.
     */
    public final void addDefault(String defaultValue) {
        if (defaultValue != Arguments.NO_DEFAULT) {
            if (this.defaultValue == null) {
                this.defaultValue = new String[0];
            }
            int defaultValueCount = this.defaultValue.length + 1;
            String[] newDefaultValue = new String[defaultValueCount];
            for (int i = 0; i < defaultValueCount - 1; ++i) {
                newDefaultValue[i] = this.defaultValue[i];
            }
            newDefaultValue[defaultValueCount - 1] = defaultValue;
            this.defaultValue = newDefaultValue;
        }
    }

    /**
     * Sets the name of this AbstractParameter for the purposes of
     * usage information.  If null, the id will be used.
     * @param usageName the usage name for this AbstractParameter
     */
    protected final void _setUsageName(String usageName) {
    	this.usageName = usageName;
    }
    
    /**
     * Returns the name of this AbstractParameter for the purposes of
     * usage information.  The returned value is by default the id of
     * this parameter, but can be overridden via setUsageName(String).
     * @return the name of this AbstractParameter for the purposes of
     * usage information.
     */
    public final String getUsageName() {
    	return (usageName == null ? id : usageName);
    }
    
    /**
     * Returns an array of default values for this parameter, or
     * null if no default values have been defined.
     * @return an array of default values for this parameter, or
     * null if no default values have been defined.
     */
    public final String[] getDefault() {
        return (this.defaultValue);
    }

    /**
     * Returns an ArrayList of values resulting from the parsing
     * of the specified argument. Multiple values may be returned
     * if the arg is a list (such as a PATH or CLASSPATH variable),
     * or if the semantics of the parameter type otherwise represent
     * multiple values.
     *
     * @param   arg the argument to parse.
     * @return  a List of values resulting from the parse.
     * @throws  ParseException if the specified argument cannot be parsed.
     */
    protected abstract List<Object> parse(String arg) throws ParseException;

    /**
     * A convenience method for automatically generating syntax
     * information based upon a JSAP configuration.  A call to
     * JSAP.getSyntax() returns a String built by calling getSyntax() on
     * every parameter registered to a JSAP in the order in which they
     * were registered.  This method does not provide a help
     * description (see getHelp()), but rather the syntax for this
     * parameter.
     * @return usage information for this parameter.
     */
    public abstract String getSyntax();

    /**
     * Returns a description of the option's usage.
     * @return a textual description of this option's usage
     */
    public final String getHelp() {
        return (help == null ? "" : help);
    }

    /**
     * Sets the help text for this parameter.
     * @param help the help text for this parameter.
     */
    public final Parameter setHelp(String help) {
        this.help = help;
        return this;
    }
}
