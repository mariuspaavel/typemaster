import { Component, HostListener } from '@angular/core';
import { WindowChar } from './windowchar.model'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'keyboardwarrior';

	chars: WindowChar[] = [];
		
		
	cursorpos: number = 0;
	
	roundStart: number = 0;
	roundEnd: number = 0;

	lastBlock: number= 0;
	blocked: boolean = false;

	wpm: number = 0;	
	getFormattedWpm() : string{
		return Math.round(this.wpm).toString();
	}	
	
	correct = new Audio("/assets/correct.wav");
	wrong = new Audio("/assets/wrong.wav");

	constructor() {	
	}

	ngOnInit(): void {
		this.startNewRound("Uwugoddessa");
	}

	loadString(content: string) : void{
		for(let i = 0; i < content.length; i++){
			this.chars.push(new WindowChar(content.charAt(i)));
		}
	}
	
	requestNextRound() : void{
		console.log("Requesting next round");
		this.roundEnd = Date.now();
		this.calculateWpm();
		var me = this;
		var xhr = new XMLHttpRequest();
		xhr.open("GET", "http://95.179.136.134/keyboardwarrior/next", true);
		xhr.onload = function(e){
			me.startNewRound(xhr.responseText);
		}
		xhr.send();
	}

	maxLineLength: number = 30;

	startNewRound(content: string) : void{
		if(content.length > 200)this.requestNextRound();
		this.chars = [];
		this.loadString(content);
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
	
	calculateWpm(){
		this.wpm = this.chars.length / 5 / (this.roundEnd - this.roundStart) * 60000;
	}

	
	@HostListener('window:keydown', ['$event'])
	keyEvent(event: KeyboardEvent){
		var c = event.key;
		if(c.length != 1)return;
		if(this.cursorpos >= this.chars.length)return;		
		if(this.blocked)return;
		
		if(this.cursorpos === 0 && this.chars[0].state === 0)this.roundStart = Date.now();		

		if(this.chars[this.cursorpos].value === c){	//If the expected matches the typed character
			if(this.chars[this.cursorpos].state == 0){
				this.chars[this.cursorpos].state = 1;
			}	//If the character hasn't already been missed, it is set to green
			
			this.cursorpos++;
			
			if(this.cursorpos >= this.chars.length)this.requestNextRound();
			
			this.correct.pause();
			this.correct.currentTime = 0;
			this.correct.play();
			
		}else{ 	//Checks if the user has mistakenly skipped characters
			
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
					return;
				}
			}
			this.chars[this.cursorpos].state = 2;
		}
	}

}
