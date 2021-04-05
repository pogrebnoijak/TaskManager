package main

interface Task {
    // выполняет задачу
    fun execute() = run { }

    // возвращает зависимости для данной задачи
    fun dependencies(): Collection<Task>
}