import { Component, HostListener } from '@angular/core';
import { CoreService } from './core.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  	title = 'typemaster';
	constructor(private coreService: CoreService) {	
	}

	ngOnInit(): void {
		this.coreService.init();
	}
}
