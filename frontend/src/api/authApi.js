import config from '../config';
import { request } from './client';

const BASE = config.AUTH_URL;

export const authApi = {
  login(credentials) {
    return request(BASE, '/api/ecommerce/usuario/login', {
      method: 'POST',
      body: credentials,
      auth: false,
    });
  },

  register(usuario) {
    return request(BASE, '/api/ecommerce/usuario/save', {
      method: 'POST',
      body: usuario,
      auth: false,
    });
  },

  getUsuario(id) {
    return request(BASE, `/api/ecommerce/usuario/${id}`);
  },
};
