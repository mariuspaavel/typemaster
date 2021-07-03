import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { Routes,  RouterModule } from "@angular/router";
import { FormsModule } from "@angular/forms";
import { HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { BarComponent } from './bar/bar.component';
import { ErrorMessageComponent } from './error-message/error-message.component';

import { ReactiveFormsModule } from '@angular/forms';
import { SocialLoginModule, SocialAuthServiceConfig } from 'angularx-social-login';
import { GoogleLoginProvider, FacebookLoginProvider } from 'angularx-social-login';


const appRoutes: Routes = [
	{path: '', component: HomeComponent},
	{path: 'login', component: LoginComponent},
	{path: 'register', component: RegisterComponent}
];

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LoginComponent,
    RegisterComponent,
    BarComponent,
    ErrorMessageComponent
  ],
  imports: [
  	BrowserModule,
	FormsModule,
	HttpClientModule,
	RouterModule.forRoot(appRoutes),
	SocialLoginModule	
  ],
  providers: [{
 provide: 'SocialAuthServiceConfig',
      useValue: {
        autoLogin: false,
        providers: [
          {
            id: GoogleLoginProvider.PROVIDER_ID,
            provider: new GoogleLoginProvider(
              '738936194228-1npnv3bl0l6t132vqk81nsfhsp66jmf3.apps.googleusercontent.com'
            )
          },
           {
            id: FacebookLoginProvider.PROVIDER_ID,
            provider: new FacebookLoginProvider('2591152074511801')
          } 
        ]
      } as SocialAuthServiceConfig,

}],
  bootstrap: [AppComponent]
})
export class AppModule { }
