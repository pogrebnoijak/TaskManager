package main

import kotlinx.coroutines.delay

class LongTasker(tasks: Collection<Task>, private val name: String = ""): Tasker(tasks, name) {
    override suspend fun execute() {
        delay(100)
        println("Task $name execute")
    }
}