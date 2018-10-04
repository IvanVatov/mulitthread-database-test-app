package info.vatov.threadsafesqlite

import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.atomic.AtomicInteger


class MultiThreadExecutor : Executor {

    private val threadPoolExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2,
            JobThreadFactory("DatabaseExecutor")) as ThreadPoolExecutor

    override fun execute(runnable: Runnable) = threadPoolExecutor.execute(runnable)

    private class JobThreadFactory(private val threadIdentifier: String) : ThreadFactory {
        private val counter = AtomicInteger()

        override fun newThread(runnable: Runnable?) =
                Thread(runnable, "thread:$threadIdentifier:${counter.incrementAndGet()}")
    }

    fun getQueueSize(): Int {
        return threadPoolExecutor.queue.size
    }

    fun getRunningThreads(): Int {
        return threadPoolExecutor.activeCount
    }
}