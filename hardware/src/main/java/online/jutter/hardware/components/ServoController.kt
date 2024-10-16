package online.jutter.hardware.components

import com.pi4j.context.Context
import com.pi4j.io.i2c.I2C
import com.pi4j.io.i2c.I2CBus
import online.jutter.hardware.base.I2CDevice
import java.time.Duration
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or
import kotlin.math.log


class ServoController(
    val pi4j: Context,
    val device: Int = DEFAULT_DEVICE,
): I2CDevice(pi4j, device, "LcdDisplay") {

    companion object {
        private const val DEFAULT_DEVICE = 0x8
    }

    override fun init(i2c: I2C?) {

    }

    fun setServoAngle(servoName: ServoName, angle: Int) {
        try {
            write(COMMAND_START)
            servoName.strName.forEach {
                write(it.code.toByte())
            }
            write(COMMAND_SPACE)
            angle.toString().forEach {
                write(it.code.toByte())
            }
            write(COMMAND_END)
        } catch (ex: Exception) {
            ex.toString()
        }
    }
}

enum class ServoName(val strName: String) {
    FRONT_LEFT_FIRST("0"),
    FRONT_LEFT_SECOND("1"),
    FRONT_RIGHT_FIRST("2"),
    FRONT_RIGHT_SECOND("3"),
    BACK_LEFT_FIRST("4"),
    BACK_LEFT_SECOND("5"),
    BACK_RIGHT_FIRST("6"),
    BACK_RIGHT_SECOND("7"),
}

private const val COMMAND_START: Byte = 0x0
private const val COMMAND_END: Byte = 0x1
private const val COMMAND_SPACE: Byte = 0x2