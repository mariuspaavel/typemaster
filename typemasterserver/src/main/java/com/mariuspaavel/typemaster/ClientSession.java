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
		return accountId != -1;
	}
	public ClientSession(int sessionId, long sessionKey){
		this.sessionId = sessionId;
		this.sessionKey = sessionKey;
		this.accountId = -1;
	}
	public void setAccountId(int accountId){
		this.accountId = accountId;
	}
	public Map<String, Object> toJson(){
		Map<String, Object> result = new HashMap<>();
		result.put("sessionId", sessionId);
		result.put("sessionKey", sessionKey);
		result.put("accountId", accountId)l
		return result;
	}
}
