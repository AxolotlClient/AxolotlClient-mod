package io.github.axolotlclient.api.util;

import java.time.Instant;

import io.netty.buffer.ByteBuf;

public class InstantSerializer implements Serializer<Instant> {

	@Override
	public ByteBuf serialize(Instant instant) {
		return BufferUtil.wrap(instant.getEpochSecond());
	}

	@Override
	public Instant deserialize(ByteBuf buf) {
		return Instant.ofEpochSecond(buf.readLong());
	}
}
