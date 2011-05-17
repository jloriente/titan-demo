package com.moremr.teknikforetagen.data;

import java.io.IOException;

import android.content.Context;

import com.innerfunction.js.mobile.MVCFactory;
import com.innerfunction.js.mobile.ScriptResolver;
import org.mozilla.javascript.EvaluatorException;

/**
 * A factory class for producing instances of the NewsMVC interface.
 * The NewsMVC interface is implemented by news-feed-mvc.js.
 */
public class NewsMVCFactory extends MVCFactory<NewsMVC> {

    /**
     * Code for instantiating the MVC and wrapping it in a Java object.
     */
    static final String MAKE_JS =
        "Services.__makeMVC = function( view ) {"+
        "   return new com.moremr.teknikforetagen.data.NewsMVC( new NewsMVC( view, settings ) );"+
        "}";

    /** Create a new factory. */
    public NewsMVCFactory(ScriptResolver resolver, Context context) throws IOException, EvaluatorException {
        super( resolver, context);
        // Load MVC configuration settings.
        this.loadScript("com/moremr/teknikforetagen/data/settings.js");
        // Load the MVC definition.
        this.loadScript("com/moremr/teknikforetagen/data/news-feed-mvc.js");
        // Evaluate the script describing the make() function.
        this.env.eval( MAKE_JS, "<make-mvc>");
        // Add Android context to Services.
        try {
            this.env.push("Services");
            this.env.set("context", context );
        }
        finally {
            this.env.pop();
        }
    }

    /** Return a new MVC instance. */
    public NewsMVC getMVCInstance(Object view) {
        try {
            this.env.push("Services");
            return (NewsMVC)this.env.invoke("__makeMVC", view );
        }
        finally {
            this.env.pop();
        }
    }

}
