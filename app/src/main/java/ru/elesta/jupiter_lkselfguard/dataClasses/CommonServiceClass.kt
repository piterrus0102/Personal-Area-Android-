package ru.elesta.jupiter_lkselfguard.dataClasses

class CommonServiceClass(serviceID: String,
                         name: String,
                         description: String){

    var serviceID: String
    var name: String
    var description: String

    init {
        this.serviceID = serviceID
        this.name = name
        this.description = description
    }
}