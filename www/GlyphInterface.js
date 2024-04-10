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
    displayProgress: function(options, success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'displayProgress', [options]);
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
    },
    createBuilder: function(success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'createBuilder', []);
    },
    addFrameToBuilder: function(builderId, channels, success, error) {
        exec(success, error, 'GlyphInterfacePlugin', 'addFrameToBuilder', [builderId, channels]);
    },
    // Constants
    CHANNELS: {
        Nothing_Phone_1: {
            A1: 0,
            B1: 1,
            C: [2, 3, 4, 5],
            C1: 2,
            C2: 3,
            C3: 4,
            C4: 5,
            D: [7, 8, 9, 10, 11, 12, 13, 14],
            D1_1: 7,
            D1_2: 8,
            D1_3: 9,
            D1_4: 10,
            D1_5: 11,
            D1_6: 12,
            D1_7: 13,
            D1_8: 14,
            E1: 6
        },
        Nothing_Phone_2: {
            A: [0, 1],
            A1: 0,
            A2: 1,
            B: 2,
            B1: 2,
            C: [3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,20,21,22,23],
            C1: [3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18],
            C1_1: 3,
            C1_2: 4,
            C1_3: 5,
            C1_4: 6,
            C1_5: 7,
            C1_6: 8,
            C1_7: 9,
            C1_8: 10,
            C1_9: 11,
            C1_10: 12,
            C1_11: 13,
            C1_12: 14,
            C1_13: 15,
            C1_14: 16,
            C1_15: 17,
            C1_16: 18,
            C2: 19,
            C3: 20,
            C4: 21,
            C5: 22,
            C6: 23,
            D: [25, 26, 27, 28, 29, 30, 31, 32],
            D1_1: 25,
            D1_2: 26,
            D1_3: 27,
            D1_4: 28,
            D1_5: 29,
            D1_6: 30,
            D1_7: 31,
            D1_8: 32,
            E1: 24
        },
        Nothing_Phone_2a: {
            A: 25,
            B: 24,
            C: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23],
            C1: 0,
            C2: 1,
            C3: 2,
            C4: 3,
            C5: 4,
            C6: 5,
            C7: 6,
            C8: 7,
            C9: 8,
            C10: 9,
            C11: 10,
            C12: 11,
            C13: 12,
            C14: 13,
            C15: 14,
            C16: 15,
            C17: 16,
            C18: 17,
            C19: 18,
            C20: 19,
            C21: 20,
            C22: 21,
            C23: 22,
            C24: 23
        }
    }
};

module.exports = GlyphInterface;
