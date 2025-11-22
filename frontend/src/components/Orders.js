import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Orders.css';

function Orders({ user }) {
  const [compras, setCompras] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchCompras();
  }, []);

  const fetchCompras = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(
        `http://localhost:9092/api/ecommerce/compras/usuario/${user.id}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setCompras(response.data);
    } catch (err) {
      console.error('Error al cargar compras');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner"></div>
        Cargando compras...
      </div>
    );
  }

  return (
    <div className="orders-container">
      <h1 className="page-title">Mis Compras</h1>
      
      {compras.length === 0 ? (
        <div className="empty-orders">
          <span className="empty-icon">📦</span>
          <h2>No tienes compras aún</h2>
          <p>Realiza tu primera compra y aparecerá aquí</p>
        </div>
      ) : (
        <div className="orders-list">
          {compras.map((compra) => (
            <div key={compra.id} className="order-card">
              <div className="order-header">
                <div>
                  <h3>Compra #{compra.id}</h3>
                  <p className="order-date">
                    {new Date(compra.fechaCompra).toLocaleDateString('es-ES', {
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric',
                      hour: '2-digit',
                      minute: '2-digit'
                    })}
                  </p>
                </div>
                <div className="order-total">
                  <span className="total-label">Total</span>
                  <span className="total-amount">{compra.total.toLocaleString('es-CO')} COP</span>
                </div>
              </div>
              
              <div className="order-items">
                <h4>Productos</h4>
                {compra.items && compra.items.map((item, index) => (
                  <div key={index} className="order-item">
                    <span className="item-name">{item.nombreProducto}</span>
                    <span className="item-quantity">x{item.cantidad}</span>
                    <span className="item-price">{(item.precioUnitario * item.cantidad).toLocaleString('es-CO')} COP</span>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default Orders;
