import { Injectable, inject, signal } from "@angular/core";
import { Product } from "./product.model";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { catchError, Observable, of, tap } from "rxjs";
import { AuthService } from "app/products/data-access/auth.service";
@Injectable({
    providedIn: "root"
}) export class ProductsService {

     private readonly http = inject(HttpClient);
      private readonly basePath = 'http://localhost:8080/products';

      private readonly _products = signal<Product[]>([]);
      public readonly products = this._products.asReadonly();
      private authService = inject(AuthService);

      // Function to get products *after* authentication
        private getProductsWithAuth(): Observable<Product[]> {
            const token = this.authService.getToken(); // Get token *synchronously*
            console.log(token)
            if (!token) {
                console.error('No token available.  User not logged in.');
                //  Return an empty observable or an observable with an error.
                return of([]); //  Or:  return throwError(() => new Error('Not authenticated'));
            }

            const headers = new HttpHeaders({
                Authorization: `Bearer ${token}`,
            });

            return this.http.get<Product[]>(this.basePath, { headers }).pipe(
                catchError((error) => {
                    console.error('Error fetching products from API:', error);
                    return this.http.get<Product[]>('assets/products.json');
                }),
                tap((products) => this._products.set(products))
            );
        }

      public get(): Observable<Product[]> {
            return this.getProductsWithAuth();
      }

      public create(product: Product): Observable<boolean> {
        const token = this.authService.getToken();
        console.log(token)
        if (!token) {
          console.error('No token available.  User not logged in.');
          return of(false);
        }
        const headers = new HttpHeaders({
          Authorization: `Bearer ${token}`,
        });
        return this.http.post<boolean>(this.basePath, product, { headers }).pipe(
          catchError(() => {
            return of(true);
          }),
          tap(() => this._products.update(products => [product, ...products])),
        );
      }

      public update(product: Product): Observable<boolean> {
        const token = this.authService.getToken();
        if (!token) {
          console.error('No token available.  User not logged in.');
          return of(false);
        }
        const headers = new HttpHeaders({
          Authorization: `Bearer ${token}`,
        });
        return this.http.patch<boolean>(`${this.basePath}/${product.id}`, product, { headers }).pipe(
          catchError(() => {
            return of(true);
          }),
          tap(() => this._products.update(products => {
            return products.map(p => p.id === product.id ? product : p);
          })),
        );
      }

      public delete(productId: number): Observable<boolean> {
        const token = this.authService.getToken();
         if (!token) {
          console.error('No token available. User not logged in.');
          return of(false);
        }
        const headers = new HttpHeaders({
          Authorization: `Bearer ${token}`,
        });
        return this.http.delete<boolean>(`${this.basePath}/${productId}`, { headers }).pipe(
          catchError(() => {
            return of(true);
          }),
          tap(() => this._products.update(products => products.filter(product => product.id !== productId))),
        );
      }
}
