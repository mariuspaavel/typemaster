import { Component, OnInit, ViewChild } from '@angular/core';
import { ApiconnectionService } from '../apiconnection.service';
import { CoreService } from '../core.service';
import { ErrorMessageComponent } from '../error-message/error-message.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
	email: string = "";
	password: string = "";
	passwordconfirm: string = "";
	
	@ViewChild(ErrorMessageComponent) errormessage!: ErrorMessageComponent;

	constructor(private apiconnection: ApiconnectionService, private coreService: CoreService, private router: Router) { }

	ngOnInit(): void {
	}
	
	submit(): void{
		if(this.passwordconfirm !== this.password)this.onError("Passwords don't match.");
		this.apiconnection.register(this.email, this.password, (accoutnId: number)=>{this.router.navigate(['']);}, (message: string)=>{this.onError(message)});
	}
	onError(message: string): void{
		this.errormessage.popup(message);	
	}
}
