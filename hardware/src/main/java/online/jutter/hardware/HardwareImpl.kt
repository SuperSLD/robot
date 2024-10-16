package online.jutter.hardware

import com.pi4j.Pi4J
import com.pi4j.context.Context
import com.pi4j.library.pigpio.PiGpio
import com.pi4j.plugin.linuxfs.provider.i2c.LinuxFsI2CProvider
import com.pi4j.plugin.linuxfs.provider.pwm.LinuxFsPwmProvider
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalInputProvider
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalOutputProvider
import com.pi4j.plugin.pigpio.provider.serial.PiGpioSerialProvider
import com.pi4j.plugin.pigpio.provider.spi.PiGpioSpiProvider
import com.pi4j.plugin.raspberrypi.platform.RaspberryPiPlatform
import online.jutter.hardware.base.Component
import online.jutter.hardware.components.ServoController

class HardwareImpl private constructor() : Hardware {

    companion object {

        private var instance: HardwareImpl? = null

        @Synchronized
        fun getInstance(): HardwareImpl {
            if (instance == null) {
                //System.setProperty("pi4j.library.path", "/home/libs")
                instance = HardwareImpl()
            }
            return instance!!
        }
    }

    var piGpio: PiGpio = PiGpio.newNativeInstance()
    var pi4j: Context = Pi4J.newContextBuilder()
        .noAutoDetect()
        .add(object : RaspberryPiPlatform() {
            override fun getProviders(): Array<String> {
                return arrayOf()
            }
        })
        .add(
            PiGpioDigitalInputProvider.newInstance(piGpio),
            PiGpioDigitalOutputProvider.newInstance(piGpio),
            LinuxFsPwmProvider.newInstance(0),
            PiGpioSerialProvider.newInstance(piGpio),
            PiGpioSpiProvider.newInstance(piGpio),
            LinuxFsI2CProvider.newInstance()
        )
        .build()

    private val components = hashMapOf<RobotComponent, Component>()

    init {
        //components[RobotComponent.LCD_DISPLAY] = LcdDisplay(pi4j)
        components[RobotComponent.SERVO_CONTROLLER] = ServoController(pi4j)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Component> getComponent(component: RobotComponent): T {
        return components[component] as T
    }
}