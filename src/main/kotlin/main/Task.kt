package main

interface Task {
    // выполняет задачу
    suspend fun execute() = run { }

    // возвращает зависимости для данной задачи
    fun dependencies(): Collection<Task>
}