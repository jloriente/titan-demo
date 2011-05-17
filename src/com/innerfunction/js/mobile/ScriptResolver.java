package com.innerfunction.js.mobile;

import java.io.IOException;
import java.io.Reader;

import android.content.Context;

/**
 * Interface used to abstract the details of how to open a reader on a script file on a particular mobile platform.
 */
public interface ScriptResolver {

    /**
     * Return a reader on the contents of the file at the specified path.
     * Subclasses must return 
     * @throws IOException If the specified path can't be resolved for any reason.
     */
    public abstract Reader getReader(String path, Context context) throws IOException;

}
