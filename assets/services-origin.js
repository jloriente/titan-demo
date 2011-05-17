var Services = {
    Platform: {
        isAndroid: function() {
            return Ti.Platform.osname == 'android';
        },
        isIPhone: function() {
            return Ti.Platform.osname == 'iphone';
        }
    },
    Log: {
        error: function( msg ) {
            Ti.API.error( msg );
        },
        info: function( msg ) {
            Ti.API.info( msg );
        }
    },
    Filesystem: {
        dataDir: Ti.Filesystem.applicationDataDirectory,
        getFile: function( dir, name ) {
            var file = Ti.Filesystem.getFile( dir, name );
            return {
                exists: function() {
                    return file.exists();
                },
                readJSON: function() {
                    return JSON.parse( this.readText() );
                },
                readText: function() {
                    return file.read().text;
                },
                remove: function() {
                    file.deleteFile();
                },
                write: function( data ) {
                    file.write( data );
                },
                writeJSON: function( obj ) {
                    this.write( JSON.stringify( obj ) );
                }
            };
        }
    },
    Locale: {
        getString: function( id ) {
            return Ti.Locale.getString( id );
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
            return Ti.App.Properties.getDouble( name );
        },
        setDouble: function( name, value ) {
            Ti.App.Properties.setDouble( name, value );
        },
        getString: function( name ) {
            return Ti.App.Properties.getString( name );
        },
        setString: function( name, value ) {
            Ti.App.Properties.setString( name, value );
        }
    }
};

