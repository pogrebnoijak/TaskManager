package test

import kotlinx.coroutines.runBlocking
import main.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.system.measureTimeMillis
import kotlin.random.Random

class TaskExecutorTest {
    private val taskExecutor = TaskExecutor()
    private val taskExecutorNaive = TaskExecutorNaive()
    private val listEx: MutableList<Task> = Collections.synchronizedList(mutableListOf())

    inner class TaskerTest(private val list: MutableList<Task>, tasks: Collection<Task>,
                           name: String = "") : Tasker(tasks, name) {
        override suspend fun execute() {
            list.add(this)
        }
    }

    inner class TaskerTestLong(private val list: MutableList<Task>, tasks: Collection<Task>,
                               name: String = "") : LongTasker(tasks, name) {
        override suspend fun execute() {
            list.add(this)
        }
    }

    private fun checking (listExec: List<Task>, listTask: List<Task> = emptyList()) {
        val listDone = mutableSetOf<Task>()
        listExec.forEach { task ->
            assertTrue(listDone.containsAll(task.dependencies()))
            listDone.add(task)
        }
        assertTrue(listDone.containsAll(listTask))
    }

    @BeforeEach
    fun init() {
        listEx.clear()
    }

    @Test
    fun easyTest() {
        val t1 = TaskerTest(listEx, listOf())
        val t2 = TaskerTest(listEx, listOf())
        val t3 = TaskerTest(listEx, listOf())
        val t4 = TaskerTest(listEx, listOf())
        val t5 = TaskerTest(listEx, listOf(t1, t2))
        val t6 = TaskerTest(listEx, listOf(t3, t4))
        val t7 = TaskerTest(listEx, listOf(t6, t6, t6))
        val listTask = listOf(t5, t7, t6)

        runBlocking {
            val alg = measureTimeMillis {
                taskExecutor.execute(listTask)
                assertTrue(listEx.size == 7)
                checking(listEx, listTask)
            }
            init()
            val algNaive = measureTimeMillis {
                taskExecutorNaive.execute(listTask)
                assertTrue(listEx.size == 7)
                checking(listEx, listTask)
            }
            println("easyTest: taskExecutor work $alg, taskExecutorNaive work $algNaive.")
        }
    }

    @Test
    fun testSame() {
        val listEmpty: List<Task> =
            (1..10).map { TaskerTest(listEx, listOf()) }
        val listTaker: List<Task> =
            (1..10).map { TaskerTest(listEx, listEmpty) }
        val listLongTaker: List<Task> =
            (1..10).map { TaskerTest(listEx, listTaker) }
        val listTask: MutableList<Task> = listLongTaker as MutableList<Task>
        listTask.addAll(listOf(listTaker[1], listTaker[4], listTaker[5], listEmpty[2], listEmpty[8]))

        runBlocking {
            val alg = measureTimeMillis {
                taskExecutor.execute(listTask)
                assertTrue(listEx.size == 30)
                checking(listEx, listTask)
            }
            init()
            val algNaive = measureTimeMillis {
                taskExecutorNaive.execute(listTask)
                assertTrue(listEx.size == 30)
                checking(listEx, listTask)
            }
            println("testSame: taskExecutor work $alg, taskExecutorNaive work $algNaive.")
        }
    }

    @Test
    fun bigTest() {
        generateSequence(1000) { it * 2 }.takeWhile { it <= 16000 }.forEach { kol ->
            val allTask: MutableList<Task> = (1..kol/20).map { TaskerTest(listEx, listOf()) }.toMutableList()
            for (i in kol/20..kol) {
                allTask.add(TaskerTest(listEx, (List(Random.nextInt(kol/10)) {
                    Random.nextInt(0, i)
                }.map { allTask[it] })))
            }
            val listTask = List(kol/20) { allTask[Random.nextInt(0, kol)] }

            runBlocking {
                val alg = measureTimeMillis {
                    taskExecutor.execute(listTask)
                    checking(listEx)
                }
                init()
                val algNaive = measureTimeMillis {
                    taskExecutorNaive.execute(listTask)
                    checking(listEx)
                }
                println("testBig: taskExecutor($kol) work $alg, taskExecutorNaive work $algNaive.")
            }
        }
    }
}
