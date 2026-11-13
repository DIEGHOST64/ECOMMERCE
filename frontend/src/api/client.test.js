import { ApiError, SESSION_EXPIRED_EVENT, request } from './client';
import { saveSession } from '../session';

const LOGIN = {
  id: 1,
  nombre: 'Ana',
  email: 'ana@example.com',
  role: 'USER',
  token: 'jwt-de-prueba',
  tokenType: 'Bearer',
  expiraEnSegundos: 3600,
};

function jsonResponse(body, status = 200) {
  return {
    ok: status >= 200 && status < 300,
    status,
    headers: { get: () => 'application/json' },
    json: async () => body,
    text: async () => JSON.stringify(body),
  };
}

const originalFetch = global.fetch;

describe('cliente HTTP', () => {
  beforeEach(() => {
    localStorage.clear();
    global.fetch = jest.fn();
  });

  afterAll(() => {
    global.fetch = originalFetch;
  });

  test('antepone la URL base, agrega query y envia el JWT', async () => {
    saveSession(LOGIN);
    global.fetch.mockResolvedValue(jsonResponse([]));

    await request('http://api.local', '/producto', {
      query: { cantidad: 2, vacio: '' },
    });

    expect(global.fetch).toHaveBeenCalledWith(
      'http://api.local/producto?cantidad=2',
      expect.objectContaining({
        method: 'GET',
        headers: expect.objectContaining({
          Authorization: 'Bearer jwt-de-prueba',
          Accept: 'application/json',
        }),
      })
    );
  });

  test('no envia Authorization cuando auth es false', async () => {
    saveSession(LOGIN);
    global.fetch.mockResolvedValue(jsonResponse({}));

    await request('http://api.local', '/login', { auth: false });

    const [, options] = global.fetch.mock.calls[0];
    expect(options.headers.Authorization).toBeUndefined();
  });

  test('convierte los errores del backend en ApiError', async () => {
    global.fetch.mockResolvedValue(
      jsonResponse({ message: 'Stock insuficiente' }, 400)
    );

    await expect(
      request('http://api.local', '/compras/realizar', { method: 'POST' })
    ).rejects.toMatchObject({
      name: 'ApiError',
      status: 400,
      message: 'Stock insuficiente',
    });
  });

  test('usa los errores de validacion cuando no hay message', async () => {
    global.fetch.mockResolvedValue(
      jsonResponse(
        {
          fieldErrors: {
            email: 'El email no es valido',
            password: 'La contrasena es obligatoria',
          },
        },
        400
      )
    );

    await expect(
      request('http://api.local', '/usuario/save', { method: 'POST', body: {} })
    ).rejects.toThrow(
      'El email no es valido. La contrasena es obligatoria'
    );
  });

  test('devuelve null en respuestas 204', async () => {
    global.fetch.mockResolvedValue({
      ok: true,
      status: 204,
      headers: { get: () => '' },
      json: async () => null,
      text: async () => '',
    });

    await expect(
      request('http://api.local', '/carrito/vaciar', { method: 'DELETE' })
    ).resolves.toBeNull();
  });

  test('cierra la sesion y emite el evento cuando el JWT expira', async () => {
    saveSession(LOGIN);
    global.fetch.mockResolvedValue(
      jsonResponse({ message: 'Token invalido' }, 401)
    );
    const listener = jest.fn();
    window.addEventListener(SESSION_EXPIRED_EVENT, listener);

    await expect(
      request('http://api.local', '/carrito')
    ).rejects.toBeInstanceOf(ApiError);

    expect(localStorage.getItem('ecommerce.session')).toBeNull();
    expect(listener).toHaveBeenCalledTimes(1);

    window.removeEventListener(SESSION_EXPIRED_EVENT, listener);
  });
});
