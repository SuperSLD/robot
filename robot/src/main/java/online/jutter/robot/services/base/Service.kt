package online.jutter.robot.services.base

import kotlinx.coroutines.*

@OptIn(DelicateCoroutinesApi::class)
abstract class Service {

    private var isRunning = false
    private var loopJob: Job? = null

    protected open fun onStart() { /* pass */ }

    protected open fun onStop() { /* pass */ }

    fun start() {
        loopJob?.cancel()
        isRunning = true
        loopJob = GlobalScope.launch(Dispatchers.IO) {
            onStart()
            while (isRunning) {
                loop()
            }
        }
    }

    fun stop() {
        onStop()
        loopJob?.cancel()
        isRunning = false
    }

    abstract suspend fun loop()

    fun isRunning() = isRunning
}