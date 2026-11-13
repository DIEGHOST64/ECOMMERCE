import {
  buildSession,
  clearSession,
  getAccessToken,
  getSession,
  getUser,
  isAdmin,
  normalizeRole,
  saveSession,
} from './session';

const LOGIN = {
  id: 7,
  nombre: 'Ana',
  email: 'ana@example.com',
  role: 'CLIENTE',
  token: 'jwt-de-prueba',
  tokenType: 'Bearer',
  expiraEnSegundos: 3600,
};

describe('sesion', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  test('normaliza los roles del backend', () => {
    expect(normalizeRole('ADMIN')).toBe('ADMIN');
    expect(normalizeRole('ROLE_ADMIN')).toBe('ADMIN');
    expect(normalizeRole('CLIENTE')).toBe('USER');
    expect(normalizeRole('USER')).toBe('USER');
    expect(normalizeRole(null)).toBe('USER');
  });

  test('guarda la sesion y expone usuario y token', () => {
    const session = saveSession(LOGIN);

    expect(session.user).toEqual({
      id: 7,
      nombre: 'Ana',
      email: 'ana@example.com',
      role: 'USER',
    });
    expect(getUser()).toEqual(session.user);
    expect(getAccessToken()).toBe('jwt-de-prueba');
    expect(isAdmin(getUser())).toBe(false);
  });

  test('rechaza respuestas de login sin token', () => {
    expect(() => buildSession({ id: 1 })).toThrow();
  });

  test('elimina una sesion expirada', () => {
    localStorage.setItem(
      'ecommerce.session',
      JSON.stringify({
        token: 'viejo',
        expiresAt: Date.now() - 1000,
        user: { id: 1, role: 'USER' },
      })
    );

    expect(getSession()).toBeNull();
    expect(localStorage.getItem('ecommerce.session')).toBeNull();
  });

  test('limpia las claves antiguas de usuario y token', () => {
    localStorage.setItem('user', '{"id":1}');
    localStorage.setItem('token', 'token-viejo');

    saveSession(LOGIN);

    expect(localStorage.getItem('user')).toBeNull();
    expect(localStorage.getItem('token')).toBeNull();
  });

  test('clearSession elimina todos los datos', () => {
    saveSession(LOGIN);
    clearSession();

    expect(getSession()).toBeNull();
    expect(getAccessToken()).toBeNull();
  });
});
