package io.github.axolotlclient.api.util;

import com.google.common.base.Strings;
import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;

@UtilityClass
public class BufferUtil {

	public String getString(ByteBuf buffer, int index, int byteLength) {
		byte[] bytes = new byte[byteLength];
		buffer.getBytes(index, bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}

	public String padString(String s, int length) {
		return Strings.padEnd(s, length, Character.MIN_VALUE).substring(0, length);
	}
}
