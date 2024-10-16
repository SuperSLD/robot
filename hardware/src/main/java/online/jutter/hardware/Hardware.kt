package online.jutter.hardware

import online.jutter.hardware.base.Component

interface Hardware {

    fun <T: Component> getComponent(component: RobotComponent): T
}

enum class RobotComponent {
    LCD_DISPLAY,
    SERVO_CONTROLLER,
}