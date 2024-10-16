package online.jutter.robot.services.base

import kotlinx.coroutines.*
import online.jutter.robot.common.log

@OptIn(DelicateCoroutinesApi::class)
abstract class Service {

    private var isRunning = false
    private var loopJob: Job? = null

    private var params = hashMapOf<String, Any>()

    protected open fun onStart() { /* pass */ }

    protected open fun onStop() { /* pass */ }

    fun start(params: HashMap<String, Any>) {
        loopJob?.cancel()
        isRunning = true
        this.params = params
        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            log("error in service loop")
            log(throwable.message ?: "exception message is null")
            log(throwable.stackTrace.toString())
        }
        loopJob = GlobalScope.launch(Dispatchers.IO + exceptionHandler) {
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

    @Suppress("UNCHECKED_CAST")
    fun <T> getParam(key: String) = params[key] as T

    fun isRunning() = isRunning
}