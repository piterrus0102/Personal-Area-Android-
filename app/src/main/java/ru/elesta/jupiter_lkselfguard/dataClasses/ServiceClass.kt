package ru.elesta.jupiter_lkselfguard.dataClasses

data class ServiceClass(var id: String,
                        var serviceID: String,
                        var pin: Long,
                        var urls: ArrayList<String>)