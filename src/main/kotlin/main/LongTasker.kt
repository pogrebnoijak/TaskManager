package main

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

open class LongTasker(tasks: Collection<Task>, name: String = ""): Tasker(tasks, name) {
    override fun execute() = runBlocking {
        delay(10)
        super.execute()
    }
}
