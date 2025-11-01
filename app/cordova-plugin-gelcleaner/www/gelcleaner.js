var gelcleaner = {
  clean: function (success, error) {
    cordova.exec(success, error, "GELCleaner", "clean", []);
  },
  kill: function (success, error) {
    cordova.exec(success, error, "GELCleaner", "kill", []);
  },
  stats: function (success, error) {
    cordova.exec(success, error, "GELCleaner", "stats", []);
  }
};
module.exports = gelcleaner;
