package main

// assume that the graph is acyclic
class TaskExecutorNaive {
    val set = mutableSetOf<Task>()

    fun execute(tasks: Collection<Task>) =  dfs(object : Task { override fun dependencies() = tasks })

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
