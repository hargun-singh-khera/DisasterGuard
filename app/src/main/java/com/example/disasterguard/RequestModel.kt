package com.example.disasterguard

data class RequestModel (
    var userId: String ?= null,
    var ticketId: String?= null,
    var laptopModel: String?= null,
    var problemDesc: String?= null,
    var requestSubmissionDate: String ?= null,
    var remarks: String ?= "",
    var reqCompleted: Boolean ?= false,
    var problemSolved: Boolean ?= false,
    var choiceChoosen: Boolean ?= false
)