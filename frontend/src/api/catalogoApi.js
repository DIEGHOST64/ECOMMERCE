import config from '../config';
import { request } from './client';

const BASE = config.CATALOGO_URL;
const PRODUCTOS = '/api/ecommerce/producto';
const CARRITO = '/api/ecommerce/carrito';
const COMPRAS = '/api/ecommerce/compras';

/**
 * Cantidad total de unidades del carrito, usada por el badge de la barra
 * de navegacion.
 */
export function contarItems(carrito) {
  if (!carrito?.items?.length) {
    return 0;
  }
  return carrito.items.reduce(
    (total, item) => total + (Number(item.cantidad) || 0),
    0
  );
}

export const catalogoApi = {
  productos: {
    listar() {
      return request(BASE, PRODUCTOS);
    },
    obtener(id) {
      return request(BASE, `${PRODUCTOS}/${id}`);
    },
    crear(producto) {
      return request(BASE, PRODUCTOS, { method: 'POST', body: producto });
    },
    actualizar(id, producto) {
      return request(BASE, `${PRODUCTOS}/${id}`, {
        method: 'PUT',
        body: producto,
      });
    },
    eliminar(id) {
      return request(BASE, `${PRODUCTOS}/${id}`, { method: 'DELETE' });
    },
  },

  carrito: {
    obtener() {
      return request(BASE, CARRITO);
    },
    agregar(productoId, cantidad) {
      return request(BASE, `${CARRITO}/agregar`, {
        method: 'POST',
        body: { productoId, cantidad },
      });
    },
    actualizar(productoId, cantidad) {
      return request(BASE, `${CARRITO}/actualizar/${productoId}`, {
        method: 'PUT',
        query: { cantidad },
      });
    },
    eliminar(productoId) {
      return request(BASE, `${CARRITO}/eliminar/${productoId}`, {
        method: 'DELETE',
      });
    },
    vaciar() {
      return request(BASE, `${CARRITO}/vaciar`, { method: 'DELETE' });
    },
  },

  compras: {
    realizar() {
      return request(BASE, `${COMPRAS}/realizar`, { method: 'POST' });
    },
    listarPorUsuario(usuarioId) {
      return request(BASE, `${COMPRAS}/usuario/${usuarioId}`);
    },
    obtener(compraId) {
      return request(BASE, `${COMPRAS}/${compraId}`);
    },
  },
};
