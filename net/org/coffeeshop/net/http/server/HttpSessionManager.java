package org.coffeeshop.net.http.server;


import java.util.*;

import org.coffeeshop.string.Cryptography;

/**
 * This class manages sessions. The class automaticly cleans up the sessions
 * if expired and makes it possible to retrieve the sessions for a given user.
 * @author Rick Blommers
 */
public class HttpSessionManager
{
////////////////////////////////////////////////////////////////////////////////
// data members
////////////////////////////////////////////////////////////////////////////////

    /**
     * this hashmap contains all 'HttpSession' objects
     */
    private HashMap<String, HttpSession> sessions = new HashMap<String, HttpSession>();

    /**
     * the last given session id
     */
    private long sessionCounter=0;

    /**
     * the server var. Just for administration purposes
     */
    private HttpServerInformation server;

    /**
     * the sesion garbage collector. Yep I had to create my own one.
     */
    private SessionGarbageCollector gc;

////////////////////////////////////////////////////////////////////////////////
// helping functions
////////////////////////////////////////////////////////////////////////////////

    /**
     * the default constructor. Only used internally by the webserer
     * @param server the server object this session manager belong to
     */
    public HttpSessionManager(HttpServerInformation server)
    {
        this.server = server;
        gc = new SessionGarbageCollector();
        gc.start();
    }


    /**
     * this method generations unique session id's
     * @todo not yet very secure, we need to use MD5 and a more randomness, to make it harder
     *      to guess the sessionid
     * @return the unique session id
     * @since V1.01 added better security with the help of MD5
     */
    public String generateNewSessionId()
    {
        // the result
        StringBuffer result = new StringBuffer(32);

        // increase the session counter
        sessionCounter++;


        // append the date/time
        //result.append(System.currentTimeMillis());

        // generate random part
        result.append( Cryptography.generateRandom32() ) ;

        // append the unique id (this is required, only using MD5 isn't good
        // enought, we don't have the garantee every MD5 is unique... And we
        // don't want to mix up sessions!
        result.append(Long.toHexString(sessionCounter));

        return result.toString();
    }

////////////////////////////////////////////////////////////////////////////////
// public functions
////////////////////////////////////////////////////////////////////////////////

    /**
     * this method remove a given session.
     * @param session the session to destroy
     */
    public void destroySession( HttpSession session )
    {
        sessions.remove(session);
    }


	/**
	 * this method is called to load the sessions for the given request.
	 * If the client does't have a session a new one is created.
	 * <br/:>
	 * This method is used internally by the webserver.
	 * @param request the request object
	 * @param response the reponse object
	 * @return null if autosessions are disabled, else the HttpSession object
	 *      is returned.
	 */
	public HttpSession loadSessionVars( HttpRequest request, HttpResponse response )
	{
		// auto session enabled ??
		if( server.isSessionAutoStart() )
		{
			// this is the session we need to return
			HttpSession session=null;

			// first try to find the given cookie
			boolean found=false;
			String id = request.getCookie(server.getSessionKeyName(),"");
			if( !id.equals("") )
			{
				// check if the cookie still exists
				synchronized( sessions )
				{
					// check if the session contains the key
					if( sessions.containsKey(id) )
					{
						// check for timeout
						session = (HttpSession)sessions.get(id);
						if( !session.isExpired(server) )
						{
							session.access();
							found = true;
						}
						else sessions.remove(session);
					}
				}
			}
			// not found ? Create the session
			if( !found )
			{
				synchronized( sessions )
				{
					// create the session object
					session = new HttpSession();
					id = generateNewSessionId();
					sessions.put(id,session);
					response.setCookie(server.getSessionKeyName(),id);
				}
			}
			return session;
		}
		return null;
	}

////////////////////////////////////////////////////////////////////////////////
// Session garbage collector
////////////////////////////////////////////////////////////////////////////////


    /**
     * this thread runs every x-seconds to check if sessions have expired, if
     * they have they are removed. A kind of garbage collector ;)
     */
    class SessionGarbageCollector extends Thread
    {
        /** constuction */
        public SessionGarbageCollector()
        {
            super();
            try
            {
                this.setDaemon(true);
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }
        }
        /** the main 'check' loop. Garbage collects every 30 seconds */
        public void run()
        {
            try
            {
                while( !interrupted() )
                {
                    sleep(30000);
                    synchronized( sessions )
                    {
                        // check ALL sessions
                        Iterator<String> i = sessions.keySet().iterator();
                        while( i.hasNext() )
                        {
                            // get the session
                            String key = (String)i.next();
                            HttpSession session = (HttpSession)sessions.get(key);

                            // timeout happend
                            if( session.isExpired(server) )
                            {
                                i.remove(); // Fixed memory leak, thanks Gabriel Klein!
                            }
                        }
                    }
                }
            }
            catch( InterruptedException e )
            {
                interrupt();
            }
        }
    }

}