package main

import java.util.Collections.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// assume that the graph is acyclic
class TaskExecutor(private val nTreads: Int = 4) {
    private val setAll: MutableSet<Task> = mutableSetOf()
    private val setDone: MutableSet<Task> = synchronizedSet(mutableSetOf())
    private val mapParent: MutableMap<Task, MutableList<() -> Unit>> = mutableMapOf()
    private lateinit var executor : ExecutorService

    fun execute(tasks: Collection<Task>) {
        setAll.clear()
        setDone.clear()
        executor = Executors.newFixedThreadPool(nTreads)
        val latch = CountDownLatch(1)
        dfs(object : Task { override fun dependencies() = tasks }, latch)
        latch.await()
        executor.shutdown()
        executor.awaitTermination(1, TimeUnit.DAYS)
    }

    private fun dfs(task: Task, latchParent: CountDownLatch) {
        val latch = CountDownLatch(task.dependencies().size)
        task.dependencies().forEach {
            synchronized(setAll) {
                if (!setAll.contains(it)) {
                    setAll.add(it)
                    dfs(it, latch)
                } else synchronized(setDone) {
                    if (setDone.contains(it)) {
                        latch.countDown()
                    } else {
                        synchronized(mapParent) {
                            mapParent.getOrPut(it) { mutableListOf() }
                                .add { latch.countDown() }
                        }
                    }
                }
            }
        }
        latch.await()
        executor.submit {
            task.execute()
            setDone.add(task)
            mapParent[task]?.forEach { it() }
            latchParent.countDown()
        }
    }
}
