import { Component, Inject, OnInit } from '@angular/core';
import { GSNUser, LoginService } from './services/login.service';
import { DOCUMENT } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  title = 'gsn-webui-frontend';

  constructor(
    @Inject(DOCUMENT) private document: Document,
    private route: ActivatedRoute,
    private loginService: LoginService,
    private http: HttpClient) { }

  login_url : string = ''
  user : GSNUser = new GSNUser()

  ngOnInit(): void {

    // get stored user credentials
    const storedUser = localStorage.getItem('user');
    this.user = storedUser ? JSON.parse(storedUser) : null;
    
    // if redirected from login
    this.route.queryParams
      .subscribe(params => {
        if(params['response_type']){
          var code = params['code']
          var response_type = params['response_type']
          var user_name = params['user_name']
          var user_email = params['user_email']

          var query_params = "?code=" + code + "&response_type=" + response_type + 
          "&user_name" + user_name + "&user_email" + user_email;
          console.log("query_params", query_params);

          // creates profile after logging in
          this.loginService.createProfile(query_params).subscribe((data: GSNUser) => {
            this.user = data;
            localStorage.setItem('user', JSON.stringify(this.user));
          }, (error: any) => {
            console.error(error);
          });
        }
      }
    );
  }

  login(): void {
    this.loginService.getLoginUrl().subscribe((data: any) => {
      this.document.location.href = data.url;
    }, (error: any) => {
      console.error(error);
    });
  }

  logout(): void {
    localStorage.removeItem('user');
    this.user = new GSNUser();
    this.user.logged_in = false;

    this.http.get('http://localhost:8000/logout/', {withCredentials: true}).subscribe(
      (data: any) => {
       console.log(data);
      },
      error => {
        console.log(error)
        // Handle error
      }
    );

  }

}