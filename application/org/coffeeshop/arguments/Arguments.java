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

import java.util.Map;
import java.util.List;
import java.util.Iterator;

import org.coffeeshop.string.StringUtils;
import org.coffeeshop.string.parsers.BigDecimalStringParser;
import org.coffeeshop.string.parsers.BigIntegerStringParser;
import org.coffeeshop.string.parsers.BooleanStringParser;
import org.coffeeshop.string.parsers.ByteStringParser;
import org.coffeeshop.string.parsers.CharacterStringParser;
import org.coffeeshop.string.parsers.ClassStringParser;
import org.coffeeshop.string.parsers.ColorStringParser;
import org.coffeeshop.string.parsers.DoubleStringParser;
import org.coffeeshop.string.parsers.FloatStringParser;
import org.coffeeshop.string.parsers.InetAddressStringParser;
import org.coffeeshop.string.parsers.IntSizeStringParser;
import org.coffeeshop.string.parsers.IntegerStringParser;
import org.coffeeshop.string.parsers.LongSizeStringParser;
import org.coffeeshop.string.parsers.LongStringParser;
import org.coffeeshop.string.parsers.PackageStringParser;
import org.coffeeshop.string.parsers.ShortStringParser;
import org.coffeeshop.string.parsers.StringStringParser;
import org.coffeeshop.string.parsers.URLStringParser;

/**
 * <p>The main class of CoffeeShop argument parser. The whole implementation
 * of this parser is based upon JSAP argument parser by 
 * <a href="http://www.martiansoftware.com/">Martian Software, Inc.</a></p>
 * 
 * <p>Argument parser is responsible for converting an array of Strings, typically
 * received from a  command line in the main class' main() method, into a
 * collection of Objects that are retrievable by a unique ID assigned by the
 * developer.</p>
 *
 * <p>Before the parser parses a command line, it is configured with the Switches,
 * FlaggedOptions, and UnflaggedOptions it will accept.  As a result, the
 * developer can rest assured that if no Exceptions are thrown by the 
 * parse() method, the entire command line was parsed successfully.</p>
 *
 * <p>For example, to parse a command line with the syntax "[--verbose]
 * {-n|--number} Mynumber", the following code could be used:</p.
 *
 * <code><pre>
 * Arguments myArg = new Arguments();
 * myArg.registerParameter( new Switch( "verboseSwitch", Arguments.NO_SHORTFLAG,
 * "verbose" ) );
 * myArg.registerParameter( new FlaggedOption( "numberOption", new
 * IntegerStringParser(), Arguments.NO_DEFAULT,
 * Arguments.NOT_REQUIRED, 'n', "number" ) );
 * ArgumentsResult result = myArg.parse(args);
 * </pre></code>
 *
 * <p>The results of the parse could then be obtained with:</p>
 *
 * <code><pre>
 * int n = result.getInt("numberOption");
 * boolean isVerbose = result.getBoolean("verboseSwitch");
 * </pre></code>
 *
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @author Klaus Berg (bug fixes in help generation)
 * @author Wolfram Esser (contributed code for custom line separators in help)
 * @author Luka Cehovin (embedding into CoffeeShop)
 * @see org.coffeeshop.arguments.ant.JSAPAntTask
 */
public class Arguments {

    /**
     * Map of AbstractParameters keyed on their unique ID.
     */
    private Map<String, Parameter> paramsByID = null;

    /**
     * Map of AbstractParameters keyed on their short flag.
     */
    private Map<Character, Parameter> paramsByShortFlag = null;

    /**
     * Map of AbstractParameters keyed on their long flag.
     */
    private Map<String, Parameter> paramsByLongFlag = null;

    /**
     * List of UnflaggedOptions, in order of declaration.
     */
    private List<UnflaggedOption> unflaggedOptions = null;

    /**
     * List of all of AbstractParameters, in order of
     * declaration.
     */
    private List<Parameter> paramsByDeclarationOrder = null;

