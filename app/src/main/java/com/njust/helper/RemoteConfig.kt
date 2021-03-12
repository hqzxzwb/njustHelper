package com.njust.helper

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


object RemoteConfig {
    private var termId = "2020-2021-2"
    private var termStartId = "2021-03-08"


    fun setTerm(termId: String, termStartId: String) {
        if (termId.isNotEmpty()) {
            this.termId = termId
        }
        if (termStartId.isNotEmpty()) {
            this.termStartId = termStartId
        }
    }

    fun getTermId():String{
        return termId.toString()
    }

    fun getTermStartTime(): Long {
        try {
            val dateString = termStartId.toString()
            val dd = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = dd.parse(dateString)
            return date.time
        } catch (e: Exception) {
            val dateString = "2020-02-24"
            val dd = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = dd.parse(dateString)
            return date.time
        }
    }

}
