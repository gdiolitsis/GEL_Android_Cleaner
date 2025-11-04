var exec = require('cordova/exec');

var GELCleaner = {

  // ✅ Clear full cache
  clearAppCache: function (success, error) {
    exec(success, error, 'GELCleaner', 'clearAppCache', []);
  },

  // ✅ RAM booster (best-effort)
  boostRAM: function (success, error) {
    exec(success, error, 'GELCleaner', 'boostRAM', []);
  },

  // ✅ Remove temp directories
  clearTemp: function (success, error) {
    exec(success, error, 'GELCleaner', 'clearTemp', []);
  },

  // ✅ Junk scan + delete
  removeJunk: function (success, error) {
    exec(success, error, 'GELCleaner', 'removeJunk', []);
  },

  // ✅ Battery optimization suggestions
  optimizeBattery: function (success, error) {
    exec(success, error, 'GELCleaner', 'optimizeBattery', []);
  },

  // ✅ Kill background apps
  killBackground: function (success, error) {
    exec(success, error, 'GELCleaner', 'killBackground', []);
  },

  // ✅ Storage stats
  stats: function (success, error) {
    exec(success, error, 'GELCleaner', 'stats', []);
  }
};

module.exports = GELCleaner;
