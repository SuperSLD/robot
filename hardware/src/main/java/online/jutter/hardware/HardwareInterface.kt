package online.jutter.hardware

import online.jutter.hardware.base.Component

object HardwareInterface {

    fun getInstance(): Hardware = HardwareImpl.getInstance()

    fun <T: Component> getComponent(component: RobotComponent) =
        HardwareImpl.getInstance().getComponent<T>(component)
}