    /**
     * List of all of DefaultSources, in order of declaration.
     */
    private List<DefaultSource> defaultSources = null;

    /**
     * If not null, overrides the automatic usage info.
     */
    private String usage = null;

    /**
     * If not null, overrides the automatic help info.
     */
    private String help = null;

    /**
     * Does not have a short flag.
     *
     * @see org.coffeeshop.arguments.FlaggedOption
     * @see org.coffeeshop.arguments.UnflaggedOption
     */
    public static final char NO_SHORTFLAG = '\0';

    /**
     * Does not have a long flag.
     *
     * @see org.coffeeshop.arguments.FlaggedOption
     * @see org.coffeeshop.arguments.UnflaggedOption
     */
    public static final String NO_LONGFLAG = null;

    /**
     * The default separator for list parameters (equivalent to
     * java.io.File.pathSeparatorChar)
     *
     * @see FlaggedOption#setListSeparator(char)
     */
    public static final char DEFAULT_LISTSEPARATOR =
        java.io.File.pathSeparatorChar;

    /**
     * The default separator between parameters in generated help (a newline
     * by default)
     */
    public static final String DEFAULT_PARAM_HELP_SEPARATOR = "\n";
    
    /**
     * The parameter is required.
     *
     * @see FlaggedOption#setRequired(boolean)
     */
    public static final boolean REQUIRED = true;

    /**
     * The parameter is not required.
     *
     * @see FlaggedOption#setRequired(boolean)
     */
    public static final boolean NOT_REQUIRED = false;

    /**
     * The parameter is a list.
     *
     * @see FlaggedOption#setList(boolean)
     */
    public static final boolean LIST = true;

    /**
     * The parameter is not a list.
     *
     * @see FlaggedOption#setList(boolean)
     */
    public static final boolean NOT_LIST = false;

    /**
     * The parameter allows multiple declarations.
     *
     * @see FlaggedOption#setAllowMultipleDeclarations(boolean)
     */
    public static final boolean MULTIPLEDECLARATIONS = true;

    /**
     * The parameter does not allow multiple declarations.
     *
     * @see FlaggedOption#setAllowMultipleDeclarations(boolean)
     */
    public static final boolean NO_MULTIPLEDECLARATIONS = false;

    /**
     * The parameter consumes the command line.
     *
     * @see org.coffeeshop.arguments.UnflaggedOption#setGreedy(boolean)
     */
    public static final boolean GREEDY = true;

    /**
     * The parameter does not consume the command line.
     *
     * @see org.coffeeshop.arguments.UnflaggedOption#setGreedy(boolean)
     */
    public static final boolean NOT_GREEDY = false;

    /** The parameter has no default value.
     */
    public static final String NO_DEFAULT = null;

    /**
     * The parameter has no help text.
     *
     * @see org.coffeeshop.arguments.Parameter#setHelp(String)
     */
    public static final String NO_HELP = null;

    /** 
     * The only instance of a {@link org.coffeeshop.string.parsers.BigDecimalStringParser}.
     */
    
    public static final BigDecimalStringParser BIGDECIMAL_PARSER = BigDecimalStringParser.getParser();
    
    /** 
     * The only instance of a {@link org.coffeeshop.string.parsers.BigIntegerStringParser}.
     */
    
    public static final BigIntegerStringParser BIGINTEGER_PARSER = BigIntegerStringParser.getParser();
    
    /** 
     * The only instance of a {@link org.coffeeshop.string.parsers.BooleanStringParser}.
     */
    
    public static final BooleanStringParser BOOLEAN_PARSER = BooleanStringParser.getParser();
    
    /** 
     * The only instance of a {@link org.coffeeshop.string.parsers.ByteStringParser}.
     */
    
    public static final ByteStringParser BYTE_PARSER = ByteStringParser.getParser();
    
    
    /** 
     * The only instance of a {@link org.coffeeshop.string.parsers.CharacterStringParser}.
     */
    
    public static final CharacterStringParser CHARACTER_PARSER = CharacterStringParser.getParser();
    
