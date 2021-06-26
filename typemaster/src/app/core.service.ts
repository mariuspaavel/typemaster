import { Injectable } from '@angular/core';
import { Account } from './account.model';

@Injectable({
  providedIn: 'root'
})
export class CoreService {
	private accountId: number = -1;	

	constructor() { }
	
	getAccount() : number{
		return this.accountId;
	}
	setAccount(accountId: number): void{
		this.accountId = accountId;
	}
		
}
