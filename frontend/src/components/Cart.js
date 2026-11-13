import React, { useCallback, useEffect, useState } from 'react';
import './Cart.css';
import Toast from './Toast';
import {
  FaShoppingCart,
  FaBox,
  FaTrashAlt,
  FaPlus,
  FaMinus,
  FaTimes,
  FaCreditCard,
  FaExclamationTriangle,
} from 'react-icons/fa';
import { catalogoApi, contarItems } from '../api/catalogoApi';

let toastSequence = 0;

function Cart({ setCartCount }) {
  const [carrito, setCarrito] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [procesando, setProcesando] = useState(false);
  const [itemProcesando, setItemProcesando] = useState(null);
  const [toasts, setToasts] = useState([]);
  const [showConfirmModal, setShowConfirmModal] = useState(false);

  const showToast = (message, type = 'success') => {
    toastSequence += 1;
    setToasts((prev) => [...prev, { id: toastSequence, message, type }]);
  };

  const removeToast = (id) => {
    setToasts((prev) => prev.filter((toast) => toast.id !== id));
  };

  const updateCarrito = useCallback(
    (nuevoCarrito) => {
      setCarrito(nuevoCarrito);
      setCartCount(contarItems(nuevoCarrito));
    },
    [setCartCount]
  );

  const fetchCarrito = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      updateCarrito(await catalogoApi.carrito.obtener());
    } catch (err) {
      setError(err.message || 'Error al cargar el carrito');
    } finally {
      setLoading(false);
    }
  }, [updateCarrito]);

  useEffect(() => {
    fetchCarrito();
  }, [fetchCarrito]);

  const eliminarItem = async (productoId) => {
    setItemProcesando(productoId);
    try {
      updateCarrito(await catalogoApi.carrito.eliminar(productoId));
      showToast('Producto eliminado del carrito');
    } catch (err) {
      showToast(err.message || 'No se pudo eliminar el producto', 'error');
    } finally {
      setItemProcesando(null);
    }
  };

  const actualizarCantidad = async (productoId, nuevaCantidad) => {
    if (nuevaCantidad < 1) {
      return;
    }

    setItemProcesando(productoId);
    try {
      updateCarrito(
        await catalogoApi.carrito.actualizar(productoId, nuevaCantidad)
      );
    } catch (err) {
      showToast(err.message || 'No se pudo actualizar la cantidad', 'error');
    } finally {
      setItemProcesando(null);
    }
  };

  const confirmarVaciarCarrito = async () => {
    setShowConfirmModal(false);
    setProcesando(true);
    try {
      await catalogoApi.carrito.vaciar();
      updateCarrito({ ...carrito, items: [], precioTotal: 0 });
      showToast('Carrito vaciado');
    } catch (err) {
      showToast(err.message || 'No se pudo vaciar el carrito', 'error');
    } finally {
      setProcesando(false);
    }
  };

  const realizarCompra = async () => {
    setProcesando(true);
    try {
      const compra = await catalogoApi.compras.realizar();
      updateCarrito({ ...carrito, items: [], precioTotal: 0 });
      showToast(`¡Compra #${compra.id} realizada con éxito!`);
    } catch (err) {
      showToast(err.message || 'No se pudo realizar la compra', 'error');
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

  if (error) {
    return (
      <div className="cart-error">
        <p>{error}</p>
        <button type="button" className="btn-checkout" onClick={fetchCarrito}>
          Reintentar
        </button>
      </div>
    );
  }

  const items = carrito?.items || [];
  const total = Number(
    carrito?.precioTotal ??
      items.reduce(
        (sum, item) => sum + Number(item.precioUnitario) * item.cantidad,
        0
      )
  );

  return (
    <div className="cart-container">
      <div className="cart-header">
        <h1>
          <FaShoppingCart /> Mi Carrito
        </h1>
        {items.length > 0 && (
          <button
            onClick={() => setShowConfirmModal(true)}
            className="btn-clear-cart"
            disabled={procesando}
          >
            <FaTrashAlt /> Vaciar Carrito
          </button>
        )}
      </div>

      {items.length === 0 ? (
        <div className="empty-cart">
          <FaShoppingCart className="empty-icon" />
          <h2>Tu carrito está vacío</h2>
          <p>Agrega productos para comenzar tu compra</p>
        </div>
      ) : (
        <div className="cart-content">
          <div className="cart-items">
            {items.map((item) => {
              const ocupado = itemProcesando === item.productoId;
              return (
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
                        onClick={() =>
                          actualizarCantidad(item.productoId, item.cantidad - 1)
                        }
                        className="btn-quantity"
                        disabled={ocupado || item.cantidad <= 1}
                        aria-label="Quitar una unidad"
                      >
                        <FaMinus />
                      </button>
                      <span className="quantity">{item.cantidad}</span>
                      <button
                        onClick={() =>
                          actualizarCantidad(item.productoId, item.cantidad + 1)
                        }
                        className="btn-quantity"
                        disabled={ocupado}
                        aria-label="Agregar una unidad"
                      >
                        <FaPlus />
                      </button>
                    </div>
                  </div>
                  <div className="item-price">
                    {(
                      Number(item.precioUnitario) * item.cantidad
                    ).toLocaleString('es-CO')}{' '}
                    COP
                  </div>
                  <button
                    onClick={() => eliminarItem(item.productoId)}
                    className="btn-remove"
                    disabled={ocupado}
                    aria-label={`Eliminar ${item.nombreProducto}`}
                  >
                    <FaTimes />
                  </button>
                </div>
              );
            })}
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
              <FaCreditCard />{' '}
              {procesando ? 'Procesando...' : 'Realizar Compra'}
            </button>
          </div>
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

      {showConfirmModal && (
        <div
          className="modal-overlay"
          onClick={() => setShowConfirmModal(false)}
        >
          <div className="modal-card" onClick={(e) => e.stopPropagation()}>
            <div className="modal-icon">
              <FaExclamationTriangle />
            </div>
            <h2>¿Vaciar Carrito?</h2>
            <p>
              Todos los productos serán eliminados del carrito. Esta acción no
              se puede deshacer.
            </p>
            <div className="modal-actions">
              <button
                onClick={() => setShowConfirmModal(false)}
                className="btn-cancel-modal"
              >
                Cancelar
              </button>
              <button
                onClick={confirmarVaciarCarrito}
                className="btn-confirm-modal"
              >
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
