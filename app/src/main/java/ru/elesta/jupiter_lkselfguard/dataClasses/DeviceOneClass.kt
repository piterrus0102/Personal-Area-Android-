package ru.elesta.jupiter_lkselfguard.dataClasses

class DeviceOneClass(var deviceID: String,
                     var type: String,
                     var barcode: String,
                     var guardStatus: String,
                     var alarmStatus: String,
                     var faultStatus: String,
                     var objects: ArrayList<Long>,
                     var socketNumber: String)