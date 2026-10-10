package dev.minixr9k.network;

import dev.minixr9k.MiniCore;
import java.util.concurrent.locks.LockSupport;

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

                try {
                    MiniCore.getScheduler().tick();
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                lastTickExecutionTimeNanos = System.nanoTime() - tickStart;

                double lastMspt = lastTickExecutionTimeNanos / 1_000_000.0;
                currentMspt = (currentMspt * 0.95) + (lastMspt * 0.05);
            } else {
                long timeLeft = targetNanosPerTick - (System.nanoTime() - lastTime);
                if (timeLeft > 1_000_000L) {
                    LockSupport.parkNanos(timeLeft - 500_000L);
                } else if (timeLeft > 0) {
                    Thread.yield();
                }
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
