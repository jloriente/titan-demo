package com.innerfunction.js;

import java.io.IOException;
import java.io.Reader;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * A class for representing JS evaluation environments.
 */
public class JSEnv extends JSObject {

    /**
     * Create a standard environment.
     * Initializes the scope with the standard objects.
     */
    public JSEnv() {
        super();
        Context cx = Context.enter();
        try {
            this.obj = cx.initStandardObjects();
        }
        finally {
            cx.exit();
        }
        this.env = this;
    }

    /** Create a new environment using the specified object as the scope. */
    public JSEnv(Scriptable scope) {
        super( scope );
        this.env = this;
    }

    /**
     * Create a standard environment and initialize it using the specified code. */
    public JSEnv(String setup, String setupID) throws EvaluatorException {
        this();
        this.env = this;
        eval( setup, setupID );
    }

    /** Evaluate the specified code in this environment. */
    public void eval(String code, String codeID) throws EvaluatorException {
        Context cx = Context.enter();
        try {
            cx.evaluateString( this.obj, code, codeID, 1, null );
        }
        finally {
            cx.exit();
        }
    }

    /** Read JS code from the specified input and evaluate it in this environment. */
    public void eval(Reader r, String rID) throws IOException, EvaluatorException {
        Context cx = Context.enter();
        try {
            cx.evaluateReader( this.obj, r, rID, 1, null );
        }
        finally {
            cx.exit();
        }
    }

    /** Parse a JSON string and return the result. */
    public JSObject parseJSON(String json) {
        return newJSObject( (Scriptable)JSONReader.read( this, json ) );
    }

    /** Create a new object within this environment. */
    public Scriptable newObject() {
        Context cx = Context.enter();
        try {
            return cx.newObject( this.obj );
        }
        finally {
            cx.exit();
        }
    }

    /** Create a new object within this environment. */
    public JSObject newJSObject() {
        Context cx = Context.enter();
        try {
            return new JSObject( cx.newObject( this.obj ), this );
        }
        finally {
            cx.exit();
        }
    }

    /** Create a new object within this environment using the provided scriptable. */
    public JSObject newJSObject(Scriptable obj) {
        return new JSObject( obj, this );
    }

    /** Create a new environment by extending this one. */
    public JSEnv extend() {
        Scriptable scope = newObject();
        scope.setPrototype( this.obj );
        scope.setParentScope( null );
        return new JSEnv( scope );
    }

}
