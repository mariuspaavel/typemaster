package com.mariuspaavel.typemaster;

public class UnknownRequestException extends RuntimeException{
	public UnknownRequestException(){
		super("Unknown request");
	}	
}
