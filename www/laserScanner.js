var exec = require('cordova/exec');

var scanner = {
  receiveListener: function(cb) {
    exec(cb, null, "broadcastBarcodeReceiver", 'receive', []);
  }
};

module.exports = scanner;