    /** 
     * The only instance of a {@link org.coffeeshop.string.parsers.ClassStringParser}.
     */
    
    public static final ClassStringParser CLASS_PARSER = ClassStringParser.getParser();

    
    /** 
     * The only instance of a {@link org.coffeeshop.string.parsers.ColorStringParser}.
     */
    
    public static final ColorStringParser COLOR_PARSER = ColorStringParser.getParser();

    
    /** 
     * The only instance of a {@link org.coffeeshop.string.parsers.DoubleStringParser}.
     */
    
    public static final DoubleStringParser DOUBLE_PARSER = DoubleStringParser.getParser();

    
    /** 
     * The only instance of a {@link org.coffeeshop.string.parsers.FloatStringParser}.
     */
    
    public static final FloatStringParser FLOAT_PARSER = FloatStringParser.getParser();

    /** 
     * The only instance of a {@link org.coffeeshop.string.parsers.InetAddressStringParser}.
     */
    
    public static final InetAddressStringParser INETADDRESS_PARSER = InetAddressStringParser.getParser();

    /** 
     * The only instance of a {@link org.coffeeshop.string.parsers.IntegerStringParser}.
     */
    
    public static final IntegerStringParser INTEGER_PARSER = IntegerStringParser.getParser();

    /** 
     * The only instance of a {@link org.coffeeshop.string.parsers.IntSizeStringParser}.
     */
    
    public static final IntSizeStringParser INTSIZE_PARSER = IntSizeStringParser.getParser();

    /** 
     * The only instance of a {@link org.coffeeshop.string.parsers.LongSizeStringParser}.
     */
    
    public static final LongSizeStringParser LONGSIZE_PARSER = LongSizeStringParser.getParser();

    /** 
     * The only instance of a {@link org.coffeeshop.string.parsers.LongStringParser}.
     */
    
    public static final LongStringParser LONG_PARSER = LongStringParser.getParser();

    /** 
     * The only instance of a {@link org.coffeeshop.string.parsers.PackageStringParser}.
     */
    
    public static final PackageStringParser PACKAGE_PARSER = PackageStringParser.getParser();

    /** 
     * The only instance of a {@link org.coffeeshop.string.parsers.ShortStringParser}.
     */
    
    public static final ShortStringParser SHORT_PARSER = ShortStringParser.getParser();

    /** 
     * The only instance of a {@link org.coffeeshop.string.parsers.StringStringParser}.
     */
    
    public static final StringStringParser STRING_PARSER = StringStringParser.getParser();

    /** 
     * The only instance of a {@link org.coffeeshop.string.parsers.URLStringParser}.
     */
    
    public static final URLStringParser URL_PARSER = URLStringParser.getParser();

    /**
     * The default screen width used for formatting help.
     */
    public static final int DEFAULT_SCREENWIDTH = 80;

    /**
     * Temporary fix for bad console encodings screwing up non-breaking spaces.
     */
    static char SYNTAX_SPACECHAR = ' ';
    
    static {
    	if (Boolean.valueOf(System.getProperty("org.coffeeshop.arguments.usenbsp", "false")).booleanValue()) {
    		SYNTAX_SPACECHAR = '\u00a0';
    	}
    }
    
    /**
     * Creates a new JSAP with an empty configuration.  It must be configured
     * with registerParameter() before its parse() methods may be called.
     */
    public Arguments() {
    	init();
    }
        
    private void init() {
        paramsByID = new java.util.HashMap<String, Parameter>();
        paramsByShortFlag = new java.util.HashMap<Character, Parameter>();
        paramsByLongFlag = new java.util.HashMap<String, Parameter>();
        unflaggedOptions = new java.util.ArrayList<UnflaggedOption>();
        paramsByDeclarationOrder = new java.util.ArrayList<Parameter>();
        defaultSources = new java.util.ArrayList<DefaultSource>();    	
    }
    
