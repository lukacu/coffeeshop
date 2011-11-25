package org.coffeeshop.net.http.server;


import java.util.*;

/**
 * This class remembers the information for a single Session. This means
 * remembering all session objects and remembering when the last accesstime
 * was.
 */

public class HttpSession
{
////////////////////////////////////////////////////////////////////////////////
// the data member
////////////////////////////////////////////////////////////////////////////////

    /** the last access time (System.currentTimeMillis()*/
    long lastAccessTime=0;

    /** all session variables */
    HashMap<String, Object> sessionVars = new HashMap<String, Object>();

	/** the number of session hits
	 * @since V1.03
	 */
	int hits=0;

////////////////////////////////////////////////////////////////////////////////
// construction / initialisation
////////////////////////////////////////////////////////////////////////////////

    /**
     * construction of the session object. This also sets the last
     * access time to NOW.
     */
    public HttpSession()
    {
        lastAccessTime = System.currentTimeMillis();
        hits = 1;
    }

////////////////////////////////////////////////////////////////////////////////
// session option
////////////////////////////////////////////////////////////////////////////////

    /**
     * this method returns the last access time in ms. since the Unix EPOCH.
     * @return the lastAccess time
     */
    long getLastAccessTime()
    {
        return lastAccessTime;
    }

    /**
     * this method sets the last access time. Only used internal by
     * the webserver.
     * @param value the new access time
     */
    void setLastAccessTime( long value )
    {
        lastAccessTime = value;
    }

    /**
     * call this method to notify a session access. It isn't required to
     * this manually, the webserver does it for you when a page is access by
     * the same user.
     */
    void access()
    {
        setLastAccessTime( System.currentTimeMillis() );
        hits++;
    }


    /**
     * this method checks if the session is expired
     * @param server the HttpServer object. (For the autosession timeout value).
     * @return true if this session has expired
     */
    public boolean isExpired(HttpServerInformation server) {
    	
        return (getLastAccessTime() + server.getSessionTimeout()*1000) < System.currentTimeMillis();
    }



////////////////////////////////////////////////////////////////////////////////
// other session retrieval
////////////////////////////////////////////////////////////////////////////////

    /**
     * this method returns a reference to the given session value
     * @param key the name of the session variable
     * @param def the default value to return if the given session doesn't exist
     * @return a reference to the session value or def if not found
     */
    public Object getValue( String key, Object def )
    {
        if( !sessionVars.containsKey(key) ) return def;
        return sessionVars.get(key);
    }

    /**
     * this method sets a session variable
     * @param key the name of the session variable
     * @param value the value of the session variable
     */
    public void setValue( String key, Object value )
    {
        sessionVars.put(key, value);
    }

	/**
	 * Returns the number of session hits there have been.
	 * @since V1.03
	 */
	public int getHits()
    {
        return hits;
    }
}