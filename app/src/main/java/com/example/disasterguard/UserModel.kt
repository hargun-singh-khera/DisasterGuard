package com.example.disasterguard

data class UserModel(
    var userId: String?= null,
    var userName: String?= null,
    var userEmail: String?= null,
    var userMobileNumber: String?= null,
    var userAdmin: Boolean?= false,
    var superUser: Boolean ?= false
)