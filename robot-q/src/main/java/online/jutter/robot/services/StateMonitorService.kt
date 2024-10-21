package online.jutter.robot.services

import kotlinx.coroutines.delay
import online.jutter.hardware.HardwareInterface
import online.jutter.hardware.RobotComponent
import online.jutter.hardware.components.ServoController
import online.jutter.hardware.components.ServoName
import online.jutter.robot.common.log
import online.jutter.robot.services.base.Service

class StateMonitorService: Service() {

    private val servoController: ServoController = HardwareInterface.getComponent(RobotComponent.SERVO_CONTROLLER)
    private val startTime: Long = System.currentTimeMillis()

    override suspend fun loop() {
        var angle = 0
        try {
         angle = readln().toInt()
        } catch (ex: Exception) {
            println("error")
        }
        servoController.setServoAngle(ServoName.FRONT_LEFT_FIRST, angle)
        servoController.setServoAngle(ServoName.FRONT_LEFT_SECOND, angle)
        servoController.setServoAngle(ServoName.FRONT_RIGHT_FIRST, angle)
        servoController.setServoAngle(ServoName.FRONT_RIGHT_SECOND, angle)
        servoController.setServoAngle(ServoName.BACK_LEFT_FIRST, angle)
        servoController.setServoAngle(ServoName.BACK_LEFT_SECOND, angle)
        servoController.setServoAngle(ServoName.BACK_RIGHT_FIRST, angle)
        servoController.setServoAngle(ServoName.BACK_RIGHT_SECOND, angle)
//        delay(1000 * 5)
//        servoController.setServoAngle(ServoName.FRONT_LEFT_FIRST, 0)
//        delay(1000 * 5)
//        servoController.setServoAngle(ServoName.FRONT_LEFT_FIRST, 180)
//        delay(1000 * 5)
    }

    private fun getUpTimeString(): String {
        val sec = ((System.currentTimeMillis() - startTime) / 1000) % 60
        val min = ((System.currentTimeMillis() - startTime) / 1000 / 60) % 60
        val hour = (System.currentTimeMillis() - startTime) / 1000 / 60 / 60
        return "${hour}h ${min}m ${sec}s"
    }
}