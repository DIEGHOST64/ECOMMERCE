import React, { useCallback, useEffect, useState } from 'react';
import './AdminPanel.css';
import Toast from './Toast';
import { FaImage } from 'react-icons/fa';
import { catalogoApi } from '../api/catalogoApi';

let toastSequence = 0;

const FORMULARIO_VACIO = {
  nombre: '',
  descripcion: '',
  precio: '',
  stock: '',
  imagenUrl: '',
};

function AdminPanel() {
  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [guardando, setGuardando] = useState(false);
  const [eliminandoId, setEliminandoId] = useState(null);
  const [toasts, setToasts] = useState([]);
  const [formData, setFormData] = useState(FORMULARIO_VACIO);

  const showToast = useCallback((message, type = 'success') => {
    toastSequence += 1;
    setToasts((prev) => [...prev, { id: toastSequence, message, type }]);
  }, []);

  const removeToast = (id) => {
    setToasts((prev) => prev.filter((toast) => toast.id !== id));
  };

  const fetchProductos = useCallback(async () => {
    setLoading(true);
    try {
      const data = await catalogoApi.productos.listar();
      setProductos(Array.isArray(data) ? data : []);
    } catch (err) {
      showToast(err.message || 'Error al cargar productos', 'error');
    } finally {
      setLoading(false);
    }
  }, [showToast]);

  useEffect(() => {
    fetchProductos();
  }, [fetchProductos]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const resetFormulario = () => {
    setShowForm(false);
    setEditingProduct(null);
    setFormData(FORMULARIO_VACIO);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setGuardando(true);

    const producto = {
      nombre: formData.nombre.trim(),
      descripcion: formData.descripcion.trim(),
      precio: parseFloat(formData.precio),
      stock: parseInt(formData.stock, 10),
      imagenUrl: formData.imagenUrl.trim() || null,
    };

    try {
      if (editingProduct) {
        await catalogoApi.productos.actualizar(editingProduct.id, producto);
        showToast('Producto actualizado correctamente');
      } else {
        await catalogoApi.productos.crear(producto);
        showToast('Producto creado correctamente');
      }

      resetFormulario();
      fetchProductos();
    } catch (err) {
      showToast(err.message || 'Error al guardar producto', 'error');
    } finally {
      setGuardando(false);
    }
  };

  const handleEdit = (producto) => {
    setEditingProduct(producto);
    setFormData({
      nombre: producto.nombre,
      descripcion: producto.descripcion || '',
      precio: producto.precio.toString(),
      stock: producto.stock.toString(),
      imagenUrl: producto.imagenUrl || '',
    });
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('¿Estás seguro de eliminar este producto?')) {
      return;
    }

    setEliminandoId(id);
    try {
      await catalogoApi.productos.eliminar(id);
      showToast('Producto eliminado correctamente');
      fetchProductos();
    } catch (err) {
      showToast(err.message || 'Error al eliminar producto', 'error');
    } finally {
      setEliminandoId(null);
    }
  };

  if (loading) {
    return <div className="loading">Cargando...</div>;
  }

  return (
    <div className="admin-container">
      <div className="admin-header">
        <h1>Panel de Administración</h1>
        <button
          onClick={() => (showForm ? resetFormulario() : setShowForm(true))}
          className="btn-new-product"
        >
          {showForm ? '✕ Cancelar' : '+ Nuevo Producto'}
        </button>
      </div>

      {showForm && (
        <div className="product-form-card">
          <h2>{editingProduct ? 'Editar Producto' : 'Nuevo Producto'}</h2>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Nombre del Producto</label>
              <input
                type="text"
                name="nombre"
                value={formData.nombre}
                onChange={handleChange}
                required
                maxLength="150"
                placeholder="Ej: Laptop Dell XPS 13"
              />
            </div>

            <div className="form-group">
              <label>Descripción</label>
              <textarea
                name="descripcion"
                value={formData.descripcion}
                onChange={handleChange}
                required
                maxLength="2000"
                placeholder="Descripción detallada del producto"
                rows="3"
              />
            </div>

            <div className="form-group">
              <label>
                <FaImage /> URL de Imagen del Producto
              </label>
              <input
                type="url"
                name="imagenUrl"
                value={formData.imagenUrl}
                onChange={handleChange}
                placeholder="https://ejemplo.com/imagen.jpg"
              />
              {formData.imagenUrl && (
                <div className="image-preview">
                  <img
                    src={formData.imagenUrl}
                    alt="Preview"
                    onError={(e) => {
                      e.target.style.display = 'none';
                    }}
                  />
                </div>
              )}
              <small
                style={{
                  color: '#a0a0a0',
                  fontSize: '12px',
                  display: 'block',
                  marginTop: '8px',
                }}
              >
                Ingresa la URL de una imagen (puede ser de Google Images,
                Imgur, etc.)
              </small>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Precio ($)</label>
                <input
                  type="number"
                  step="0.01"
                  min="0.01"
                  name="precio"
                  value={formData.precio}
                  onChange={handleChange}
                  required
                  placeholder="0.00"
                />
              </div>

              <div className="form-group">
                <label>Stock</label>
                <input
                  type="number"
                  min="0"
                  name="stock"
                  value={formData.stock}
                  onChange={handleChange}
                  required
                  placeholder="0"
                />
              </div>
            </div>

            <div className="form-actions">
              <button
                type="button"
                onClick={resetFormulario}
                className="btn-cancel"
              >
                Cancelar
              </button>
              <button type="submit" className="btn-save" disabled={guardando}>
                {guardando
                  ? 'Guardando...'
                  : editingProduct
                    ? 'Actualizar'
                    : 'Guardar'}
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="products-table">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Imagen</th>
              <th>Nombre</th>
              <th>Descripción</th>
              <th>Precio</th>
              <th>Stock</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {productos.length === 0 ? (
              <tr>
                <td colSpan="7">No hay productos registrados</td>
              </tr>
            ) : (
              productos.map((producto) => (
                <tr key={producto.id}>
                  <td>{producto.id}</td>
                  <td>
                    {producto.imagenUrl ? (
                      <img
                        src={producto.imagenUrl}
                        alt={producto.nombre}
                        className="product-thumbnail"
                      />
                    ) : (
                      <div className="no-image">
                        <FaImage />
                      </div>
                    )}
                  </td>
                  <td>{producto.nombre}</td>
                  <td>{producto.descripcion}</td>
                  <td>${Number(producto.precio).toFixed(2)}</td>
                  <td>
                    <span
                      className={`stock-badge ${
                        producto.stock === 0 ? 'out-of-stock' : ''
                      }`}
                    >
                      {producto.stock}
                    </span>
                  </td>
                  <td>
                    <button
                      onClick={() => handleEdit(producto)}
                      className="btn-edit"
                    >
                      ✎ Editar
                    </button>
                    <button
                      onClick={() => handleDelete(producto.id)}
                      className="btn-delete"
                      disabled={eliminandoId === producto.id}
                    >
                      🗑 {eliminandoId === producto.id ? 'Eliminando...' : 'Eliminar'}
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

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

export default AdminPanel;
