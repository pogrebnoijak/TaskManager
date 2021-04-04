package main

// assume that the graph is acyclic
class TaskExecutorNaive {
    private val set: MutableSet<Task> = mutableSetOf()

    suspend fun execute(tasks: Collection<Task>) {
        set.clear()
        return dfs(object : Task { override fun dependencies() = tasks })
    }

    private suspend fun dfs(task: Task) {
        task.dependencies().forEach {
            if (!set.contains(it)) {
                set.add(it)
                dfs(it)
            }
        }
        task.execute()
    }
}
