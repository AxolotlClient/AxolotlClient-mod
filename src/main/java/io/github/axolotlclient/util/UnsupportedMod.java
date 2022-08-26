package io.github.axolotlclient.util;

public record UnsupportedMod(String name,
                             io.github.axolotlclient.util.UnsupportedMod.UnsupportedReason... reason) {

    public enum UnsupportedReason {
        BAN_REASON("be bannable on lots of servers"),
        CRASH("crash your game"),
        MIGHT_CRASH("have effects that could crash your game"),
        UNKNOWN_CONSEQUENSES("have unknown consequences in combination with this mod");

        private final String description;

        UnsupportedReason(String desc) {
            description = desc;
        }

        @Override
        public String toString() {
            return description;
        }
    }
}
