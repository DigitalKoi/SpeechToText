package com.digitalkoi.speechtotext.util

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 09/03/18.
 */

open class SingletonHolderSingleArg<out T, in A>(creator: (A) -> T) {

    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                created
            }
        }
    }
}