package com.innerfunction.js;

import org.mozilla.javascript.Scriptable;

/**
 * A class for wrapping a Scriptable instance in a separate Scriptable interface.
 * Only really useful if subclassed.
 */
public class ScriptableWrapper implements Scriptable {

    /** The wrapped JS object. */
    private Scriptable jsObj;

    /**
     * Create an empty wrapper.
     * setScriptable() must be called before use.
     */
    protected ScriptableWrapper() {}

    public ScriptableWrapper(Scriptable jsObj) {
        this.jsObj = jsObj;
    }

    /** Change the wrapped object. */
    public void setScriptable(Scriptable jsObj) {
        this.jsObj = jsObj;
    }

    public Scriptable getScriptable() {
        return this.jsObj;
    }

    /** Return a JSON representation of this object. */
    public String toJSON() {
        return JSONWriter.writeObject( this.jsObj );
    }

    // Scriptable interface
    public void delete(int index) {
        this.jsObj.delete( index );
    }

    public void delete(String name) {
        this.jsObj.delete( name );
    }

    public Object get(int index, Scriptable start) {
        return this.jsObj.get( index, this.jsObj );
    }

    public Object get(String name, Scriptable start) {
        return this.jsObj.get( name, this.jsObj );
    }

    public String getClassName() {
        return this.jsObj.getClassName();
    }

    public Object getDefaultValue(Class<?> hint) {
        return this.jsObj.getDefaultValue( hint );
    }

    public Object[] getIds() {
        return this.jsObj.getIds();
    }

    public Scriptable getParentScope() {
        return this.jsObj.getParentScope();
    }

    public Scriptable getPrototype() {
        return this.jsObj.getPrototype();
    }

    public boolean has(int index, Scriptable start) {
        return this.jsObj.has( index, this.jsObj );
    }

    public boolean has(String name, Scriptable start) {
        return this.jsObj.has( name, this.jsObj );
    }

    public boolean hasInstance(Scriptable instance) {
        return this.jsObj.hasInstance( instance );
    }

    public void put(int index, Scriptable start, Object value) {
        this.jsObj.put( index, this.jsObj, value );
    }

    public void put(String name, Scriptable start, Object value) {
        this.jsObj.put( name, this.jsObj, value );
    }

    public void setParentScope(Scriptable parent) {
        this.jsObj.setParentScope( parent );
    }

    public void setPrototype(Scriptable prototype) {
        this.jsObj.setPrototype( prototype );
    }

    public String toString() {
        return toJSON();
    }

}
