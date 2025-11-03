// GEL Cleaner — Full JS Bridge (final)
var exec = require('cordova/exec');

function call(action, args, ok, fail) {
  exec(ok || function(){}, fail || function(){}, 'GELCleaner', action, args || []);
}

var GELCleaner = {
  // -- Core
  stats:          function(ok, fail){ call('stats', [], ok, fail); },
  version:        function(ok, fail){ call('version', [], ok, fail); },
  ping:           function(ok, fail){ call('ping', [], ok, fail); },

  // -- Access / Permissions
  fullAccess:     function(ok, fail){ call('fullAccess', [], ok, fail); },

  // -- CPU
  cpuInfo:        function(ok, fail){ call('cpuInfo', [], ok, fail); },
  cpuLiveStart:   function(ok, fail){ call('cpuLiveStart', [], ok, fail); },
  cpuLiveStop:    function(ok, fail){ call('cpuLiveStop', [], ok, fail); },

  // -- Memory / RAM
  cleanRam:       function(ok, fail){ call('cleanRam', [], ok, fail); },
  killApps:       function(ok, fail){ call('kill', [], ok, fail); },     // alias native "kill"
  kill:           function(ok, fail){ call('kill', [], ok, fail); },     // backwards compat

  // -- Storage cleaners
  safeClean:          function(ok, fail){ call('safeClean', [], ok, fail); },
  aggressiveClean:    function(ok, fail){ call('clean', [], ok, fail); }, // alias native "clean"
  clean:              function(ok, fail){ call('clean', [], ok, fail); },  // backwards compat
  mediaJunkClean:     function(ok, fail){ call('mediaJunkClean', [], ok, fail); },
  browserCacheClean:  function(ok, fail){ call('browserCacheClean', [], ok, fail); },
  tempClean:          function(ok, fail){ call('tempClean', [], ok, fail); },

  // -- Battery / boost
  batteryBoost:   function(ok, fail){ call('batteryBoost', [], ok, fail); },

  // -- Promise helpers (optional)
  _p: function(action, args){
    return new Promise(function(res, rej){ call(action, args, res, rej); });
  }
};

module.exports = GELCleaner;
