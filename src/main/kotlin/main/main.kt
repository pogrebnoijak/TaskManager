package main

fun main() {
    val taskExecutor = TaskExecutor()
    val taskExecutorNaive = TaskExecutorNaive()

    val emptyTask = Tasker(emptyList(), "1")
    val emptyTask2 = Tasker(emptyList(), "2")
    val emptyTask3 = Tasker(emptyList(), "3")
    val emptyTask4 = Tasker(emptyList(), "4")
    val task = Tasker(listOf(emptyTask, emptyTask2), "5")
    val task2 = Tasker(listOf(emptyTask3, emptyTask4), "6")
    val task3 = Tasker(listOf(task2, task2, task2), "7")

    taskExecutor.execute(listOf(task, task3, task2))
    println()
    taskExecutorNaive.execute(listOf(task, task3, task2))
}
