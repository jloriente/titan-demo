package com.innerfunction.js;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * An abstract implementation of the Function interface.
 * Subclasses should implement the 'call' method.
 */
public abstract class AbstractFunction extends ScriptableObject implements Function {

    public abstract Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args);

    public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
        throw new UnsupportedOperationException();
    }

    public String getClassName() { return "Function"; }

}
