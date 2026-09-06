package grill24.potionsplus.utility;

public final class ServerTickHandler {
    private ServerTickHandler() {}

    public static int ticksInGame = 0;

    public static float getTicks() {
        return ticksInGame;
    }

    public static void increment() {
        ticksInGame++;
    }
}
