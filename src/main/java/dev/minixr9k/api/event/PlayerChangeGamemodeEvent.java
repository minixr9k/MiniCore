package dev.minixr9k.api.event;

import dev.minixr9k.api.Cancellable;
import dev.minixr9k.api.Event;
import dev.minixr9k.types.GameMode;
import dev.minixr9k.types.Player;

public class PlayerChangeGamemodeEvent extends Event implements Cancellable {

    private final Player player;
    private final GameMode gameMode;
    private boolean cancelled;

    public PlayerChangeGamemodeEvent(Player player, GameMode gameMode) {
        this.player = player;
        this.gameMode = gameMode;
    }

    public Player getPlayer() {
        return player;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