    /**
     * Sets the usage string manually, overriding the automatically-
     * generated String.  To remove the override, call setUsage(null).
     * @param usage the manually-set usage string.
     */
    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     * Sets the help string manually, overriding the automatically-
     * generated String.  To remove the override, call setHelp(null).
     * @param help the manualy-set help string.
     */
    public void setHelp(String help) {
        this.help = help;
    }

    /**
     * A shortcut method for calling getHelp(80, "\n").
     * @see #getHelp(int,String)
     * @return the same as gethelp(80, "\n")
     */
    public String getHelp() {
        return (getHelp(DEFAULT_SCREENWIDTH, DEFAULT_PARAM_HELP_SEPARATOR));
    }

    /**
     * A shortcut method for calling getHelp(screenWidth, "\n").
     * @param screenWidth the screen width for which to format the help.
     * @see #getHelp(int,String)
     * @return the same as gethelp(screenWidth, "\n")
     */
    public String getHelp(int screenWidth) {
    	return (getHelp(screenWidth, DEFAULT_PARAM_HELP_SEPARATOR));
    }
    
    /**
     * If the help text has been manually set, this method simply
     * returns it, ignoring the screenWidth parameter.  Otherwise,
     * an automatically-formatted help message is returned, tailored
     * to the specified screen width.
     * @param screenWidth the screen width (in characters) for which
     * the help text will be formatted.  If zero, help will not be
     * line-wrapped.
     * @return complete help text for this Arguments object.
     */
    public String getHelp(int screenWidth, String paramSeparator) {
        String result = help;
        if (result == null) {
            StringBuffer buf = new StringBuffer();

            // We'll wrap at screenWidth - 8
            int wrapWidth = screenWidth - 8;

            // now loop through all the params again and display their help info
            for (Iterator<Parameter> i = paramsByDeclarationOrder.iterator();
                i.hasNext();) {
                Parameter param = (Parameter) i.next();
                StringBuffer defaultText = new StringBuffer();
                String[] defaultValue = param.getDefault();
                if ( !(param instanceof Switch) && defaultValue != null ) {
                    defaultText.append(" (default: ");
                    for(int j = 0; j < defaultValue.length; j++ ) {
                        if (j > 0) defaultText.append( ", " );
                        defaultText.append(defaultValue[ j ]);
                    }
                    defaultText.append(")");
                }
                Iterator<String> helpInfo =
                    StringUtils
                        .wrapToList(param.getHelp() + defaultText, wrapWidth)
                        .iterator();

                buf.append("  "); // the two leading spaces
                buf.append(param.getSyntax());
                buf.append("\n");

                while (helpInfo.hasNext()) {
                    buf.append("        ");
                    buf.append( helpInfo.next() );
                    buf.append("\n");
                }
                if (i.hasNext()) {
                    buf.append(paramSeparator);
                }
            }
            result = buf.toString();
        }
        return (result);
    }

    /**
     * Returns an automatically generated usage description based upon current configuration.
     *
     * @return an automatically generated usage description based upon current configuration.
     */
    public String getUsage() {
        String result = usage;
        if (result == null) {
            StringBuffer buf = new StringBuffer();
            for (Iterator<Parameter> i = paramsByDeclarationOrder.iterator();
                i.hasNext();) {
                Parameter param = (Parameter) i.next();
                if (buf.length() > 0) {
                    buf.append(" ");
                }
                buf.append(param.getSyntax());
            }
            result = buf.toString();
        }
        return (result);
    }

    /**
     * Returns an automatically generated usage description based upon current configuration.
     * This returns exactly the same result as getUsage().
     *
     * @return an automatically generated usage description based upon current configuration.
     */
    public String toString() {
        return (getUsage());
    }

