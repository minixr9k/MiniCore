package dev.minixr9k.api;

import java.lang.reflect.Method;

public class RegisteredListener implements Comparable<RegisteredListener> {
    private final EventListener listener; // Объект класса плагина (где лежит метод)
    private final Method method;          // Сам метод с аннотацией @CatchEvent
    private final int priority;           // Приоритет (чем меньше число, тем раньше вызов)

    public RegisteredListener(EventListener listener, Method method, int priority) {
        this.listener = listener;
        this.method = method;
        this.priority = priority;

        this.method.setAccessible(true);
    }

    public void execute(Event event) throws Exception {
        method.invoke(listener, event);
    }

    @Override
    public int compareTo(RegisteredListener o) {
        // Сортировка по приоритету
        return Integer.compare(this.priority, o.priority);
    }
}