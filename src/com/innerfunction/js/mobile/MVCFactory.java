package com.innerfunction.js.mobile;

import java.io.IOException;

import android.content.Context;

import com.innerfunction.js.JSEnv;
import org.mozilla.javascript.EvaluatorException;

/**
 * A factory class for producing JS defined MVC controller instances.
 */
public abstract class MVCFactory<T> {

    /** The JS runtime environment for the MVC. */
    protected final JSEnv env = new JSEnv();
    /** Object used to resolve scripts on the platform hosting this code. */
    protected ScriptResolver resolver;
    private Context context;

    /**
     * Instantiate a new factory.
     * @param resolver An object used to resolve script files.
     * @throws IOException 
     * @throws EvaluatorException 
     * @throws IOException If an error occurs when loading script resources.
     * @throws EvalutatorException If an error occurs when evaluating script resources.
     */
	public MVCFactory(ScriptResolver resolver, Context context) throws EvaluatorException, IOException {
		this.context = context;
        loadScript("com/innerfunction/js/mobile/services.js");
	}

	/**
     * Load a script from the specified location and evaluate it in the environment.
     * @throws IOException If an error occurs when loading the script.
     * @throws EvalutatorException If an error occurs when evaluating the script.
     */
    public void loadScript(String path) throws IOException, EvaluatorException {
        this.env.eval( resolver.getReader( path, this.context ), path );
    }

    /**
     * Return an MVC instance.
     * @param view The view component to be associated with the MVC.
     */
    public abstract T getMVCInstance(Object view);

}

