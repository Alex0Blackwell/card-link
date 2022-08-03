package com.example.cardlink

class Util {
    companion object {
        fun asString(text: Any?): String {
            if(text != null)
                return text as String
            return ""
        }
    }
}