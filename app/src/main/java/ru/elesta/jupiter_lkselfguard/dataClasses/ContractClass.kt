package ru.elesta.jupiter_lkselfguard.dataClasses

data class ContractClass(var responsibleID: String,
                         var email: String,
                         var phone: String,
                         var name: String,
                         var surname: String,
                         var patronymic: String,
                         var services: ArrayList<ServiceClass>)