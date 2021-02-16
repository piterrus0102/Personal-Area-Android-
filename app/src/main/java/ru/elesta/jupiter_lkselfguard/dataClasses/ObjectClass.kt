package ru.elesta.jupiter_lkselfguard.dataClasses

data class ObjectClass(var objectID: String,
                       var typeID: String,
                       var customName: String,
                       var alarmStatus: String,
                       var guardStatus: String,
                       var faultStatus: String,
                       var name: String,
                       var country: String,
                       var city: String,
                       var street: String,
                       var house: String,
                       var building: String,
                       var flat: String,
                       var latitude: Double,
                       var longitude: Double,
                       var socketNumber: String){
}