package online.jutter.hardware.structures

class CommandsQueue {

    private val list = mutableListOf<()->Unit>()

    fun put(item: ()->Unit) {
        list.add(item)
    }

    fun get() = list.removeFirst()

    fun empty() = list.size == 0

    fun notEmpty() = list.size != 0
}