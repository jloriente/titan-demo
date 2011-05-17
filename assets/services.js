var Services = {
    Context : 'AndroidContext',
    Platform: {
        isAndroid: function() {
            return true;
        },
        isIPhone: function() {
            return false;
        }
    },
    Log: {
        error: function( msg ) {
            var tag = 'todo';
            var Log = new Packages.android.utils.Log();
            Log.e(tag, msg);
        },
        info: function( msg ) {
            var tag = 'todo';
            var Log = new Packages.android.utils.Log();
            Log.i(tag, msg)
        }
    },
    Filesystem: {
        dataDir: function(){ 
            return Context.getFilesDir();
        }
        getFile: function( dir, name, context ) {
            return {
                exists: function() {
                    var File = Context.getFileStreamPath();
                    return file.exists();
                },
                readJSON: function() {
                    return JSON.parse( this.readText() );
                },
                readText: function() {
                    var in = new java.io.BufferedInputStrem( Context.openFileInput( dir+name ));
                    var resultText = '';
                    // Loop file and store in memory
                    while (in.available() > 0){
                        resultText += resultText;
                    }
                    return resultText.toString(); 
                },
                remove: function() {
                    Context.deleteFile( dir+name );
                },
                write: function( data ) {
                    var fos = Context.openFileOutput( dir+name, Context.MODE_PRIVATE );
                    fos.write( data );
                },
                writeJSON: function( obj ) {
                    this.write( JSON.stringify( obj ) );
                }
            };
        }
    },
    Locale: {
        getString: function( id ) {
            return Context.getString( id ); 
        }
    },
    Network: {
        isOnline: function() {
            return Ti.Network.online;
        },
        httpClient: function() {
            return Ti.Network.createHTTPClient();
        }
    },
    Storage: {
        getDouble: function( name ) {
            var pref = Context.getPreferences();
            return pref.getFloat( name );
        },
        setDouble: function( name, value ) {
            var pref = Context.getPreferences();
            var editor = pref.edit();
            edit.putFloat( name, value );
        },
        getString: function( name ) {
            var pref = Context.getPreference();
            return pref.getString( name );
        },
        setString: function( name, value ) {
            var pref = Context.getPreference();
            var editor = pref.edit();
            editor.putString( name, value );
        }
    }
};

