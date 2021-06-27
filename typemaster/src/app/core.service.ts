import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Response } from './response.model';
import { Session } from './session.model';

@Injectable({
  providedIn: 'root'
})
export class CoreService {
	private rootUrl: string = "http://95.179.136.134/typemasterserver/";

	session: Session | null = null;

	constructor(private http: HttpClient) { }
	
	init(): void{
		this.getSession();
	}	
	getAccount() : number{
		return (<Session>this.session).accountId;
	}	
		
	getSession(): void{
		this.http.get(this.rootUrl + 'getsession')
		.subscribe(response => {
			console.log(response);
			if((<Response>response).type === "success"){
				this.session = <Session>(<Response>response).payload;
			}else{
				
			}
		});

	}
	
	login(email : string, password : string, onSuccess: (accountId:number)=>void, onFailure: (message: string)=>void): void{
		this.jsonRequest("login", {email: email, password: password}, 
			(payload: any)=>{
				(<Session>this.session).accountId = <number>payload;
				onSuccess((<Session>this.session).accountId);
			},
			(message: string)=>{
				onFailure(message);
			}
		);	
	}

	register(email : string, password : string, onSuccess: (accountId:number)=>void, onFailure: (message: string)=>void): void{
		this.jsonRequest("register", {email: email, password: password}, 
			(payload: any)=>{
				(<Session>this.session).accountId = <number>payload;
				onSuccess((<Session>this.session).accountId);
			},
			(message: string)=>{
				onFailure(message);
			}
		);	
	}
	logout(onSuccess: ()=>void, onFailure: (message: string)=>void) : void{
		this.jsonRequest("logout", {}, 
			(payload: any)=>{
				(<Session>this.session).accountId = -1;
				onSuccess();
			}, 
			(message: string)=>{
				onFailure(message);
			}
		);	
	}

	
	jsonRequest(requestType: string, args: any, onSuccess: (payload: any)=>void, onFailure: (message: string) => void) : void{
		this.http.post(this.rootUrl + 'jsonrequest', {type: requestType, sessionid: (<Session>this.session).sessionId, sessionkey: (<Session>this.session).sessionKey, args: args})
		.subscribe(response => {
			console.log(response);
			if((<Response>response).type === "success"){
				onSuccess((<Response>response).payload);
			}else{
				onFailure(<string>((<Response>response).payload));
			}
		});
	}	
}
