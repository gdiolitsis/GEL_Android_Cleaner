var exec = require('cordova/exec');

var GelCleaner = {
  ping: function (ok, err) {
    exec(ok, err, 'GelCleaner', 'ping', []);
  },
  clearCache: function (ok, err) {
    exec(ok, err, 'GelCleaner', 'clearCache', []);
  }
};

module.exports = GelCleaner;
