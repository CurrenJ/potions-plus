package grill24.potionsplus.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class Platform {
    @ExpectPlatform
    public static boolean isClient() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isDevelopmentEnvironment() {
        throw new AssertionError();
    }
}
