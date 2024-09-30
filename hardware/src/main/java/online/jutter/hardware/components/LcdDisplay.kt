package online.jutter.hardware.components

import com.pi4j.context.Context
import com.pi4j.io.i2c.I2C
import kotlinx.coroutines.runBlocking
import online.jutter.hardware.base.I2CDevice
import java.time.Duration
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

class LcdDisplay(
    val pi4j: Context,
    val rows: Int = 2,
    val columns: Int = 16,
    val device: Int = DEFAULT_DEVICE,
): I2CDevice(pi4j, device, "LcdDisplay") {

    companion object {
        /** Flags for display commands  */
        private const val LCD_CLEAR_DISPLAY = 0x01.toByte()
        private const val LCD_RETURN_HOME = 0x02.toByte()
        private const val LCD_SCROLL_RIGHT = 0x1E.toByte()
        private const val LCD_SCROLL_LEFT = 0x18.toByte()
        private const val LCD_ENTRY_MODE_SET = 0x04.toByte()
        private const val LCD_DISPLAY_CONTROL = 0x08.toByte()
        private const val LCD_CURSOR_SHIFT = 0x10.toByte()
        private const val LCD_FUNCTION_SET = 0x20.toByte()
        private const val LCD_SET_CGRAM_ADDR = 0x40.toByte()
        private const val LCD_SET_DDRAM_ADDR = 0x80.toByte()

        // flags for display entry mode
        private const val LCD_ENTRY_RIGHT = 0x00.toByte()
        private const val LCD_ENTRY_LEFT = 0x02.toByte()
        private const val LCD_ENTRY_SHIFT_INCREMENT = 0x01.toByte()
        private const val LCD_ENTRY_SHIFT_DECREMENT = 0x00.toByte()

        // flags for display on/off control
        private const val LCD_DISPLAY_ON = 0x04.toByte()
        private const val LCD_DISPLAY_OFF = 0x00.toByte()
        private const val LCD_CURSOR_ON = 0x02.toByte()
        private const val LCD_CURSOR_OFF = 0x00.toByte()
        private const val LCD_BLINK_ON = 0x01.toByte()
        private const val LCD_BLINK_OFF = 0x00.toByte()

        // flags for display/cursor shift
        private const val LCD_DISPLAY_MOVE = 0x08.toByte()
        private const val LCD_CURSOR_MOVE = 0x00.toByte()

        // flags for function set
        private const val LCD_8BIT_MODE = 0x10.toByte()
        private const val LCD_4BIT_MODE = 0x00.toByte()
        private const val LCD_2LINE = 0x08.toByte()
        private const val LCD_1LINE = 0x00.toByte()
        private const val LCD_5x10DOTS = 0x04.toByte()
        private const val LCD_5x8DOTS = 0x00.toByte()

        // flags for backlight control
        private const val LCD_BACKLIGHT = 0x08.toByte()
        private const val LCD_NO_BACKLIGHT = 0x00.toByte()
        private const val En = 4.toByte() // Enable bit

        private const val Rw = 2.toByte() // Read/Write bit

        private const val Rs = 1.toByte() // Register select bit

        /**
         * Display row offsets. Offset for up to 4 rows.
         */
        private val LCD_ROW_OFFSETS = byteArrayOf(0x00, 0x40, 0x14, 0x54)

        private const val DEFAULT_DEVICE = 0x27
    }

    /**
     * Is backlight is on or off
     */
    private var backlight = false

    /**
     * Initializes the LCD with the backlight off
     */
    protected override fun init(i2c: I2C?) = runBlocking {
        sendLcdTwoPartsCommand(0x03.toByte())
        sendLcdTwoPartsCommand(0x03.toByte())
        sendLcdTwoPartsCommand(0x03.toByte())
        sendLcdTwoPartsCommand(0x02.toByte())

        // Initialize display settings
        sendLcdTwoPartsCommand((LCD_FUNCTION_SET or LCD_2LINE or LCD_5x8DOTS or LCD_4BIT_MODE) as Byte)
        sendLcdTwoPartsCommand((LCD_DISPLAY_CONTROL or LCD_DISPLAY_ON or LCD_CURSOR_OFF or LCD_BLINK_OFF) as Byte)
        sendLcdTwoPartsCommand((LCD_ENTRY_MODE_SET or LCD_ENTRY_LEFT or LCD_ENTRY_SHIFT_DECREMENT) as Byte)
        clearDisplay()

        // Enable backlight
        setDisplayBacklight(true)
    }

    /**
     * Turns the backlight on or off
     */
    suspend fun setDisplayBacklight(backlightEnabled: Boolean) {
        backlight = backlightEnabled
        sendCommand(if (backlight) LCD_BACKLIGHT else LCD_NO_BACKLIGHT)
    }

    /**
     * Clear the LCD and set cursor to home
     */
    suspend fun clearDisplay() {
        moveCursorHome()
        sendLcdTwoPartsCommand(LCD_CLEAR_DISPLAY)
    }

    /**
     * Returns the Cursor to Home Position (First line, first character)
     */
    suspend fun moveCursorHome() {
        sendLcdTwoPartsCommand(LCD_RETURN_HOME)
    }

    /**
     * Shuts the display off
     */
    suspend fun off() {
        sendCommand(LCD_DISPLAY_OFF)
    }

    /**
     * Write a line of text on the LCD
     *
     * @param text Text to be displayed
     * @param line linenumber of display, range: 0 .. rows-1
     */
    suspend fun displayLineOfText(text: String, line: Int) {
        displayLineOfText(text, line, 0)
    }

    /**
     * Center specified text in specified line
     * @param text Text to be displayed
     * @param line linenumber of display, range: 0 .. rows-1
     */
    suspend fun centerTextInLine(text: String, line: Int) {
        displayLineOfText(text, line, ((columns - text.length) * 0.5).toInt())
    }

    /**
     * Write a line of text on the LCD
     *
     * @param text     text to be displayed
     * @param line     line number of display, range: 0..rows-1
     * @param position start position, range: 0..columns-1
     */
    suspend fun displayLineOfText(text: String, line: Int, position: Int) {
        var text = text
        if (text.length + position > columns) {
            text = text.substring(0, columns - position)
        }
        if (line > rows || line < 0) {
        } else {
            setCursorToPosition(line, 0)
            for (i in 0 until position) {
                writeCharacter(' ')
            }
            for (character in text.toCharArray()) {
                writeCharacter(character)
            }
            for (i in 0 until columns - text.length) {
                writeCharacter(' ')
            }
        }
    }

    /**
     * Write text on the LCD starting in home position
     *
     * @param text Text to display
     */
    suspend fun displayText(text: String) {
        var currentLine = 0
        val texts = arrayOfNulls<StringBuilder>(rows)
        for (j in 0 until rows) {
            texts[j] = StringBuilder(rows)
        }
        var i = 0
        while (i < text.length) {
            if (currentLine > rows - 1) {
                break
            }
            if (text[i] == '\n') {
                currentLine++
                i++
                continue
            } else if (texts[currentLine]!!.length >= columns) {
                currentLine++
                if (text[i] == ' ') {
                    i++
                }
            }
            // append character to line
            if (currentLine < rows) {
                texts[currentLine]!!.append(text[i])
            }
            i++
        }

        //display the created texts
        for (j in 0 until rows) {
            displayLineOfText(texts[j].toString(), j)
        }
    }

    /**
     * write a character to LCD at current cursor position
     *
     * @param character  char that is written
     */
    suspend fun writeCharacter(character: Char) {
        sendLcdTwoPartsCommand(character.code.toByte(), Rs)
    }

    /**
     * write a character to lcd at a specific position
     *
     * @param character char that is written
     * @param line   row-position, Range 0 .. rows-1
     * @param pos    col-position, Range 0 .. columns-1
     */
    suspend fun writeCharacter(character: Char, line: Int, pos: Int) {
        setCursorToPosition(line, pos)
        sendLcdTwoPartsCommand(character.code.toByte(), Rs)
    }

    /**
     * displays a line on a specific position
     *
     * @param text to display
     * @param pos  for the start of the text
     */
    private suspend fun displayLine(text: String, pos: Int) {
        sendLcdTwoPartsCommand((0x80 + pos).toByte())
        for (i in 0 until text.length) {
            writeCharacter(text[i])
        }
    }

    /**
     * Clears a line of the display
     *
     * @param line line number of line to be cleared
     */
    suspend fun clearLine(line: Int) {
        require(!(line > rows || line < 1)) { "Wrong line id. Only $rows lines possible" }
        displayLine(" ".repeat(columns), LCD_ROW_OFFSETS[line - 1].toInt())
    }

    /**
     * Sets the cursor to a target destination
     *
     * @param line Selects the line of the display. Range: 0 - ROWS-1
     * @param pos  Selects the character of the line. Range: 0 - Columns-1
     */
    suspend fun setCursorToPosition(line: Int, pos: Int) {
        require(!(line > rows - 1 || line < 0)) { "Line out of range. Display has only " + rows + "x" + columns + " Characters!" }
        require(!(pos < 0 || pos > columns - 1)) { "Line out of range. Display has only " + rows + "x" + columns + " Characters!" }
        sendLcdTwoPartsCommand((LCD_SET_DDRAM_ADDR or (pos + LCD_ROW_OFFSETS[line]).toByte()))
    }

    /**
     * Create a custom character by providing the single digit states of each pixel. Simply pass an Array of bytes
     * which will be translated to a character.
     *
     * @param location  Set the memory location of the character. 1 - 7 is possible.
     * @param character Byte array representing the pixels of a character
     */
    suspend fun createCharacter(location: Int, character: ByteArray) {
        require(character.size == 8) {
            "Array has invalid length. Character is only 5x8 Digits. Only a array with length" +
                    " 8 is allowed"
        }
        require(!(location > 7 || location < 1)) { "Invalid memory location. Range 1-7 allowed. Value: $location" }
        sendLcdTwoPartsCommand((LCD_SET_CGRAM_ADDR or (location shl 3).toByte()))
        for (i in 0..7) {
            sendLcdTwoPartsCommand(character[i], 1.toByte())
        }
    }

    /**
     * Scroll whole display to the right by one column.
     */
    suspend fun scrollRight() {
        sendLcdTwoPartsCommand(LCD_SCROLL_RIGHT)
    }

    /**
     * Scroll whole display to the left by one column.
     */
    suspend fun scrollLeft() {
        sendLcdTwoPartsCommand(LCD_SCROLL_LEFT)
    }

    override suspend fun reset() {
        clearDisplay()
        off()
    }

    /**
     * Write a command to the LCD
     */
    private suspend fun sendLcdTwoPartsCommand(cmd: Byte) {
        sendLcdTwoPartsCommand(cmd, 0.toByte())
    }

    /**
     * Write a command in 2 parts to the LCD
     */
    private suspend fun sendLcdTwoPartsCommand(cmd: Byte, mode: Byte) {
        //bitwise AND with 11110000 to remove last 4 bits
        writeFourBits((mode.toInt() or (cmd.toInt() and 0xF0)).toByte())
        //bitshift and bitwise AND to remove first 4 bits
        writeFourBits((mode.toInt() or (cmd.toInt() shl 4 and 0xF0)).toByte())
    }

    /**
     * Write the four bits of a byte to the LCD
     *
     * @param data the byte that is sent
     */
    private suspend fun writeFourBits(data: Byte) {
        val backlightStatus = if (backlight) LCD_BACKLIGHT else LCD_NO_BACKLIGHT
        write((data or En or backlightStatus) as Byte)
        write((data and En.inv() or backlightStatus) as Byte)
        delay(Duration.ofNanos(50000))
    }

}