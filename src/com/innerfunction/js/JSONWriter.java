package com.innerfunction.js;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Wrapper;
//import org.mozilla.javascript.NativeJavaObject;

/**
 * Output Rhino JS objects as JSON.
 * Code adapted from org.stringtree.json.JSONWriter.
 */
public class JSONWriter {

    private StringBuffer buf = new StringBuffer();
    private Stack<Object> calls = new Stack<Object>();
    boolean emitClassName = true;
    
    public JSONWriter(boolean emitClassName) {
        this.emitClassName = emitClassName;
    }
    
    public JSONWriter() {
        this(true);
    }

    public static String writeObject(Object obj) {
        return new JSONWriter().write( obj );
    }

    public String write(Object object) {
        buf.setLength(0);
        value(object);
        return buf.toString();
    }

    public String write(long n) {
        return String.valueOf(n);
    }

    public String write(double d) {
        return String.valueOf(d);
    }

    public String write(char c) {
        return "\"" + c + "\"";
    }
    
    public String write(boolean b) {
        return String.valueOf(b);
    }

    private void value(Object object) {
        if (object == null || cyclic(object)) {
            add("null");
        } else {
            calls.push(object);
            if( object instanceof Wrapper ) value( ((Wrapper)object).unwrap() );
            else if( object instanceof NativeArray ) array( (NativeArray)object );
            else if( object instanceof Scriptable ) map( (Scriptable)object );
            else if (object instanceof Class) string(object);
            else if (object instanceof Boolean) bool(((Boolean) object).booleanValue());
            else if (object instanceof Number) add(object);
            else if (object instanceof String) string(object);
            else if (object instanceof Character) string(object);
            else if (object instanceof Map) map((Map)object);
            else if (object instanceof Iterator) array((Iterator)object);
            else if (object instanceof Collection) array(((Collection)object).iterator());
            else string( object );
            calls.pop();
        }
    }

    private boolean cyclic(Object object) {
        Iterator it = calls.iterator();
        while (it.hasNext()) {
            Object called = it.next();
            if (object == called) return true;
        }
        return false;
    }
    
    private void add(String name, Object value) {
        add('"');
        add(name);
        add("\":");
        value(value);
    }

    private void map(Map map) {
        add("{");
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            value(e.getKey());
            add(":");
            value(e.getValue());
            if (it.hasNext()) add(',');
        }
        add("}");
    }

    // TODO: Review this code. Currently, only direct properties of the object being serialized are
    // included in the JSON output - i.e. properties on the object's prototype won't appear.
    // A more complete - and efficient - solution would be to (i) provide a scope object when calling
    // this class (ii) enumerate the properties of the scope and store in a map (iii) in this method,
    // if one of the properties being handled appears in the map, then only include that property in
    // the output if it is a direct property of the current object or of any object in the current
    // object's prototype chain between the current object and the scope.
    // TODO: Also need someway to avoid serializing native Java objects (except strings & numbers).
    private void map(Scriptable obj) {
        Map props = new HashMap<Object,Object>();
        Object[] ids = obj.getIds();
        for( int i = 0; i < ids.length; i++ ) {
            Object key = ids[i];
            if( hasOwnProperty( key, obj ) && !"prototype".equals( key ) ) {
                props.put( key, getProperty( key, obj ) );
                /*
                Object val = getProperty( key, obj );
                if( val instanceof NativeJavaObject ) {
                    System.out.printf("%s=%s\n", key,
                            ((NativeJavaObject)val).unwrap().getClass().getName());
                }
                props.put( key, val );
                */
            }
        }
        map( props );
    }
    
    /** Test whether a property is a direct property of an object. */
    private boolean hasOwnProperty(Object key, Scriptable obj) {
        if( key instanceof Number ) return obj.has( ((Number)key).intValue(), obj );
        return obj.has( key.toString(), obj );
    }

    /** Get an object's property. */
    private Object getProperty(Object key, Scriptable obj) {
        if( key instanceof Number ) return ScriptableObject.getProperty( obj, ((Number)key).intValue() );
        return ScriptableObject.getProperty( obj, key.toString() );
    }

    private void array(Iterator it) {
        add("[");
        while (it.hasNext()) {
            value(it.next());
            if (it.hasNext()) add(",");
        }
        add("]");
    }

    private void array(NativeArray arr) {
        add("[");
        for( int i = 0; i < arr.getLength(); i++ ) {
            if( i > 0 ) add(",");
            value( ScriptableObject.getProperty( arr, i ) );
        }
        add("]");
    }

    private void bool(boolean b) {
        add(b ? "true" : "false");
    }

    private void string(Object obj) {
        add('"');
        CharacterIterator it = new StringCharacterIterator(obj.toString());
        for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
            if (c == '"') add("\\\"");
            else if (c == '\\') add("\\\\");
            else if (c == '/') add("\\/");
            else if (c == '\b') add("\\b");
            else if (c == '\f') add("\\f");
            else if (c == '\n') add("\\n");
            else if (c == '\r') add("\\r");
            else if (c == '\t') add("\\t");
            else if (Character.isISOControl(c)) {
                unicode(c);
            } else {
                add(c);
            }
        }
        add('"');
    }

    private void add(Object obj) {
        buf.append(obj);
    }

    private void add(char c) {
        buf.append(c);
    }
    
    static char[] hex = "0123456789ABCDEF".toCharArray();

    private void unicode(char c) {
        add("\\u");
        int n = c;
        for (int i = 0; i < 4; ++i) {
            int digit = (n & 0xf000) >> 12;
            add(hex[digit]);
            n <<= 4;
        }
    }
}
