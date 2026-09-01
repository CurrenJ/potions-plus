package grill24.potionsplus.utility.neoforge;

public final class TickHandler {
    public static int ticks() {
        return Math.max(ServerTickHandler.ticksInGame, ClientTickHandler.ticksInGame);
    }
}
