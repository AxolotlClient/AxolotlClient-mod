package io.github.axolotlclient.api.util;

import io.github.axolotlclient.api.requests.StatusUpdate;

public interface StatusUpdateProvider {

	StatusUpdate getStatus();
}
