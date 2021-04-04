package main

import kotlinx.coroutines.delay

open class LongTasker(tasks: Collection<Task>, name: String = ""): Tasker(tasks, name) {
    override suspend fun execute() {
        delay(100)
        super.execute()
    }
}