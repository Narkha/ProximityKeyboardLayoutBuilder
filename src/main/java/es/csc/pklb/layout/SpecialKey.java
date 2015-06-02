package es.csc.pklb.layout;

import es.csc.pklb.frecuency.Key;

public class SpecialKey extends Key {
	private int code;
	
	public SpecialKey(int code) {
		super("");
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	@Override
	public String toString() {
		return "code: " + code;
	}
}
