import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './ProductList.css';
import Toast from './Toast';
import { FaBox, FaShoppingCart, FaBoxes } from 'react-icons/fa';

function ProductList({ user, setCartCount }) {
  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [toasts, setToasts] = useState([]);

  useEffect(() => {
    fetchProductos();
  }, []);

  const showToast = (message, type = 'success') => {
    const id = Date.now();
    setToasts(prev => [...prev, { id, message, type }]);
  };

  const removeToast = (id) => {
    setToasts(prev => prev.filter(toast => toast.id !== id));
  };

  const fetchProductos = async () => {
    try {
      const response = await axios.get('http://localhost:9092/api/ecommerce/producto');
      setProductos(response.data);
    } catch (err) {
      setError('Error al cargar productos');
    } finally {
      setLoading(false);
    }
  };

  const agregarAlCarrito = async (productoId) => {
    try {
      const token = localStorage.getItem('token');
      await axios.post(
        `http://localhost:9092/api/ecommerce/carrito/agregar?usuarioId=${user.id}`,
        {
          productoId: productoId,
          cantidad: 1
        },
        {
          headers: { Authorization: `Bearer ${token}` }
        }
      );
      
      // Actualizar contador del carrito
      const carritoResponse = await axios.get(
        `http://localhost:9092/api/ecommerce/carrito?usuarioId=${user.id}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setCartCount(carritoResponse.data.items?.length || 0);
      
      // Recargar productos para actualizar el stock
      fetchProductos();
      
      showToast('✓ Producto agregado al carrito');
    } catch (err) {
      showToast('Error al agregar al carrito', 'error');
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
    return <div className="error-container">{error}</div>;
  }

  return (
    <div className="product-container">
      <h1 className="page-title">Nuestros Productos</h1>
      
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
                <span className="product-price">{producto.precio.toLocaleString('es-CO')} COP</span>
                <span className="product-stock">
                  <FaBoxes className="stock-icon" />
                  {producto.stock}
                </span>
              </div>
              <button
                onClick={() => agregarAlCarrito(producto.id)}
                className="btn-add-cart"
                disabled={producto.stock === 0}
              >
                <FaShoppingCart />
                <span>{producto.stock > 0 ? 'Agregar' : 'Sin Stock'}</span>
              </button>
            </div>
          </div>
        ))}
      </div>
      
      {/* Contenedor de notificaciones */}
      <div className="toast-container">
        {toasts.map(toast => (
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
