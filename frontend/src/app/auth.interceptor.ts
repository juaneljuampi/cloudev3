import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { environment } from '../environments/environment';
import { switchMap } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const oidc = inject(OidcSecurityService);
  if (!request.url.startsWith(environment.apiUrl)) return next(request);
  return oidc.getAccessToken().pipe(switchMap(token => next(request.clone({
    setHeaders: token ? { Authorization: `Bearer ${token}` } : {}
  }))));
};
