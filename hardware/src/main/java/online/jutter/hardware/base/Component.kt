package online.jutter.hardware.base

import java.time.Duration

open class Component {

    /**
     * Override this method to clean up all used resources
     */
    open suspend fun reset() {
        //nothing to do by default
    }

    /**
     * Utility function to sleep for the specified amount of milliseconds.
     * An [InterruptedException] will be caught and ignored while setting the interrupt flag again.
     *
     * @param duration Time to sleep
     */
    protected suspend fun delay(duration: Duration) {
        try {
            val nanos = duration.toNanos()
            val millis = nanos / 1000000
            val remainingNanos = (nanos % 1000000).toInt()
            Thread.sleep(millis, remainingNanos)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    protected fun <T> asMock(type: Class<T>, instance: Any?): T {
        return type.cast(instance)
    }
}