var exec = require('cordova/exec');

var GELCleaner = {
    clearAppCache: function(success, error) {
        exec(success, error, "GelCleaner", "clearAppCache", []);
    },
    boostRAM: function(success, error) {
        exec(success, error, "GelCleaner", "boostRAM", []);
    },
    clearTemp: function(success, error) {
        exec(success, error, "GelCleaner", "clearTemp", []);
    },
    killBackground: function(success, error) {
        exec(success, error, "GelCleaner", "killBackground", []);
    }
};

module.exports = GELCleaner;
