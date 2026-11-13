import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Auth.css';
import { authApi } from '../api/authApi';

function Register() {
  const [formData, setFormData] = useState({
    nombre: '',
    email: '',
    password: '',
    confirmPassword: '',
    telefono: '',
    edad: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === 'telefono') {
      const soloDigitos = value.replace(/\D/g, '').slice(0, 10);
      setFormData({ ...formData, telefono: soloDigitos });
      return;
    }

    setFormData({ ...formData, [name]: value });
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
      await authApi.register({
        nombre: formData.nombre.trim(),
        email: formData.email.trim(),
        password: formData.password,
        telefono: `+57${formData.telefono}`,
        edad: parseInt(formData.edad, 10),
      });

      navigate('/login', {
        replace: true,
        state: { message: 'Registro exitoso. Ya puedes iniciar sesión.' },
      });
    } catch (err) {
      setError(err.message || 'No se pudo completar el registro');
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
              autoComplete="name"
              maxLength="100"
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
              autoComplete="email"
              maxLength="120"
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
                autoComplete="tel-national"
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
              autoComplete="new-password"
              required
              minLength="6"
              maxLength="72"
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
              autoComplete="new-password"
              required
              minLength="6"
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
