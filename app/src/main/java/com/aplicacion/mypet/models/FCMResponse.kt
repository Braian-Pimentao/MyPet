package com.aplicacion.mypet.models

import java.util.*

class FCMResponse(var multicast_id: Long, var success: Int, var failure: Int, var canonical_ids: Int, var results: ArrayList<Any>?) {}