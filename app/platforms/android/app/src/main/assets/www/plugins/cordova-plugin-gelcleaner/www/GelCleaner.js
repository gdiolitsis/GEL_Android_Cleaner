cordova.define("cordova-plugin-gelcleaner.GELCleaner", function(require, exports, module) {
var exec = require('cordova/exec');

var GELCleaner = {
  clearAppCache: function (success, error) {
    exec(success, error, 'GELCleaner', 'clearAppCache', []);
  },
  boostRAM: function (success, error) {
    exec(success, error, 'GELCleaner', 'boostRAM', []);
  },
  clearTemp: function (success, error) {
    exec(success, error, 'GELCleaner', 'clearTemp', []);
  },
  removeJunk: function (success, error) {
    exec(success, error, 'GELCleaner', 'removeJunk', []);
  },
  optimizeBattery: function (success, error) {
    exec(success, error, 'GELCleaner', 'optimizeBattery', []);
  },
  killBackground: function (success, error) {
    exec(success, error, 'GELCleaner', 'killBackground', []);
  }
};

module.exports = GELCleaner;

});
