package com.digitalkoi.speechtotext.mvi.data

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

open class SingletonHolderDoubleArg<out T, in A, in B>(creator: (A, B) -> T) {

    private val creator: ((A, B) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(arg1: A, arg2: B): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg1, arg2)
                instance = created
                created
            }
        }
    }

    fun clearInstance() {
        instance = null
    }
}