    /**
     * Returns an IDMap associating long and short flags with their associated
     * parameters' IDs, and allowing the listing of IDs.  This is probably only
     * useful for developers creating their own DefaultSource classes.
     * @return an IDMap based upon current configuration.
     */
    public IDMap getIDMap() {
        List<String> ids = new java.util.ArrayList<String>(paramsByDeclarationOrder.size());
        for (Iterator<Parameter> i = paramsByDeclarationOrder.iterator(); i.hasNext();) {
            Parameter param = (Parameter) i.next();
            ids.add(param.getID());
        }

        Map<Character, String> byShortFlag = new java.util.HashMap<Character, String>();
        for (Iterator<Character> i = paramsByShortFlag.keySet().iterator();
          i.hasNext();) {
            Character c = (Character) i.next();
            byShortFlag.put(
                c,
                ((Parameter) paramsByShortFlag.get(c)).getID());
        }

        Map<String, String> byLongFlag = new java.util.HashMap<String, String>();
        for (Iterator<String> i = paramsByLongFlag.keySet().iterator(); i.hasNext();) {
            String s = (String) i.next();
            byLongFlag.put(
                s,
                ((Parameter) paramsByLongFlag.get(s)).getID());
        }

        return (new IDMap(ids, byShortFlag, byLongFlag));
    }

    /**
     * Returns the requested Switch, FlaggedOption, or UnflaggedOption with the
     * specified ID.  Depending upon what you intend to do with the result, it
     * may be necessary to re-cast the result as a Switch, FlaggedOption, or
     * UnflaggedOption as appropriate.
     *
     * @param id the ID of the requested Switch, FlaggedOption, or
     * UnflaggedOption.
     * @return the requested Switch, FlaggedOption, or UnflaggedOption, or null
     * if no Parameter with the specified ID is defined in this parser.
     */
    public Parameter getByID(String id) {
        return ((Parameter) paramsByID.get(id));
    }

    /**
     * Returns the requested Switch or FlaggedOption with the specified long
     * flag. Depending upon what you intend to do with the result, it may be
     * necessary to re-cast the result as a Switch or FlaggedOption as
     * appropriate.
     *
     * @param longFlag the long flag of the requested Switch or FlaggedOption.
     * @return the requested Switch or FlaggedOption, or null if no Flagged
     * object with the specified long flag is defined in this parser.
     */
    public Flagged getByLongFlag(String longFlag) {
        return ((Flagged) paramsByLongFlag.get(longFlag));
    }

    /**
     * Returns the requested Switch or FlaggedOption with the specified short
     * flag. Depending upon what you intend to do with the result, it may be
     * necessary to re-cast the result as a Switch or FlaggedOption as
     * appropriate.
     *
     * @param shortFlag the short flag of the requested Switch or FlaggedOption.
     * @return the requested Switch or FlaggedOption, or null if no Flagged
     * object with the specified short flag is defined in this parser.
     */
    public Flagged getByShortFlag(Character shortFlag) {
        return ((Flagged) paramsByShortFlag.get(shortFlag));
    }

    /**
     * Returns the requested Switch or FlaggedOption with the specified short
     * flag. Depending upon what you intend to do with the result, it may be
     * necessary to re-cast the result as a Switch or FlaggedOption as
     * appropriate.
     *
     * @param shortFlag the short flag of the requested Switch or FlaggedOption.
     * @return the requested Switch or FlaggedOption, or null if no Flagged
     * object with the specified short flag is defined in this parser.
     */
    public Flagged getByShortFlag(char shortFlag) {
        return (getByShortFlag(new Character(shortFlag)));
    }

    /**
     * Returns an Iterator over all UnflaggedOptions currently registered with
     * this parser.
     *
     * @return an Iterator over all UnflaggedOptions currently registered with
     * this parser.
     * @see java.util.Iterator
     */
    public Iterator<UnflaggedOption> getUnflaggedOptionsIterator() {
        return (unflaggedOptions.iterator());
    }

    /**
     * Registers a new DefaultSource with this parser, at the end of the current
     * DefaultSource chain, but before the defaults defined within the
     * AbstractParameters themselves.
     *
     * @param ds the DefaultSource to append to the DefaultSource chain.
     * @see org.coffeeshop.arguments.DefaultSource
     */
    public void registerDefaultSource(DefaultSource ds) {
        defaultSources.add(ds);
    }

