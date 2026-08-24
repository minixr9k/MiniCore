package dev.minixr9k.scheduler;

import java.util.function.Consumer;

public class CoreTask {

    private final int taskId;
    private final Consumer<CoreTask> taskAction;
    private final boolean async;
    private long delay;
    private final long period;
    private volatile boolean cancelled = false;

    public CoreTask(int taskId, Consumer<CoreTask> taskAction, boolean async, long delay, long period) {
        this.taskId = taskId;
        this.taskAction = taskAction;
        this.async = async;
        this.delay = delay;
        this.period = period;
    }

    public int getTaskId() {
        return taskId;
    }

    public boolean isAsync() {
        return async;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }

    public boolean tick() {
        if (cancelled) return false;

        if (delay > 0) {
            delay--;
            return true;
        }

        // Выполняем задачу
        try {
            taskAction.accept(this);
        } catch (Throwable t) {
            System.err.println("[Scheduler] Ошибка при выполнении задачи #" + taskId);
            t.printStackTrace();
        }

        // Если задача повторяющаяся — перезапускаем delay
        if (period > 0) {
            delay = period - 1; // -1 т.к. этот тик уже прошел
            return true;
        }

        // Одноразовая задача завершилась
        return false;
    }
}