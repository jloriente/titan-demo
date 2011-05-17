package com.innerfunction.js;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Iterator;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * A thin object wrapper around a Rhino Scriptable object.
 * This class provides convenience methods for getting and setting properties of the object and
 * for invoking its methods, as well as for generating a JSON representation of the object.
 *
 * The JSObject has an underlying Scriptable instance as the target of the different getter,
 * setter, invoke etc. methods. This target can be switched to a property of the current target
 * object with a call to push(), and can be restored to the previous target witha call to pop(),
 * or can be reset to the original root target object with a call to reset().
 */
public class JSObject {

    /** The environment this object was created within. */
    protected JSEnv env;
    /** The target of getter, setter and invoke methods. */
    protected Scriptable obj;
    /** A stack of previous target objects. */
    protected List<Scriptable> stack = new ArrayList<Scriptable>();

    protected JSObject() {}

    public JSObject(JSEnv env) {
        this.env = env;
    }

    protected JSObject(Scriptable obj) {
        this.obj = obj;
    }

    public JSObject(Scriptable obj, JSEnv env) {
        this.obj = obj;
        this.env = env;
    }

    /** Invoke a named method and return the result. */
    public Object invoke(String name, Object... args) throws EvaluatorException {
        return ScriptableObject.callMethod( this.obj, name, args );
    }

    /** Invoke a named method and return the result as the specified type. */
    public <T> T invoke(Class<T> type, String name, Object... args) throws EvaluatorException {
        return convertType( type, ScriptableObject.callMethod( this.obj, name, args ) );
    }

    /** Return an array of object property IDs. */
    public Object[] getPropertyIDs() {
        return ScriptableObject.getPropertyIds( this.obj );
    }

    /** Test whether the JS object contains a named property. */
    public boolean has(String name) {
        return ScriptableObject.hasProperty( this.obj, name );
    }

    /** Get the value of the named property. Returns null if the property isn't set. */
    public Object get(String name) {
        Object value = ScriptableObject.getProperty( this.obj, name );
        return value == Scriptable.NOT_FOUND ? null : value;
    }

    /** Get the value of the named property and return the value as the specified type. */
    public <T> T get(Class<T> type, String name) {
        return convertType( type, get( name ) );
    }

    /** Get the value of the identified property and return the value as the specified type. */
    public <T> T get(Class<T> type, int idx) {
        Object value = ScriptableObject.getProperty( this.obj, idx );
        return convertType( type, value );
    }

    /**
     * Get the value of the identified property and return the value as the specified type.
     * Handles both string and integer property identifiers.
     */
    public <T> T get(Class<T> type, Object id) {
        if( id instanceof String ) {
            return get( type, (String)id );
        }
        if( id instanceof Number ) {
            return get( type, ((Number)id).intValue() );
        }
        throw new IllegalArgumentException("Property identifiers of type '"
                +id.getClass().getName()+"' not supported");
    }

    /** Perform type conversion on a value. */
    protected <T> T convertType(Class<T> type, Object value) {
        if( value == null || value == Scriptable.NOT_FOUND ) {
            return null;
        }
        if( type == String.class ) {
            return (T)Context.toString( value );
        }
        if( type == Number.class || type == Double.class ) {
            return (T)(new Double( Context.toNumber( value )));
        }
        if( type == Boolean.class ) {
            return (T)(new Boolean( Context.toBoolean( value )));
        }
        if( type == Scriptable.class ) {
            return (T)value;
        }
        if( type == JSObject.class ) {
            return (T)(new JSObject( (Scriptable)value, this.env ));
        }
        return (T)value;
    }

    public Scriptable getScriptable(String name) {
        return get( Scriptable.class, name );
    }

    public String getString(String name) {
        return get( String.class, name );
    }

    public Number getNumber(String name ) {
        return get( Number.class, name );
    }

    public Boolean getBoolean(String name) {
        return get( Boolean.class, name );
    }

    public JSObject getJSObject(String name) {
        return get( JSObject.class, name );
    }

    /** Set the value of a named property. */
    public Object set(String name, Object value) {
        ScriptableObject.putProperty( this.obj, name, value );
        return value;
    }

    /** Set an element value of an array. */
    public Object set(int idx, Object value) {
        ScriptableObject.putProperty( this.obj, idx, value );
        return value;
    }

    /** Delete a named property. */
    public void delete(String name) {
        this.obj.delete( name );
    }

    /** Return an iterator over the elements of an array property. */
    public <T> Iterator<T> iterate(Class<T> type, String name) {
        try {
            push( name );
            return iterate( type );
        }
        finally {
            pop();
        }
    }

