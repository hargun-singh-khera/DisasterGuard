package com.example.disasterguard

data class RequestModel (
    var userId: String ?= null,
    var ticketId: String?= null,
    var incidentType: String?= null,
    var incidentDesc: String?= null,
    var incidentLocation: String ?= null,
    var incidentDate: String ?= null,
    var incidentTime: String ?= null,
    var emergencyLevel: String ?= null,
    var submissionDate: String ?= null,
    var reqCompleted: Boolean ?= false
)