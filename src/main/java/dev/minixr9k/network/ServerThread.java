package dev.minixr9k.network;

import dev.minixr9k.MiniCore;

public class ServerThread extends Thread {

    private volatile boolean isRunning = false;
    private long targetNanosPerTick = 50_000_000L;
    private long lastTime = System.nanoTime();

    private long lastTickExecutionTimeNanos = 0;
    private double currentMspt = 0.0;

    public ServerThread() {
        super("ServerThread");
    }

    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            long now = System.nanoTime();
            long elapsed = now - lastTime;

            if (elapsed >= targetNanosPerTick) {
                lastTime = now;

                long tickStart = System.nanoTime();

                MiniCore.getScheduler().tick();

                lastTickExecutionTimeNanos = System.nanoTime() - tickStart;

                double lastMspt = lastTickExecutionTimeNanos / 1_000_000.0;
                currentMspt = (currentMspt * 0.95) + (lastMspt * 0.05);
            } else {
                try {
                    long sleepMillis = (targetNanosPerTick - elapsed) / 1_000_000L;
                    if (sleepMillis > 0) {
                        Thread.sleep(sleepMillis);
                    }
                } catch (InterruptedException ignored) {}
            }
        }
    }

    public double getMspt() {
        return currentMspt;
    }

    public double getTps() {
        return currentMspt <= 50.0 ? 20.0 : 1000.0 / currentMspt;
    }

    public void shutdown() {
        this.isRunning = false;
        this.interrupt();
    }

}
