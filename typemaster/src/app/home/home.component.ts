import { Component, OnInit, HostListener} from '@angular/core';
import { WindowChar } from './windowchar.model';
import { CoreService } from '../core.service';
import { Session } from '../session.model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

 	chars: WindowChar[] = [];
		
		
	cursorpos: number = 0;
	
	roundStart: number = 0;
	roundEnd: number = 0;

	keyStrokes: number = 0;
	misses: number = 0;

	lastBlock: number= 0;
	blocked: boolean = false;

	wpm: number = 0;	
	errors: number = 0;

	averageSpeed: number = 0;
	averageErrors: number = 0;	
	
	correct = new Audio("/assets/correct.wav");
	wrong = new Audio("/assets/wrong.wav");

	constructor(private coreService: CoreService) {
		this.coreService.sessionLoaded$.subscribe(()=>{this.requestNextRound();});	
	}

	ngOnInit(): void {
		if(this.coreService.session != null)this.requestNextRound();
	}

	isLoggedIn(): boolean{
		return this.coreService.getAccount() != -1;
	}

	loadString(content: string) : void{
		for(let i = 0; i < content.length; i++){
			this.chars.push(new WindowChar(content.charAt(i)));
		}
	}
	
	requestNextRound() : void{
		
		this.coreService.getText(
			(text: string)=>{
				this.startNewRound(text);
			},
			(message: string)=>{}
		);	
		
		if(this.isLoggedIn())this.coreService.getDailyStatistics(
			(averageSpeed: number, averageErrors: number)=>{
				this.averageSpeed = averageSpeed;
				this.averageErrors = averageErrors;
				console.log(this.averageSpeed);
				console.log(this.averageErrors);
			},
			(message: string)=>{

			}
		);
	}

	maxLineLength: number = 30;

	startNewRound(content: string) : void{
		this.chars = [];
		this.loadString(content);
		
		this.keyStrokes = 0; 
		this.misses = 0;

		this.roundStart = new Date().getTime();


		this.cursorpos = 0;
		for(var i = 0, row = 0, column = 0; i < this.chars.length; i++, column++){
			this.chars[i].row = row;
			this.chars[i].column = column;
			if(this.chars[i].value === ' '){
				var potentialColumn = column;
				for(var j = i+1; j < this.chars.length; j++, potentialColumn++){
					if(potentialColumn >= this.maxLineLength){
						row++;
						column = -1;
						break;
					}
					if(this.chars[j].value === ' '){
						break;
					}
				}
			}
		}
	}
	
	calculateWpm() : void{
		console.log("Calculating wpm");
		this.wpm = this.chars.length / 5 / (this.roundEnd - this.roundStart) * 60000;
	}

	
	@HostListener('window:keydown', ['$event'])
	keyEvent(event: KeyboardEvent){
		var c = event.key;
		if(c.length != 1)return;
		if(this.cursorpos >= this.chars.length)return;		
		if(this.blocked)return;
		
		this.keyStrokes++;

		if(this.cursorpos === 0 && this.chars[0].state === 0)this.roundStart = Date.now();		

		if(this.chars[this.cursorpos].value === c){	//If the expected matches the typed character
			if(this.chars[this.cursorpos].state == 0){
				this.chars[this.cursorpos].state = 1;
			}	//If the character hasn't already been missed, it is set to green
			
			this.cursorpos++;
			
			if(this.cursorpos >= this.chars.length)this.endRound();
			
			this.correct.pause();
			this.correct.currentTime = 0;
			this.correct.play();
			
		}else{ 	//Checks if the user has mistakenly skipped characters
			this.misses++;
			this.wrong.play();
			this.lastBlock = Date.now();
			this.blocked = true;
			var me = this;
			setTimeout(() => {if(Date.now()-700 > me.lastBlock)me.blocked = false;}, 800);
			var checkBarrier = Math.min(this.cursorpos+2, this.chars.length);	//Establishes a barrier that is either 2 characters ahead or at the end of the string
			for(var i = this.cursorpos+1; i < checkBarrier; i++){
				if(this.chars[i].value === c){
					for(var j = this.cursorpos; j < i; j++){
						this.chars[j].state = 2;
					}
					this.chars[i].state = 1;
					this.cursorpos = i+1;
					if(this.cursorpos >= this.chars.length){
						this.endRound();
					}
					return;
				}
			}
			this.chars[this.cursorpos].state = 2;
		}
	}
	endRound(): void{
		this.roundEnd = new Date().getTime();
		if(this.isLoggedIn())this.coreService.insertRecord(
			this.roundEnd-this.roundStart, 
			this.keyStrokes, 
			this.misses, 
			()=>{}, 
			(message)=>{}
		);
		this.calculateWpm();
		this.errors = this.misses;
		this.requestNextRound();
	}

}
