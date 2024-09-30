package online.jutter.robot.services.base

import online.jutter.robot.common.log

object ServiceController {

    val services = hashMapOf<Class<*>, Service>()

    fun startService(service: Service) {
        val serviceName = service::class.java
        log("start service $serviceName")
        if (!services.containsKey(serviceName)) {
            services[serviceName] = service
        }
        if (!services[serviceName]!!.isRunning()) {
            services[serviceName]!!.start()
        }
    }

    inline fun <reified T> getService() = services[T::class.java]

    inline fun getService(clazz: Class<*>) = services[clazz]

    fun stopService(clazz: Class<*>) {
        if (services.containsKey(clazz) && services[clazz]!!.isRunning()) {
            services[clazz]!!.stop()
        }
    }
}