    /**
     * Removes the specified DefaultSource from this JSAP's DefaultSource chain.
     * If this specified DefaultSource is not currently in this JSAP's
     * DefaultSource chain, this method does nothing.
     *
     * @param ds the DefaultSource to remove from the DefaultSource chain.
     */
    public void unregisterDefaultSource(DefaultSource ds) {
        defaultSources.remove(ds);
    }

    /**
     * Returns a Defaults object representing the default values defined within
     * this JSAP's AbstractParameters themselves.
     *
     * @return a Defaults object representing the default values defined within
     * this JSAP's AbstractParameters themselves.
     */
    private Defaults getSystemDefaults() {
        Defaults defaults = new Defaults();
        for (Iterator<Parameter> i = paramsByDeclarationOrder.iterator(); i.hasNext();) {
            Parameter param = (Parameter) i.next();
            defaults.setDefault(param.getID(), param.getDefault());
        }
        return (defaults);
    }

    /**
     * Merges the specified Defaults objects, only copying Default values from
     * the source to the destination if they are NOT currently defined in the
     * destination.
     *
     * @param dest the destination Defaults object into which the source should
     * be merged.
     * @param src the source Defaults object.
     */
    private void combineDefaults(Defaults dest, Defaults src) {
        if (src != null) {
            for (Iterator<String> i = src.idIterator(); i.hasNext();) {
                String paramID = (String) i.next();
                dest.setDefaultIfNeeded(paramID, src.getDefault(paramID));
            }
        }
    }

    /**
     * Returns a Defaults object representing the merged Defaults of every
     * DefaultSource in the DefaultSource chain and the default values specified
     * in the AbstractParameters themselves.
     *
     * @param exceptionMap the ExceptionMap object within which any encountered
     * exceptions will be returned.
     * @return a Defaults object representing the Defaults of the entire JSAP.
     * @see org.coffeeshop.arguments.DefaultSource#getDefaults(IDMap, ExceptionMap)
     */
    protected Defaults getDefaults(ExceptionMap exceptionMap) {
        Defaults defaults = new Defaults();
        IDMap idMap = getIDMap();
        for (Iterator<DefaultSource> dsi = defaultSources.iterator(); dsi.hasNext();) {
            DefaultSource ds = (DefaultSource) dsi.next();
            combineDefaults(defaults, ds.getDefaults(idMap, exceptionMap));
        }
        combineDefaults(defaults, getSystemDefaults());
        return (defaults);
    }

