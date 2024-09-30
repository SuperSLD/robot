package online.jutter

import online.jutter.robot.Robot

class Main {

    companion object {
        fun main() {
            val robot = Robot.getInstance()
            robot.loop()
        }
    }
}

fun main() {
    Main.main()
}