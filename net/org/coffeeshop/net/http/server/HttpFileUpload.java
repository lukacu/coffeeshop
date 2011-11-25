package org.coffeeshop.net.http.server;

import java.io.*;

public class HttpFileUpload
{
////////////////////////////////////////////////////////////////////////////////
// the different upload results
////////////////////////////////////////////////////////////////////////////////

    /**
     * the different results (Those come directly from PHP)<BR/>
     * RESULT_OK = Upload succeeded<BR/>
     * RESULT_SIZE = File is larger the size specified in HttpServerSettings<BR/>
     * RESULT_FORM_PARTIAL = File uploaded was only partial uploaded<BR/>
     * RESULT_NO_FILE = No file was uploaded<BR/>
     *
     */
    public final static int RESULT_OK = 0,
                            RESULT_SIZE = 1,
                            RESULT_PARTIAL = 3,
                            RESULT_NOFILE = 4;

////////////////////////////////////////////////////////////////////////////////
// data members
////////////////////////////////////////////////////////////////////////////////

    /** the original filename */
    String originalFilename;

    /** the temporary file (is null on error!) */
    File tempFile;

    /** the resultcode of the fileupload */
    int result;

////////////////////////////////////////////////////////////////////////////////
// construction / initialisation
////////////////////////////////////////////////////////////////////////////////

    /** the constructor */
    public HttpFileUpload(String originalFilename, File tempFile, int result )
    {
        this.originalFilename   = originalFilename;
        this.tempFile           = tempFile;
        this.result             = result;
    }

////////////////////////////////////////////////////////////////////////////////
// public methods
////////////////////////////////////////////////////////////////////////////////

    /** this method returns the original filename */
    public String getOriginalFilename()
    {
        return this.originalFilename;
    }

    /** this method returs the resultcode */
    public int getResult()
    {
        return this.result;
    }

    /** this method returns the temp File object (on success) */
    public File getTempFile()
    {
        return this.tempFile;
    }
}