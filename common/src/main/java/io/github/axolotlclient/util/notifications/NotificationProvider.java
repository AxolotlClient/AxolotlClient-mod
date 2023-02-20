package io.github.axolotlclient.util.notifications;

public interface NotificationProvider {

	void addStatus(String titleKey, String descKey, Object... args);
}
