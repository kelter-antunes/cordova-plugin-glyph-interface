var exec = require('cordova/exec');

var GlyphInterface = {
    builder: function(options, success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'builder', [options]);
    },
    turnOff: function(options, success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'turnOff', []);
    },
    getPlatform: function(success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'getPlatform', []);
    },
    channel: function(options, success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'channel', [options]);
    },
    build: function(options, success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'build', [options]);
    },
    toggle: function(options, success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'toggle', [options]);
    },
    setPeriod: function(options, success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'setPeriod', [options]);
    },
    setCycles: function(options, success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'setCycles', [options]);
    },
    setInterval: function(options, success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'setInterval', [options]);
    },
    animate: function(options, success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'animate', [options]);
    },
    getPlatformVersion: function(success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'getPlatformVersion', []);
    },
    listBuilderIds: function(success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'listBuilderIds', []);
    },
    clearBuilders: function(success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'clearBuilders', []);
    },
    listFrameIds: function(success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'listFrameIds', []);
    },
    clearFrames: function(success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'clearFrames', []);
    }
};

module.exports = GlyphInterface;