    /** Assume the current target is an array instance and iterate over its elements. */
    public <T> Iterator<T> iterate(final Class<T> type) {
        final Scriptable array = this.obj;
        final int length = has("length") ? getNumber("length").intValue() : 0;
        final JSObject parent = this;
        return new Iterator<T>() {
            private int i = 0;
            public boolean hasNext() {
                return this.i < length;
            }
            public T next() {
                if( hasNext() ) {
                    return parent.convertType( type, ScriptableObject.getProperty( array, i++ ) );
                }
                throw new NoSuchElementException();
            }
            public void remove() {
                if( i > 0 ) {
                    ScriptableObject.deleteProperty( array, i - 1 );
                }
            }
        };
    }

    /** Create a new object property of the current target object. */
    public Scriptable newObject(String name) {
        Context cx = Context.enter();
        try {
            Scriptable scope = this.env.getScriptable();
            return (Scriptable)set( name, cx.newObject( scope ) );
        }
        finally {
            cx.exit();
        }
    }

    /** Create a new object property of the current target object. */
    public JSObject newJSObject(String name) {
        Context cx = Context.enter();
        try {
            Scriptable scope = this.env.getScriptable();
            Scriptable obj = (Scriptable)set( name, cx.newObject( scope ) );
            return new JSObject( obj, this.env );
        }
        finally {
            cx.exit();
        }
    }

    /** Create a new object property of the current target object. */
    public JSObject newJSObject(int idx) {
        Context cx = Context.enter();
        try {
            Scriptable scope = this.env.getScriptable();
            Scriptable obj = (Scriptable)set( idx, cx.newObject( scope ) );
            return new JSObject( obj, this.env );
        }
        finally {
            cx.exit();
        }
    }

    /** Create a new, empty list property of the current target object. */
    public JSObject newList(String name) {
        Context cx = Context.enter();
        try {
            Scriptable scope = this.env.getScriptable();
            Scriptable list = (Scriptable)set( name, cx.newArray( scope, 0 ) );
            return new JSObject( list, this.env );
        }
        finally {
            cx.exit();
        }
    }

    /** Create a new list property of the current target object, and fill with the specified items. */
    public JSObject newList(String name, Object... items) {
        Context cx = Context.enter();
        try {
            Scriptable scope = this.env.getScriptable();
            Scriptable list = (Scriptable)set( name, cx.newArray( scope, items ) );
            return new JSObject( list, this.env );
        }
        finally {
            cx.exit();
        }
    }

    /** Create a new list property of the current target object, and fill with the specified items. */
    public JSObject newList(String name, List items) {
        Context cx = Context.enter();
        try {
            Scriptable scope = this.env.getScriptable();
            Scriptable list = (Scriptable)set( name, cx.newArray( scope, items.toArray() ) );
            return new JSObject( list, this.env );
        }
        finally {
            cx.exit();
        }
    }


    /** Copy this object's properties to another JS object. */
    public void copyTo(JSObject obj) {
        copyTo( obj.getScriptable() );
    }

    /** Copy this object's properties to another JS object. */
    public void copyTo(Scriptable obj) {
        for( Object id : this.obj.getIds() ) {
            if( id instanceof Number ) {
                int i = ((Number)id).intValue();
                obj.put( i, obj, this.obj.get( i, this.obj ) );
            }
            else {
                String s = (String)id;
                obj.put( s, obj, this.obj.get( s, this.obj ) );
            }
        }
    }

    /** Get the underlying Rhino JS object. */
    public Scriptable getScriptable() {
        return this.obj;
    }
    
    /** Switch the target object to a property of the current target. */
    public JSObject push(String name) {
        Scriptable prop = get( Scriptable.class, name );
        if( prop != null ) {
            this.stack.add( this.obj );
            this.obj = prop;
        }
        return this;
    }

    /** Switch the target object to the previous target. */
    public JSObject pop() {
        int s = this.stack.size();
        if( s > 0 ) {
            this.obj = this.stack.remove( s - 1 );
        }
        return this;
    }

    /** Reset the target object to the original root object. */
    public JSObject reset() {
        if( this.stack.size() > 0 ) {
            this.obj = this.stack.get( 0 );
            this.stack.clear();
        }
        return this;
    }

    /** Return this object's JS environment. */
    public JSEnv getEnv() { return this.env; }

    /** Return the object's JSON representation. */
    public String toJSON() {
        return JSONWriter.writeObject( this.obj );
    }

}
