package io.github.axolotlclient.util;

public class UnsupportedMod {

    private final String name;
    private final UnsupportedReason[] reason;

    public UnsupportedMod(String name, UnsupportedReason... reason) {
        this.name=name;
        this.reason=reason;
    }

    public String name() {
        return name;
    }

    public UnsupportedReason[] reason() {
        return reason;
    }

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
