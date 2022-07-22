package io.github.axolotlclient.util;

import java.util.Locale;

public class OSUtil {

    private static OperatingSystem OS;

    public static OperatingSystem getOS() {
        if(OS==null) {
            String s = System.getProperty("os.name");

            for (OperatingSystem o : OperatingSystem.values()) {
                for (String v : o.getStrings()) {
                    if (s.trim().toLowerCase(Locale.ROOT).contains(v)) {
                        OS = o;
                        return OS;
                    }
                }
            }

            OS = OperatingSystem.OTHER;
        }
        return OS;
    }

    public enum OperatingSystem {
        WINDOWS("win"),
        LINUX("nix", "nux", "aix"),
        MAC("mac"),
        OTHER();

        final String[] s;

        public String[] getStrings(){
            return s;
        }

        OperatingSystem(String... detection) {
            this.s = detection;
        }
    }
}
