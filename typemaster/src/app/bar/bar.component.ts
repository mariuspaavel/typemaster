import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CoreService } from '../core.service';

@Component({
  selector: 'app-bar',
  templateUrl: './bar.component.html',
  styleUrls: ['./bar.component.css', '../app.component.css']
})
export class BarComponent implements OnInit {

  constructor(private router: Router, private coreService: CoreService) { }

  ngOnInit(): void {
  }
	loggedIn(): boolean{
		return this.coreService.getAccount() !== -1;
	}
	
	login(): void{
		this.router.navigate(['/login']);
	}
	register(): void{
		this.router.navigate(['/register']);	
	}
	logout(): void{
		this.coreService.logout(()=>{}, ()=>{});
	}
}
