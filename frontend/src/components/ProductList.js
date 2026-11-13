import React, { useCallback, useEffect, useState } from 'react';
import './ProductList.css';
import Toast from './Toast';
import { FaBox, FaShoppingCart, FaBoxes } from 'react-icons/fa';
import { catalogoApi, contarItems } from '../api/catalogoApi';

let toastSequence = 0;

function ProductList({ setCartCount }) {
  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [toasts, setToasts] = useState([]);
  const [agregandoId, setAgregandoId] = useState(null);

  const showToast = (message, type = 'success') => {
    toastSequence += 1;
    setToasts((prev) => [...prev, { id: toastSequence, message, type }]);
  };

  const removeToast = (id) => {
    setToasts((prev) => prev.filter((toast) => toast.id !== id));
  };

  const fetchProductos = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await catalogoApi.productos.listar();
      setProductos(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err.message || 'Error al cargar productos');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchProductos();
  }, [fetchProductos]);

  const agregarAlCarrito = async (producto) => {
    setAgregandoId(producto.id);
    try {
      const carrito = await catalogoApi.carrito.agregar(producto.id, 1);
      setCartCount(contarItems(carrito));
      showToast(`${producto.nombre} agregado al carrito`);
    } catch (err) {
      showToast(err.message || 'No se pudo agregar al carrito', 'error');
    } finally {
      setAgregandoId(null);
    }
  };

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner"></div>
        Cargando productos...
      </div>
    );
  }

  if (error) {
    return (
      <div className="error">
        <p>{error}</p>
        <button type="button" className="btn-retry" onClick={fetchProductos}>
          Reintentar
        </button>
      </div>
    );
  }

  return (
    <div className="product-container">
      <h1 className="page-title">Nuestros Productos</h1>

      {productos.length === 0 ? (
        <p className="error">Todavía no hay productos disponibles.</p>
      ) : (
        <div className="product-grid">
          {productos.map((producto) => (
            <div key={producto.id} className="product-card">
              <div className="product-image">
                {producto.imagenUrl ? (
                  <img src={producto.imagenUrl} alt={producto.nombre} />
                ) : (
                  <FaBox className="product-icon" />
                )}
              </div>
              <div className="product-body">
                <h3 className="product-name">{producto.nombre}</h3>
                <p className="product-description">{producto.descripcion}</p>
                <div className="product-details">
                  <span className="product-price">
                    {Number(producto.precio).toLocaleString('es-CO')} COP
                  </span>
                  <span className="product-stock">
                    <FaBoxes className="stock-icon" />
                    {producto.stock}
                  </span>
                </div>
                <button
                  onClick={() => agregarAlCarrito(producto)}
                  className="btn-add-cart"
                  disabled={producto.stock === 0 || agregandoId === producto.id}
                >
                  <FaShoppingCart />
                  <span>
                    {producto.stock === 0
                      ? 'Sin Stock'
                      : agregandoId === producto.id
                        ? 'Agregando...'
                        : 'Agregar'}
                  </span>
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      <div className="toast-container">
        {toasts.map((toast) => (
          <Toast
            key={toast.id}
            message={toast.message}
            type={toast.type}
            onClose={() => removeToast(toast.id)}
          />
        ))}
      </div>
    </div>
  );
}

export default ProductList;
