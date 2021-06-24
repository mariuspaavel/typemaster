import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Account } from './account.model';
import { Response } from './response.model';
import { CoreService } from './core.service';

@Injectable({
  providedIn: 'root'
})
export class ApiconnectionService {
	
	private rootUrl: string = "95.179.136.134/keyboardwarrior/";

	constructor(private http: HttpClient, private coreService: CoreService) { }

	login(email : string, password : string, onSuccess: ()=>void, onFailure: (message: string)=>void): void{
		this.http.post(this.rootUrl + 'jsonreq', {type: "login", email: email, password: password})
		.subscribe(response => {
			console.log(response);
			if((<Response>response).type === "success"){
				this.coreService.setAccount(<Account>(<Response>response).payload);
				onSuccess();
			}else{
				onFailure(<string>((<Response>response).payload));
			}
		});
	}
}
