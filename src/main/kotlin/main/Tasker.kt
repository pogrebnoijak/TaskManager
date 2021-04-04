package main

open class Tasker(private val tasks: Collection<Task>, private val name: String = ""): Task {
    override suspend fun execute() = println("Task $name execute")

    override fun dependencies(): Collection<Task> = tasks
}