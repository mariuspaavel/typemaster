import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Account } from './account.model';
import { Response } from './response.model';
import { CoreService } from './core.service';

@Injectable({
  providedIn: 'root'
})
export class ApiconnectionService {
	
	private rootUrl: string = "http://95.179.136.134/typemasterserver/";

	constructor(private http: HttpClient, private coreService: CoreService) { }

	login(email : string, password : string, onSuccess: (accountId:number)=>void, onFailure: (message: string)=>void): void{
		this.http.post(this.rootUrl + 'jsonrequest', {type: "login", email: email, password: password})
		.subscribe(response => {
			console.log(response);
			if((<Response>response).type === "success"){
				let accountId: number = <number>(<Response>response).payload;
				this.coreService.setAccount(accountId);
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
				this.coreService.setAccount(accountId);
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
				this.coreService.setAccount(-1);
				onSuccess();
			}else{
				onFailure(<string>((<Response>response).payload));
			}
		});
	}
}