    /**
     * Registers the specified Parameter (i.e., Switch, FlaggedOption,
     * or UnflaggedOption) with this JSAP.
     *
     * <p>Registering an Parameter <b>locks</b> the parameter.
     * Attempting to change its properties (ID, flags, etc.) while it is locked
     * will result in a JSAPException.  To unlock an Parameter, it must
     * be unregistered from the JSAP.
     *
     * @param param the Parameter to register.
     * @throws ArgumentsException if this Parameter cannot be added. Possible
     * reasons include:
     * <ul>
     *     <li>Another Parameter with the same ID has already been
     *      registered.</li>
     *  <li>You are attempting to register a Switch or FlaggedOption with
     *      neither a short nor long flag.</li>
     *  <li>You are attempting to register a Switch or FlaggedOption with a long
     *      or short flag that is already
     *  defined in this JSAP.</li>
     *  <li>You are attempting to register a second greedy UnflaggedOption</li>
     * </ul>
     */
    public void registerParameter(Parameter param)
        throws ArgumentsException {
        String paramID = param.getID();

        if (paramsByID.containsKey(paramID)) {
            throw (
                new ArgumentsException(
                    "A parameter with ID '"
                        + paramID
                        + "' has already been registered."));
        }

        if (param instanceof Flagged) {
            Flagged f = (Flagged) param;
            if ((f.getShortFlagCharacter() == null)
                && (f.getLongFlag() == null)) {
                throw (
                    new ArgumentsException(
                        "FlaggedOption '"
                            + paramID
                            + "' has no flags defined."));
            }
            if (paramsByShortFlag.containsKey(f.getShortFlagCharacter())) {
                throw (
                    new ArgumentsException(
                        "A parameter with short flag '"
                            + f.getShortFlag()
                            + "' has already been registered."));
            }
            if (paramsByLongFlag.containsKey(f.getLongFlag())) {
                throw (
                    new ArgumentsException(
                        "A parameter with long flag '"
                            + f.getLongFlag()
                            + "' has already been registered."));
            }
        } else {
            if ((unflaggedOptions.size() > 0)
                && (((UnflaggedOption) unflaggedOptions
                    .get(unflaggedOptions.size() - 1))
                    .isGreedy())) {
                throw (
                    new ArgumentsException(
                        "A greedy unflagged option has already been registered;"
                            + " option '"
                            + paramID
                            + "' will never be reached."));
            }
        }

        // if we got this far, it's safe to insert it.
        param.setLocked(true);
        paramsByID.put(paramID, param);
        paramsByDeclarationOrder.add(param);
        if (param instanceof Flagged) {
            Flagged f = (Flagged) param;
            if (f.getShortFlagCharacter() != null) {
                paramsByShortFlag.put(f.getShortFlagCharacter(), param);
            }
            if (f.getLongFlag() != null) {
                paramsByLongFlag.put(f.getLongFlag(), param);
            }
        } else if (param instanceof UnflaggedOption) {
            unflaggedOptions.add((UnflaggedOption)param);
        }
    }

    /**
     * Unregisters the specified Parameter (i.e., Switch, FlaggedOption,
     * or UnflaggedOption) from this JSAP.  Unregistering an Parameter
     * also unlocks it, allowing changes to its properties (ID, flags, etc.).
     *
     * @param param the Parameter to unregister from this JSAP.
     */
    public void unregisterParameter(Parameter param) {
        if (paramsByID.containsKey(param.getID())) {
        	
            paramsByID.remove(param.getID());
            paramsByDeclarationOrder.remove(param);
            if (param instanceof Flagged) {
                Flagged f = (Flagged) param;
                paramsByShortFlag.remove(f.getShortFlagCharacter());
                paramsByLongFlag.remove(f.getLongFlag());
            } else if (param instanceof UnflaggedOption) {
                unflaggedOptions.remove(param);
            }
            param.setLocked(false);
        }
    }

    /**
     * Parses the specified command line array.  If no Exception is thrown, the
     * entire command line has been parsed successfully, and its results have
     * been successfully instantiated.
     *
     * @param args An array of command line arguments to parse.  This array is
     * typically provided in the application's main class' main() method.
     * @return a JSAPResult containing the resulting Objects.
     */
    public ArgumentsResult parse(String[] args) {
        Parser p = new Parser(this, args);
        return (p.parse());
    }

    /**
     * Parses the specified command line.  The specified command line is first
     * parsed into an array, much like the operating system does for the JVM
     * prior to calling your application's main class' main() method.  If no
     * Exception is thrown, the entire command line has been parsed
     * successfully, and its results have been successfully instantiated.
     *
     * @param cmdLine An array of command line arguments to parse.  This array
     * is typically provided in the application's main class' main() method.
     * @return a JSAPResult containing the resulting Objects.
     */
    public ArgumentsResult parse(String cmdLine) {
        String[] args = CommandLineTokenizer.tokenize(cmdLine);
        return (parse(args));
    }

    /**
     * Unregisters all registered AbstractParameters, allowing them to perform
     * their cleanup.
     */
    public void finalize() {
        Parameter[] params =
            (Parameter[]) paramsByDeclarationOrder.toArray(
                new Parameter[0]);
        int paramCount = params.length;
        for (int i = 0; i < paramCount; ++i) {
            unregisterParameter(params[i]);
        }
    }

}
