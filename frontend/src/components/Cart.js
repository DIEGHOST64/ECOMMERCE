import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Cart.css';
import Toast from './Toast';
import { FaShoppingCart, FaBox, FaTrashAlt, FaPlus, FaMinus, FaTimes, FaCreditCard, FaExclamationTriangle } from 'react-icons/fa';

function Cart({ user, cartCount, setCartCount }) {
  const [carrito, setCarrito] = useState(null);
  const [loading, setLoading] = useState(true);
  const [procesando, setProcesando] = useState(false);
  const [toasts, setToasts] = useState([]);
  const [showConfirmModal, setShowConfirmModal] = useState(false);

  useEffect(() => {
    fetchCarrito();
  }, []);

  const showToast = (message, type = 'success') => {
    const id = Date.now();
    setToasts(prev => [...prev, { id, message, type }]);
  };

  const removeToast = (id) => {
    setToasts(prev => prev.filter(toast => toast.id !== id));
  };

  const fetchCarrito = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(
        `http://localhost:9092/api/ecommerce/carrito?usuarioId=${user.id}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setCarrito(response.data);
      setCartCount(response.data.items?.length || 0);
    } catch (err) {
      console.error('Error al cargar carrito');
    } finally {
      setLoading(false);
    }
  };

  const eliminarItem = async (itemId) => {
    try {
      const token = localStorage.getItem('token');
      await axios.delete(
        `http://localhost:9092/api/ecommerce/carrito/eliminar/${itemId}?usuarioId=${user.id}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      fetchCarrito();
      showToast('✓ Producto eliminado del carrito');
    } catch (err) {
      showToast('Error al eliminar item', 'error');
    }
  };

  const actualizarCantidad = async (productoId, nuevaCantidad) => {
    if (nuevaCantidad < 1) return;
    try {
      const token = localStorage.getItem('token');
      await axios.put(
        `http://localhost:9092/api/ecommerce/carrito/actualizar/${productoId}?usuarioId=${user.id}&cantidad=${nuevaCantidad}`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      fetchCarrito();
      showToast('✓ Cantidad actualizada');
    } catch (err) {
      showToast('Error al actualizar cantidad', 'error');
    }
  };

  const vaciarCarrito = async () => {
    setShowConfirmModal(true);
  };

  const confirmarVaciarCarrito = async () => {
    setShowConfirmModal(false);
    try {
      const token = localStorage.getItem('token');
      await axios.delete(
        `http://localhost:9092/api/ecommerce/carrito/vaciar?usuarioId=${user.id}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      fetchCarrito();
      showToast('✓ Carrito vaciado');
    } catch (err) {
      showToast('Error al vaciar carrito', 'error');
    }
  };

  const realizarCompra = async () => {
    setProcesando(true);
    try {
      const token = localStorage.getItem('token');
      await axios.post(
        `http://localhost:9092/api/ecommerce/compras/realizar?usuarioId=${user.id}`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      showToast('✓ ¡Compra realizada con éxito!');
      fetchCarrito();
    } catch (err) {
      showToast('Error al realizar la compra', 'error');
    } finally {
      setProcesando(false);
    }
  };

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner"></div>
        Cargando carrito...
      </div>
    );
  }

  const total = carrito?.items?.reduce(
    (sum, item) => sum + (item.precioUnitario * item.cantidad), 0
  ) || 0;

  return (
    <div className="cart-container">
      <div className="cart-header">
        <h1><FaShoppingCart /> Mi Carrito</h1>
        {carrito?.items && carrito.items.length > 0 && (
          <button onClick={vaciarCarrito} className="btn-clear-cart">
            <FaTrashAlt /> Vaciar Carrito
          </button>
        )}
      </div>
      
      {!carrito?.items || carrito.items.length === 0 ? (
        <div className="empty-cart">
          <FaShoppingCart className="empty-icon" />
          <h2>Tu carrito está vacío</h2>
          <p>Agrega productos para comenzar tu compra</p>
        </div>
      ) : (
        <div className="cart-content">
          <div className="cart-items">
            {carrito.items.map((item) => (
              <div key={item.id} className="cart-item">
                <div className="item-image">
                  {item.imagenUrl ? (
                    <img src={item.imagenUrl} alt={item.nombreProducto} />
                  ) : (
                    <FaBox />
                  )}
                </div>
                <div className="item-info">
                  <h3>{item.nombreProducto}</h3>
                  <div className="quantity-controls">
                    <button 
                      onClick={() => actualizarCantidad(item.productoId, item.cantidad - 1)}
                      className="btn-quantity"
                    >
                      <FaMinus />
                    </button>
                    <span className="quantity">{item.cantidad}</span>
                    <button 
                      onClick={() => actualizarCantidad(item.productoId, item.cantidad + 1)}
                      className="btn-quantity"
                    >
                      <FaPlus />
                    </button>
                  </div>
                </div>
                <div className="item-price">
                  {(item.precioUnitario * item.cantidad).toLocaleString('es-CO')} COP
                </div>
                <button
                  onClick={() => eliminarItem(item.productoId)}
                  className="btn-remove"
                >
                  <FaTimes />
                </button>
              </div>
            ))}
          </div>
          
          <div className="cart-summary">
            <h2>Resumen de Compra</h2>
            <div className="summary-row">
              <span>Subtotal:</span>
              <span>{total.toLocaleString('es-CO')} COP</span>
            </div>
            <div className="summary-row">
              <span>Envío:</span>
              <span>Gratis</span>
            </div>
            <div className="summary-total">
              <span>Total:</span>
              <span>{total.toLocaleString('es-CO')} COP</span>
            </div>
            <button
              onClick={realizarCompra}
              className="btn-checkout"
              disabled={procesando}
            >
              <FaCreditCard /> {procesando ? 'Procesando...' : 'Realizar Compra'}
            </button>
          </div>
        </div>
      )}
      
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

      {/* Modal de confirmación */}
      {showConfirmModal && (
        <div className="modal-overlay" onClick={() => setShowConfirmModal(false)}>
          <div className="modal-card" onClick={(e) => e.stopPropagation()}>
            <div className="modal-icon">
              <FaExclamationTriangle />
            </div>
            <h2>¿Vaciar Carrito?</h2>
            <p>Todos los productos serán eliminados del carrito. Esta acción no se puede deshacer.</p>
            <div className="modal-actions">
              <button onClick={() => setShowConfirmModal(false)} className="btn-cancel-modal">
                Cancelar
              </button>
              <button onClick={confirmarVaciarCarrito} className="btn-confirm-modal">
                <FaTrashAlt /> Sí, vaciar carrito
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Cart;
