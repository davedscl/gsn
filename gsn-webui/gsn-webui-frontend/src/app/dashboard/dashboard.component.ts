import { Component, Inject, OnInit } from '@angular/core';
import { DashboardService } from '../services/dashboard.service';
import { FavoritesService } from '../services/favorites.service';
import { LoginService } from '../services/login.service';
import { DOCUMENT } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  constructor(
    @Inject(DOCUMENT) private document: Document,
    private dashboardService : DashboardService,
    private favoritesService : FavoritesService,
    private loginService : LoginService
  ) { }

  sensors: { [key: string]: any } = {};
  
  ngOnInit(): void {
    this.getSensorDashboard();
  }

  getSensorDashboard(): void { 
    this.dashboardService.dashboard().subscribe((data: any) => {
      this.sensors = data
    }, (error: any) => {
      if(error.status == 302){
        console.log(error)
        this.login();
      }else{
        console.error(error);
      }
    });
  }


  removeFavorite(favorite : string) : void {
    this.favoritesService.remove(favorite).subscribe((data: any) => {
      delete this.sensors[favorite];
    }, (error: any) => {
      if(error.status == 302){
        console.log(error)
        this.login();
      }else{
        console.error(error);
      }
    });
  }

  login(): void {
    this.loginService.getLoginUrl().subscribe((data: any) => {
      this.document.location.href = data.url;
    }, (error: any) => {
      console.error(error);
    });
  }


}
