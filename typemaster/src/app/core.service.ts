import { Injectable, EventEmitter } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Response } from './response.model';
import { Session } from './session.model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SocialAuthService, GoogleLoginProvider, FacebookLoginProvider, SocialUser } from 'angularx-social-login';

@Injectable({
  providedIn: 'root'
})
export class CoreService {
	private rootUrl: string = "https://mariuspaavel.com/typemasterserver/";

	session: Session | null = null;

	public sessionLoaded$: EventEmitter<void>;

	constructor(private http: HttpClient, private socialAuthService: SocialAuthService) {
		this.sessionLoaded$ = new EventEmitter();
		this.sessionLoaded$.subscribe(()=>{
			this.socialAuthService.authState.subscribe((user : any) => {
    				if(user == null)return;
				this.socialLoginSubmit(<SocialUser>user, (accountId: number)=>{}, (message: string)=>{});
  			});
		});
	}
	
	init(): void{
		this.getSession();	
	}	
	getAccount() : number{
		if(this.session == null){
			return -1;
		}
		return (<Session>this.session).accountId;
	}	
		
	getSession(): void{
		this.http.get(this.rootUrl + 'getsession')
		.subscribe(response => {
			console.log(response);
			if((<Response>response).type === "success"){
				this.session = <Session>(<Response>response).payload;
				this.sessionLoaded$.emit();
			}else{
						
			}
		});

	}
	
	login(email : string, password : string, onSuccess: (accountId:number)=>void, onFailure: (message: string)=>void): void{
		this.jsonRequest("login", {email: email, password: password}, 
			(payload: any)=>{
				(<Session>this.session).accountId = <number>payload;
				onSuccess((<Session>this.session).accountId);
			},
			(message: string)=>{
				onFailure(message);
			}
		);	
	}
	
	socialLoginSubmit(user: SocialUser, onSuccess: (accountId: number)=>void, onFailure: (message: string)=>void): void{
		this.jsonRequest("sociallogin", user,
			(payload: any)=>{
				(<Session>this.session).accountId = <number>payload;
				onSuccess((<Session>this.session).accountId);
			},
			(message: string)=>{
				onFailure(message);
			}
		);	
	}
	
	register(email : string, password : string, onSuccess: (accountId:number)=>void, onFailure: (message: string)=>void): void{
		this.jsonRequest("register", {email: email, password: password}, 
			(payload: any)=>{
				(<Session>this.session).accountId = <number>payload;
				onSuccess((<Session>this.session).accountId);
			},
			(message: string)=>{
				onFailure(message);
			}
		);	
	}
	logout(onSuccess: ()=>void, onFailure: (message: string)=>void) : void{
		this.jsonRequest("logout", {}, 
			(payload: any)=>{
				(<Session>this.session).accountId = -1;
				onSuccess();
			}, 
			(message: string)=>{
				onFailure(message);
			}
		);	
	}
	
	getText(onSuccess: (text: string)=>void, onFailure: (message: string)=>void){
		this.jsonRequest("gettext", {},
			(payload: any)=>{
				onSuccess(<string>payload);
			},
			(message: string)=>{
				onFailure(message);
			}
		);
	}
	
	insertRecord(timetaken: number, keystrokes: number, misses: number, onSuccess: ()=>void, onFailure: (message: string)=>void){
		this.jsonRequest("insertrecord", {timetaken:timetaken, keystrokes:keystrokes, misses:misses},
			(payload: any)=>{
				onSuccess();
			},
			(message: string)=>{
				onFailure(message);
			}
		);
	}

	getDailyStatistics(onSuccess: (averageSpeed: number, averageErrors: number)=>void, onFailure: (message: string)=>void){
		this.jsonRequest("getdailystatistics", {},
			(payload: any)=>{
				interface dailyStatistics{
					average_speed: number;
					average_errors: number;
				}
				let stats: dailyStatistics = <dailyStatistics>payload;
				onSuccess(stats.average_speed, stats.average_errors);
			},
			(message: string)=>{
				onFailure(message);
			}
		);
	}
	
	jsonRequest(requestType: string, args: any, onSuccess: (payload: any)=>void, onFailure: (message: string) => void) : void{
		this.http.post(this.rootUrl + 'jsonrequest', {type: requestType, sessionid: (<Session>this.session).sessionId, sessionkey: (<Session>this.session).sessionKey, args: args})
		.subscribe(response => {
			console.log(response);
			if((<Response>response).type === "success"){
				onSuccess((<Response>response).payload);
			}else{
				onFailure(<string>((<Response>response).payload));
			}
		},
		err=>{
			if (err.error instanceof Error) {
         			 // A client-side or network error occurred. Handle it accordingly.
          			console.error('An error occurred:', err.error.message);
        		} else {
          			// The backend returned an unsuccessful response code.
       				// The response body may contain clues as to what went wrong,
          			console.error(`Backend returned code ${err.status}, body was: ${err.error}`);
       			}
		});
	}
	googleLogin(): void{
		this.socialAuthService.signIn(GoogleLoginProvider.PROVIDER_ID)

	}
	facebookLogin(): void{
		this.socialAuthService.signIn(FacebookLoginProvider.PROVIDER_ID);	
	}	
}
