cordova.define('cordova/plugin_list', function(require, exports, module) {
  module.exports = [
    {
      "id": "cordova-plugin-gelcleaner.GELCleaner",
      "file": "plugins/cordova-plugin-gelcleaner/www/GelCleaner.js",
      "pluginId": "cordova-plugin-gelcleaner",
      "clobbers": [
        "GELCleaner"
      ]
    }
  ];
  module.exports.metadata = {
    "cordova-plugin-gelcleaner": "1.0.1"
  };
});