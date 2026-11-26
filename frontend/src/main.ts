import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAuth, withAppInitializerAuthCheck } from 'angular-auth-oidc-client';
import { AppComponent } from './app/app.component';
import { environment } from './environments/environment';
import { authInterceptor } from './app/auth.interceptor';

bootstrapApplication(AppComponent, {
  providers: [provideHttpClient(withInterceptors([authInterceptor])), provideAuth({ config: {
    authority: environment.oidc.authority, clientId: environment.oidc.clientId,
    redirectUrl: environment.oidc.redirectUrl, postLogoutRedirectUri: environment.oidc.redirectUrl,
    scope: environment.oidc.scope, responseType: 'code', silentRenew: true,
    useRefreshToken: true, ignoreNonceAfterRefresh: true
  }}), withAppInitializerAuthCheck()]
}).catch(err => console.error(err));
