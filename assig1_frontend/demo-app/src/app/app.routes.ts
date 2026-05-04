import { Routes } from '@angular/router';
import { authGuard, guestGuard } from './guards/auth.guards';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login',
  },
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./features/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'register',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./features/register/register.component').then((m) => m.RegisterComponent),
  },
  {
    path: 'reset-password',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/password-reset/password-reset.component').then(
        (m) => m.PasswordResetComponent,
      ),
  },
  {
    path: 'people',
    loadComponent: () =>
      import('./features/person-list/person-list-page.component').then(
        (m) => m.PersonListPageComponent,
      ),
  },
  {
    path: 'students',
    loadComponent: () =>
      import('./features/student-list/student-list-page.component').then(
        (m) => m.StudentListPageComponent,
      ),
  },
  {
    path: 'professors',
    loadComponent: () =>
      import('./features/professor-list/professor-list-page.component').then(
        (m) => m.ProfessorListPageComponent,
      ),
  },
  {
    path: 'error',
    loadComponent: () =>
      import('./features/not-found/not-found-page.component').then(
        (m) => m.NotFoundPageComponent,
      ),
  },
  {
    path: '**',
    redirectTo: 'error',
  },
];
