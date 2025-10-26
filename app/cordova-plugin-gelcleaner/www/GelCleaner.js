var exec = require('cordova/exec');

exports.cleanCache = function (success, error) {
  exec(success, error, 'GelCleaner', 'cleanCache', []);
};
exports.clearTemp = function (success, error) {
  exec(success, error, 'GelCleaner', 'clearTemp', []);
};
exports.boostRAM = function (success, error) {
  exec(success, error, 'GelCleaner', 'boostRAM', []);
};
