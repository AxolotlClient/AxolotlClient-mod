package io.github.axolotlclient.util;

import io.github.axolotlclient.AxolotlClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
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
        WINDOWS("win"){
            @Override
            protected String[] getURLOpenCommand(URL url) {
                return new String[]{"rundll32", "url.dll,FileProtocolHandler", url.toString()};
            }
        },
        LINUX("nix", "nux", "aix"),
        MAC("mac") {
            @Override
            protected String[] getURLOpenCommand(URL url) {
                return new String[]{"open", url.toString()};
            }
        },
        OTHER();

        final String[] s;

        public String[] getStrings(){
            return s;
        }

        OperatingSystem(String... detection) {
            this.s = detection;
        }

        private void open(URL url) {
            try {
                Process process = AccessController.doPrivileged((PrivilegedExceptionAction<Process>) () -> Runtime.getRuntime().exec(this.getURLOpenCommand(url)));
                process.getInputStream().close();
                process.getErrorStream().close();
                process.getOutputStream().close();
            } catch (IOException | PrivilegedActionException var3) {
                Logger.error("Couldn't open url '{}'", url, var3);
            }

        }

        public void open(URI uri) {
            try {
                this.open(uri.toURL());
            } catch (MalformedURLException var3) {
                Logger.error("Couldn't open uri '{}'", uri, var3);
            }

        }

        protected String[] getURLOpenCommand(URL url) {
            String string = url.toString();
            if ("file".equals(url.getProtocol())) {
                string = string.replace("file:", "file://");
            }

            return new String[]{"xdg-open", string};
        }
    }
}
