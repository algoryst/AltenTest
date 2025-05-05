import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, BehaviorSubject, tap, catchError, switchMap } from 'rxjs';
import { signal } from '@angular/core';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private tokenSubject = new BehaviorSubject<string | null>(null);
  public token$ = this.tokenSubject.asObservable();

  //  URL for your login endpoint.  ***ADJUST THIS***
  private loginUrl = 'http://localhost:8080/account/token';
  private readonly loginEmail = 'admin@admin.com';
  private readonly loginPassword = 'password123';

  constructor() {
     //  Load token from storage (e.g., localStorage) on service initialization.
     const storedToken = localStorage.getItem('authToken');
     this.tokenSubject.next(storedToken);
  }

login(): Observable<{ token: string }> { // Changed to no credentials.
    console.log('logging in');
    return this.http.post(this.loginUrl, { email: this.loginEmail, password: this.loginPassword }, { responseType: 'text' }).pipe( // Added responseType
      map(responseText => {
          this.tokenSubject.next(responseText);
          localStorage.setItem('authToken', responseText);
          return { token: responseText }; // Assumes the string was actually JSON

      }),
      catchError(error => {
        console.error('Login failed', error);
        this.tokenSubject.next(null); // Ensure tokenSubject is null on error
        localStorage.removeItem('authToken');
        return of({ token: '' }); //  Important:  Return a value, don't just throw.  Changed to of
      })
    );
  }

  // Method to get the current token (synchronously or asynchronously)
  getToken(): string | null {
    if (!this.tokenSubject.value) {
      this.login().subscribe();
    }
    return this.tokenSubject.value;
  }

    // Method to check if the user is logged in
  isLoggedIn(): boolean {
    console.log("isLoggedIn " + this.tokenSubject.value)
     return !!this.tokenSubject.value;
    }

  logout(): void {
        this.tokenSubject.next(null);
        localStorage.removeItem('authToken');
  }
}
