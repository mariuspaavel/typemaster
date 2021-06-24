package com.mariuspaavel.typemaster;

public class ClientSession{
	private int sessionId;
	private long sessionKey;
	private int accountId;
	
	public int getSessionId(){
		return sessionId;
	}
	public long getSessionKey(){
		return sessionKey;
	}
	public int getAccountId(){
		return accountId;
	}
	public boolean isLoggedIn(){
		return accountId != 0;
	}
	public ClientSession(int sessionId, long sessionKey){
		this.sessionId = sessionId;
		this.sessionKey = sessionKey;
	}
	public void setAccountId(int accountId){
		this.accountId = accountId;
	}
}
