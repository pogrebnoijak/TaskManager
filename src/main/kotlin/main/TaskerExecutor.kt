package main

import kotlinx.coroutines.*
import java.util.Collections.synchronizedMap

// assume that the graph is acyclic
class TaskExecutor {
    private val synMap: MutableMap<Task, Pair<Deferred<Task>, Boolean>> = synchronizedMap(mutableMapOf())

    suspend fun execute(tasks: Collection<Task>): Task {
        synMap.clear()
        return bfs(object : Task { override fun dependencies() = tasks })
    }

    private suspend fun bfs(task: Task): Task {
        val job = GlobalScope.launch {
            task.dependencies().map {
                if (!synMap.containsKey(it)) {
                    async { bfs(it) }.also { exTask ->
                        synMap[it] = Pair(exTask, false)
                    }
                }
                else synMap[it]!!.first
            }.forEach { dTask ->
                dTask.await().let {
                    if(synMap[it]?.second == false) {
                        synMap[it] = Pair(dTask, true)
                        it.execute()
                    }
                }
            }
        }
        job.join()
        return task
    }
}
