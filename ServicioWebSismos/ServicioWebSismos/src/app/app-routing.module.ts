import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { LoginComponent } from "./components/login/login.component";
import { SismosComponent } from "./components/sismos/sismos.component";

const routes: Routes = [
  { path:'index', component: LoginComponent },
  { path:'sismos', component: SismosComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
