package online.jutter.robot.services

import kotlinx.coroutines.delay
import online.jutter.hardware.HardwareInterface
import online.jutter.hardware.RobotComponent
import online.jutter.hardware.components.ServoController
import online.jutter.hardware.components.ServoName
import online.jutter.robot.services.base.Service

class StateMonitorService: Service() {

    private val servoController: ServoController = HardwareInterface.getComponent(RobotComponent.SERVO_CONTROLLER)
    private val startTime: Long = System.currentTimeMillis()

    override suspend fun loop() {
        servoController.setServoAngle(ServoName.FRONT_LEFT_FIRST, 90)
        delay(1000 * 5)
    }

    private fun getUpTimeString(): String {
        val sec = ((System.currentTimeMillis() - startTime) / 1000) % 60
        val min = ((System.currentTimeMillis() - startTime) / 1000 / 60) % 60
        val hour = (System.currentTimeMillis() - startTime) / 1000 / 60 / 60
        return "${hour}h ${min}m ${sec}s"
    }
}