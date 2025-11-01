var gelcleaner = {
    clean: function (success, error) {
        cordova.exec(
            success,
            error,
            "GELCleaner",
            "clean",
            []
        );
    }
};
module.exports = gelcleaner;
