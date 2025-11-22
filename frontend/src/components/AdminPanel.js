import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './AdminPanel.css';
import Toast from './Toast';
import { FaImage, FaTrashAlt, FaUpload } from 'react-icons/fa';

function AdminPanel({ user }) {
  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [toasts, setToasts] = useState([]);
  const [imagePreview, setImagePreview] = useState(null);
  const [imageFile, setImageFile] = useState(null);
  const [formData, setFormData] = useState({
    nombre: '',
    descripcion: '',
    precio: '',
    stock: '',
    imagenUrl: ''
  });

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
      showToast('Error al cargar productos', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      // Validar tamaño de archivo (máximo 2MB)
      if (file.size > 2 * 1024 * 1024) {
        showToast('La imagen es muy grande. Máximo 2MB', 'error');
        return;
      }
      
      setImageFile(file);
      const reader = new FileReader();
      reader.onload = (event) => {
        const img = new Image();
        img.onload = () => {
          // Crear canvas para redimensionar
          const canvas = document.createElement('canvas');
          const ctx = canvas.getContext('2d');
          
          // Redimensionar a máximo 800x800 manteniendo proporción
          let width = img.width;
          let height = img.height;
          const maxSize = 800;
          
          if (width > height) {
            if (width > maxSize) {
              height = (height * maxSize) / width;
              width = maxSize;
            }
          } else {
            if (height > maxSize) {
              width = (width * maxSize) / height;
              height = maxSize;
            }
          }
          
          canvas.width = width;
          canvas.height = height;
          ctx.drawImage(img, 0, 0, width, height);
          
          // Convertir a base64 con calidad 0.7
          const compressedBase64 = canvas.toDataURL('image/jpeg', 0.7);
          
          // Validar tamaño del base64 (máximo 200KB)
          if (compressedBase64.length > 200000) {
            showToast('La imagen sigue siendo muy grande después de comprimir', 'error');
            setImageFile(null);
            setImagePreview(null);
            return;
          }
          
          setImagePreview(compressedBase64);
          setFormData({ ...formData, imagenUrl: compressedBase64 });
        };
        img.src = event.target.result;
      };
      reader.readAsDataURL(file);
    }
  };

  const removeImage = () => {
    setImageFile(null);
    setImagePreview(null);
    setFormData({ ...formData, imagenUrl: '' });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      const token = localStorage.getItem('token');
      const headers = {
        'Authorization': `Bearer ${token}`,
        'X-User-Role': user?.role || 'ADMIN'
      };
      
      const producto = {
        nombre: formData.nombre,
        descripcion: formData.descripcion,
        precio: parseFloat(formData.precio),
        stock: parseInt(formData.stock),
        imagenUrl: formData.imagenUrl || null
      };

      if (editingProduct) {
        await axios.put(
          `http://localhost:9092/api/ecommerce/producto/${editingProduct.id}`,
          producto,
          { headers }
        );
        showToast('✓ Producto actualizado correctamente');
      } else {
        await axios.post(
          'http://localhost:9092/api/ecommerce/producto',
          producto,
          { headers }
        );
        showToast('✓ Producto creado correctamente');
      }

      setShowForm(false);
      setEditingProduct(null);
      setFormData({ nombre: '', descripcion: '', precio: '', stock: '', imagenUrl: '' });
      setImagePreview(null);
      setImageFile(null);
      fetchProductos();
    } catch (err) {
      console.error('Error completo:', err.response || err);
      let errorMsg = 'Error al guardar producto';
      
      if (err.response?.data) {
        if (typeof err.response.data === 'string') {
          errorMsg = err.response.data;
        } else if (err.response.data.message) {
          errorMsg = err.response.data.message;
        } else if (err.response.data.error) {
          errorMsg = err.response.data.error;
        }
      }
      
      showToast(errorMsg, 'error');
    }
  };

  const handleEdit = (producto) => {
    setEditingProduct(producto);
    setFormData({
      nombre: producto.nombre,
      descripcion: producto.descripcion,
      precio: producto.precio.toString(),
      stock: producto.stock.toString(),
      imagenUrl: producto.imagenUrl || ''
    });
    if (producto.imagenUrl) {
      setImagePreview(producto.imagenUrl);
    }
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('¿Estás seguro de eliminar este producto?')) return;

    try {
      const token = localStorage.getItem('token');
      const headers = {
        'Authorization': `Bearer ${token}`,
        'X-User-Role': user?.role || 'ADMIN'
      };
      
      await axios.delete(
        `http://localhost:9092/api/ecommerce/producto/${id}`,
        { headers }
      );
      showToast('✓ Producto eliminado correctamente');
      fetchProductos();
    } catch (err) {
      showToast('Error al eliminar producto', 'error');
    }
  };

  const cancelForm = () => {
    setShowForm(false);
    setEditingProduct(null);
    setFormData({ nombre: '', descripcion: '', precio: '', stock: '', imagenUrl: '' });
    setImagePreview(null);
    setImageFile(null);
  };

  if (loading) {
    return <div className="loading">Cargando...</div>;
  }

  return (
    <div className="admin-container">
      <div className="admin-header">
        <h1>Panel de Administración</h1>
        <button 
          onClick={() => setShowForm(!showForm)} 
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
                placeholder="Descripción detallada del producto"
                rows="3"
              />
            </div>

            <div className="form-group">
              <label><FaImage /> URL de Imagen del Producto</label>
              <input
                type="url"
                name="imagenUrl"
                value={formData.imagenUrl}
                onChange={handleChange}
                placeholder="https://ejemplo.com/imagen.jpg"
              />
              {formData.imagenUrl && (
                <div className="image-preview">
                  <img src={formData.imagenUrl} alt="Preview" onError={(e) => e.target.style.display = 'none'} />
                </div>
              )}
              <small style={{color: '#a0a0a0', fontSize: '12px', display: 'block', marginTop: '8px'}}>
                Ingresa la URL de una imagen (puede ser de Google Images, Imgur, etc.)
              </small>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Precio ($)</label>
                <input
                  type="number"
                  step="0.01"
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
                  name="stock"
                  value={formData.stock}
                  onChange={handleChange}
                  required
                  placeholder="0"
                />
              </div>
            </div>

            <div className="form-actions">
              <button type="button" onClick={cancelForm} className="btn-cancel">
                Cancelar
              </button>
              <button type="submit" className="btn-save">
                {editingProduct ? 'Actualizar' : 'Guardar'}
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
            {productos.map(producto => (
              <tr key={producto.id}>
                <td>{producto.id}</td>
                <td>
                  {producto.imagenUrl ? (
                    <img src={producto.imagenUrl} alt={producto.nombre} className="product-thumbnail" />
                  ) : (
                    <div className="no-image"><FaImage /></div>
                  )}
                </td>
                <td>{producto.nombre}</td>
                <td>{producto.descripcion}</td>
                <td>${producto.precio.toFixed(2)}</td>
                <td>
                  <span className={`stock-badge ${producto.stock === 0 ? 'out-of-stock' : ''}`}>
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
                  >
                    🗑 Eliminar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

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

export default AdminPanel;
