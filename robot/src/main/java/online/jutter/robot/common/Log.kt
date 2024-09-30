package online.jutter.robot.common

import java.util.logging.Level
import java.util.logging.Logger

fun log(text: String) {
    Logger.getLogger("robotlog").log(Level.INFO, text)
}