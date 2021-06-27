export class Session{
	sessionId: number;
	sessionKey: bigint;
	accountId: number = -1;
	constructor(sessionId: number, sessionKey: bigint){
		this.sessionId = sessionId;
		this.sessionKey = sessionKey;
	}
}
