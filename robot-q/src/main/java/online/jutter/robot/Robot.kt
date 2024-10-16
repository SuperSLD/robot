package online.jutter.robot

import online.jutter.hardware.HardwareInterface
import online.jutter.robot.common.log
import online.jutter.robot.services.StateMonitorService
import online.jutter.robot.services.base.ServiceController

class Robot {

    companion object {

        private var instance: Robot? = null

        @Synchronized
        fun getInstance(): Robot {
            if (instance == null) {
                instance = Robot()
            }
            return instance!!
        }
    }

    init {
        HardwareInterface.getInstance()
        ServiceController.startService(StateMonitorService())
    }

    fun loop() {
        log("robot main loop started")
        while (true) {

        }
    }
}