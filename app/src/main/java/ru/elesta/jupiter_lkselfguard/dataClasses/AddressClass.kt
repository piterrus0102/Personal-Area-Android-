package ru.elesta.jupiter_lkselfguard.dataClasses

class AddressClass(country: String,
                   city:String,
                   street:String,
                   house:String,
                   building:String,
                   corpus:String,
                   flat:String) {

    var country: String
    var city:String
    var street:String
    var house:String
    var building:String
    var corpus:String
    var flat:String

    init {
        this.country = country
        this.city = city
        this.street = street
        this.house = house
        this.building = building
        this.corpus = corpus
        this.flat = flat
    }
}