package ru.elesta.jupiter_lkselfguard.dataClasses

data class SectionClass(var deviceID: String,
                        var alarmStatus: String,
                        var guardStatus: String,
                        var faultStatus: String,
                        var indexOfSection: String,
                        var objectID: String,
                        var sectionID: String,
                        var customName: String,
                        var socketNumber: String)