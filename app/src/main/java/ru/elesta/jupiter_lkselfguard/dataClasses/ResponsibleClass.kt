package ru.elesta.jupiter_lkselfguard.dataClasses

import ru.elesta.jupiter_lkselfguard.JSONParser.JSONParserResponsibles

data class ResponsibleClass(var responsibleID: String,
                            var name: String,
                            var surname: String,
                            var patronymic: String,
                            var login: String,
                            var accessAlarmButton: Boolean,
                            var accessCustomerAccount: Boolean,
                            var accessUmka: Boolean,
                            var umkaID: String,
                            var writeTrackOfMovement: Boolean,
                            var objects: Array<Int>,
                            var devices: Array<Int>,
                            var deviceKeys: ArrayList<KeyClass>,
                            var socketNumber: String)