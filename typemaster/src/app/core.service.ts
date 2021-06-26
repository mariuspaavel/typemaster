import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Response } from './response.model';

@Injectable({
  providedIn: 'root'
})
export class CoreService {
	private rootUrl: string = "http://95.179.136.134/typemasterserver/";



	private accountId: number = -1;	

	constructor(private http: HttpClient) { }
	
	init(): void{
		this.getStatus();
	}	
	getAccount() : number{
		return this.accountId;
	}
	setAccount(accountId: number): void{
		this.accountId = accountId;
	}	
		
	getStatus(): void{
		this.http.post(this.rootUrl + 'jsonrequest', {type: "status"})
		.subscribe(response => {
			console.log(response);
			if((<Response>response).type === "success"){
				let accountId: number = <number>(<Response>response).payload;
				this.setAccount(accountId);
			}else{
				this.setAccount(-1);
			}
		});

	}
	
	login(email : string, password : string, onSuccess: (accountId:number)=>void, onFailure: (message: string)=>void): void{
		this.http.post(this.rootUrl + 'jsonrequest', {type: "login", email: email, password: password})
		.subscribe(response => {
			console.log(response);
			if((<Response>response).type === "success"){
				let accountId: number = <number>(<Response>response).payload;
				this.setAccount(accountId);
				onSuccess(accountId);
			}else{
				onFailure(<string>((<Response>response).payload));
			}
		});
	}

	register(email : string, password : string, onSuccess: (accountId:number)=>void, onFailure: (message: string)=>void): void{
		this.http.post(this.rootUrl + 'jsonrequest', {type: "register", email: email, password: password})
		.subscribe(response => {
			console.log(response);
			if((<Response>response).type === "success"){
				let accountId: number = <number>(<Response>response).payload;
				this.setAccount(accountId);
				onSuccess(accountId);
			}else{
				onFailure(<string>((<Response>response).payload));
			}
		});
	}
	logout(onSuccess: ()=>void, onFailure: (message: string)=>void) : void{
		this.http.post(this.rootUrl + 'jsonrequest', {type: "logout"})
		.subscribe(response => {
			console.log(response);
			if((<Response>response).type === "success"){
				this.setAccount(-1);
				onSuccess();
			}else{
				onFailure(<string>((<Response>response).payload));
			}
		});
	}
		
}
