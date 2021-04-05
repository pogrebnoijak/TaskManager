package main

// assume that the graph is acyclic
class TaskExecutorNaive {
    private val set: MutableSet<Task> = mutableSetOf()

    fun execute(tasks: Collection<Task>) {
        set.clear()
        return dfs(object : Task { override fun dependencies() = tasks })
    }

    private fun dfs(task: Task) {
        task.dependencies().forEach {
            if (!set.contains(it)) {
                set.add(it)
                dfs(it)
            }
        }
        task.execute()
    }
}
