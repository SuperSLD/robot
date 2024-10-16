package online.jutter.hardware.base

import com.pi4j.context.Context
import com.pi4j.io.i2c.I2C
import com.pi4j.plugin.mock.provider.i2c.MockI2C
import java.time.Duration

abstract class I2CDevice(
    pi4j: Context,
    device: Int,
    name: String
): Component() {

    /**
     * The Default BUS and Device Address.
     * On the PI, you can look it up with the Command 'sudo i2cdetect -y 1'
     */
    protected val DEFAULT_BUS = 0x01

    /**
     * The PI4J I2C component
     */
    private var i2c: I2C? = null

    init {
        i2c = pi4j.create(
            I2C.newConfigBuilder(pi4j)
                .id("I2C-$DEFAULT_BUS@$device")
                .name("$name@$device")
                .bus(DEFAULT_BUS)
                .device(device)
                .build()
        )
        init(i2c)
    }

    /**
     * send a single command to device
     */
    protected suspend fun sendCommand(cmd: Byte) {
        i2c!!.write(cmd)
        delay(Duration.ofNanos(100000))
    }

    /**
     * send some data to device
     *
     * @param data
     */
    fun write(data: Byte) {
        i2c!!.write(data)
    }

    protected abstract fun init(i2c: I2C?)

    // --------------- for testing --------------------

    // --------------- for testing --------------------
    fun mock(): MockI2C? {
        return asMock(MockI2C::class.java, i2c)
    }
}