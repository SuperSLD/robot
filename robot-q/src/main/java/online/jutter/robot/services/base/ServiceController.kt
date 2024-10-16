package online.jutter.robot.services.base

import online.jutter.robot.common.log

object ServiceController {

    val services = hashMapOf<Class<*>, Service>()

    @Synchronized
    fun startService(service: Service, params: HashMap<String, Any> = hashMapOf()) {
        val clazz = service::class.java
        log("start service ${clazz.simpleName}")
        if (!services.containsKey(clazz)) {
            services[clazz] = service
            log("service ${clazz.simpleName}: register new instance")
        }
        if (!services[clazz]!!.isRunning()) {
            services[clazz]!!.start(params)
        }
    }

    inline fun <reified T> getService() = services[T::class.java]

    inline fun getService(clazz: Class<*>) = services[clazz]

    fun stopService(clazz: Class<*>) {
        if (services.containsKey(clazz) && services[clazz]!!.isRunning()) {
            services[clazz]!!.stop()
            log("stop service ${clazz.simpleName}")
        }
    }
}