import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { LoginStore } from '../features/login/login.store';

export const authGuard: CanActivateFn = () => {
  const loginStore = inject(LoginStore);
  const router = inject(Router);

  return loginStore.isAuthenticated() ? true : router.createUrlTree(['/login']);
};

export const guestGuard: CanActivateFn = () => {
  const loginStore = inject(LoginStore);
  const router = inject(Router);

  if (!loginStore.isAuthenticated()) {
    return true;
  }

  const normalizedRole = loginStore.role()?.trim().toLowerCase();
  const targetRoute =
    normalizedRole === 'student'
      ? '/students'
      : normalizedRole === 'professor'
        ? '/professors'
        : '/people';

  return router.createUrlTree([targetRoute]);
};