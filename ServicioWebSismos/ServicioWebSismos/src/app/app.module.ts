import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ToastrModule} from 'ngx-toastr'
import {NgxPaginationModule} from 'ngx-pagination';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

//Firebase 
import{AngularFireModule} from 'angularfire2';
import {AngularFireDatabaseModule} from 'angularfire2/database';
import {environment} from '../environments/environment';

//Components 
import { SismosComponent } from './components/sismos/sismos.component';
import { SismosListComponent } from './components/sismos/sismos-list/sismos-list.component';
import { SismoComponent } from './components/sismos/sismo/sismo.component';
import { LoginComponent } from './components/login/login.component';

//Services
import { SismoService } from './services/sismo.service';
import { UsersReportsComponent } from './components/sismos/users-reports/users-reports.component';

@NgModule({
  declarations: [
    AppComponent,
    SismosComponent,
    SismosListComponent,
    SismoComponent,
    LoginComponent,
    UsersReportsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    AngularFireModule.initializeApp(environment.firebase),
    AngularFireDatabaseModule,
    FormsModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot({ timeOut: 2000, enableHtml: true }),
    NgxPaginationModule
  ],
  providers: [
    SismoService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
