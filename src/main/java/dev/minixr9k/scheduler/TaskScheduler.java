package dev.minixr9k.scheduler;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class TaskScheduler {

    private final AtomicInteger idCounter = new AtomicInteger(1);

    private final Queue<CoreTask> pendingTasks = new ConcurrentLinkedQueue<>();
    private final Queue<CoreTask> syncTasks = new ConcurrentLinkedQueue<>();

    private final ExecutorService asyncExecutor = Executors.newCachedThreadPool();

    public CoreTask runTaskLater(Runnable runnable, long delayTicks) {
        return runTaskTimer(task -> runnable.run(), delayTicks, 0);
    }

    public CoreTask runTaskTimer(Consumer<CoreTask> action, long delayTicks, long periodTicks) {
        CoreTask task = new CoreTask(idCounter.getAndIncrement(), action, false, delayTicks, periodTicks);
        pendingTasks.add(task);
        return task;
    }

    public CoreTask runTaskAsync(Runnable runnable) {
        return runTaskAsyncLater(runnable, 0);
    }

    public CoreTask runTaskAsyncLater(Runnable runnable, long delayTicks) {
        CoreTask task = new CoreTask(idCounter.getAndIncrement(), t -> runnable.run(), true, delayTicks, 0);

        if (delayTicks <= 0) {
            asyncExecutor.execute(() -> task.tick());
        } else {
            pendingTasks.add(task);
        }
        return task;
    }

    public void tick() {
        // Переносим новые задачи в основной список
        CoreTask pending;
        while ((pending = pendingTasks.poll()) != null) {
            syncTasks.add(pending);
        }

        Iterator<CoreTask> iterator = syncTasks.iterator();
        while (iterator.hasNext()) {
            CoreTask task = iterator.next();

            if (task.isCancelled()) {
                iterator.remove();
                continue;
            }

            if (task.isAsync()) {
                // Если асинхронная задача подошла по задержке
                if (task.tick()) {
                    asyncExecutor.execute(() -> task.tick());
                }
                iterator.remove();
            } else {
                boolean keepAlive = task.tick();
                if (!keepAlive) {
                    iterator.remove();
                }
            }
        }
    }

    public void shutdown() {
        asyncExecutor.shutdown();
        try {
            if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                asyncExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            asyncExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}