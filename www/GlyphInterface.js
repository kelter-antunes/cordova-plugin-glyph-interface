var exec = require('cordova/exec');

var GlyphInterface = {
    builder: function(success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'builder', []);
    },
    getPlatform: function(success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'getPlatform', []);
    },
    channel: function(id, channel, lightValue, success, error) {
        var args = [id, channel];
        if(lightValue !== undefined) {
            args.push(lightValue);
        }
        exec(success, error, 'GlyphInterfacePlugin', 'channel', args);
    },
    build: function(id, success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'build', [id]);
    },
    toggle: function(id, success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'toggle', [id]);
    },
    setPeriod: function(id, period, success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'setPeriod', [id, period]);
    },
    setCycles: function(id, cycles, success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'setCycles', [id, cycles]);
    },
    setInterval: function(id, interval, success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'setInterval', [id, interval]);
    },
    animate: function(id, success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'animate', [id]);
    },
    getPlatformVersion: function(success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'getPlatformVersion', []);
    }
};

module.exports = GlyphInterface;
