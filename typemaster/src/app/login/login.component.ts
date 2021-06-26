import { Component, OnInit, ViewChild } from '@angular/core';
import { CoreService } from '../core.service';
import { ErrorMessageComponent } from '../error-message/error-message.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
	email: string = "";
	password: string = "";
	
	@ViewChild(ErrorMessageComponent) errormessage!: ErrorMessageComponent;

	constructor(private coreService: CoreService, private router: Router) { }

	ngOnInit(): void {
	}
	
	submit(): void{
		this.coreService.login(this.email, this.password, (accountId: number)=>{this.router.navigate(['']);}, (message: string)=>{this.onError(message)});
	}
	onError(message: string): void{
		this.errormessage.popup(message);	
	}
}
