package io.github.axolotlclient.api.util;

import lombok.Data;

@Data
public class BiContainer<A, B> {

	private A left;
	private B right;

	private BiContainer(A left, B right){
		this.left = left;
		this.right = right;
	}

	public void set(A left, B right) {
		this.left = left;
		this.right = right;
	}

	public static <A, B> BiContainer<A, B> empty(){
		return of(null, null);
	}

	public static <A, B> BiContainer<A, B> of(A left, B right){
		return new BiContainer<>(left, right);
	}
}
