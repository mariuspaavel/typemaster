import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { Routes,  RouterModule } from "@angular/router";
import { FormsModule } from "@angular/forms";
import { HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { BarComponent } from './bar/bar.component';
import { ErrorMessageComponent } from './error-message/error-message.component';

const appRoutes: Routes = [
	{path: '', component: HomeComponent},
	{path: 'login', component: LoginComponent}
];

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LoginComponent,
    BarComponent,
    ErrorMessageComponent
  ],
  imports: [
  	BrowserModule,
	FormsModule,
	HttpClientModule,
	RouterModule.forRoot(appRoutes)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
