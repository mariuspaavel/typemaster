import { Component, OnInit, ViewChild } from '@angular/core';
import { ApiconnectionService } from '../apiconnection.service'
import { ErrorMessageComponent } from '../error-message/error-message.component';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
	email: string = "";
	password: string = "";
	
	@ViewChild(ErrorMessageComponent) errormessage!: ErrorMessageComponent;

	constructor(private apiconnection: ApiconnectionService) { }

	ngOnInit(): void {
	}
	
	submit(): void{
		this.apiconnection.login(this.email, this.password, ()=>{}, this.onError);
	}
	onError(message: string): void{
		this.errormessage.popup(message);	
	}
}
