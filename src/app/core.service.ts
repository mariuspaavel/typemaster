import { Injectable } from '@angular/core';
import { Account } from './account.model';

@Injectable({
  providedIn: 'root'
})
export class CoreService {
	private account: any = null;	
	

	constructor() { }
	
	setAccount(acc: Account): void{
		this.account = acc;
	}	
}
