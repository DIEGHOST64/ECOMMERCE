import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './Auth.css';

function Register() {
  const [formData, setFormData] = useState({
    nombre: '',
    email: '',
    password: '',
    confirmPassword: '',
    telefono: '',
    edad: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    
    // Para el teléfono, asegurar que siempre tenga +57
    if (name === 'telefono') {
      let phoneValue = value.replace(/[^\d]/g, ''); // Solo números
      if (phoneValue.length > 10) {
        phoneValue = phoneValue.slice(0, 10); // Máximo 10 dígitos
      }
      setFormData({
        ...formData,
        telefono: phoneValue
      });
    } else {
      setFormData({
        ...formData,
        [name]: value
      });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (formData.password !== formData.confirmPassword) {
      setError('Las contraseñas no coinciden');
      return;
    }

    setLoading(true);

    try {
      await axios.post('http://localhost:9090/api/ecommerce/usuario/save', {
        nombre: formData.nombre,
        email: formData.email,
        password: formData.password,
        telefono: '+57' + formData.telefono,
        role: 'CLIENTE',
        edad: parseInt(formData.edad)
      });
      
      navigate('/login');
      alert('Registro exitoso. Por favor inicia sesión.');
    } catch (err) {
      setError(err.response?.data?.message || 'Error al registrarse');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>Crear Cuenta</h2>
        <p className="auth-subtitle">Únete a nuestra comunidad</p>
        
        {error && <div className="error-message">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Nombre</label>
            <input
              type="text"
              name="nombre"
              value={formData.nombre}
              onChange={handleChange}
              placeholder="Tu nombre completo"
              required
            />
          </div>
          
          <div className="form-group">
            <label>Email</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="tu@email.com"
              required
            />
          </div>
          
          <div className="form-group">
            <label>Teléfono</label>
            <div className="phone-input">
              <span className="phone-prefix">+57</span>
              <input
                type="tel"
                name="telefono"
                value={formData.telefono}
                onChange={handleChange}
                placeholder="3014346586"
                required
                maxLength="10"
                pattern="[0-9]{10}"
              />
            </div>
          </div>
          
          <div className="form-group">
            <label>Edad</label>
            <input
              type="number"
              name="edad"
              value={formData.edad}
              onChange={handleChange}
              placeholder="18"
              required
              min="1"
              max="120"
            />
          </div>
          
          <div className="form-group">
            <label>Contraseña</label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="••••••••"
              required
              minLength="6"
            />
          </div>
          
          <div className="form-group">
            <label>Confirmar Contraseña</label>
            <input
              type="password"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              placeholder="••••••••"
              required
            />
          </div>
          
          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Registrando...' : 'Registrarse'}
          </button>
        </form>
        
        <p className="auth-footer">
          ¿Ya tienes cuenta? <Link to="/login">Inicia sesión aquí</Link>
        </p>
      </div>
    </div>
  );
}

export default Register;
