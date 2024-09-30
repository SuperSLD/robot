package online.jutter.robot.services

import kotlinx.coroutines.delay
import online.jutter.hardware.HardwareInterface
import online.jutter.hardware.RobotComponent
import online.jutter.hardware.components.LcdDisplay
import online.jutter.robot.services.base.Service

class StateMonitorService: Service() {

    private val display: LcdDisplay = HardwareInterface.getComponent(RobotComponent.LCD_DISPLAY)
    private val startTime: Long = System.currentTimeMillis()

    override suspend fun loop() {
        display.centerTextInLine(getUpTimeString(), 0)
        delay(1000)
    }

    private fun getUpTimeString(): String {
        val sec = ((System.currentTimeMillis() - startTime) / 1000) % 60
        val min = ((System.currentTimeMillis() - startTime) / 1000 / 60) % 60
        val hour = (System.currentTimeMillis() - startTime) / 1000 / 60 / 60
        return "${hour}h ${min}m ${sec}s"
    }
}