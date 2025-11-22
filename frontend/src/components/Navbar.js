import React from 'react';
import { Link } from 'react-router-dom';
import './Navbar.css';
import { FaGem, FaStore, FaShoppingCart, FaBox, FaCog, FaUser, FaSignOutAlt, FaSignInAlt, FaUserPlus } from 'react-icons/fa';

function Navbar({ user, onLogout, cartCount }) {
  return (
    <nav className="navbar">
      <div className="nav-container">
        <Link to="/" className="nav-logo">
          <FaGem /> E-Commerce
        </Link>
        
        <div className="nav-menu">
          {user ? (
            <>
              {user.role === 'ADMIN' ? (
                <>
                  <Link to="/admin" className="nav-link"><FaCog /> Panel Admin</Link>
                  <Link to="/productos" className="nav-link"><FaStore /> Ver Tienda</Link>
                </>
              ) : (
                <>
                  <Link to="/productos" className="nav-link"><FaStore /> Productos</Link>
                  <Link to="/carrito" className="nav-link cart-link">
                    <FaShoppingCart /> Carrito
                    {cartCount > 0 && <span className="cart-badge">{cartCount}</span>}
                  </Link>
                  <Link to="/compras" className="nav-link"><FaBox /> Mis Compras</Link>
                </>
              )}
              <span className="nav-user"><FaUser /> {user.nombre}</span>
              <button onClick={onLogout} className="nav-btn logout-btn"><FaSignOutAlt /> Cerrar Sesión</button>
            </>
          ) : (
            <>
              <Link to="/login" className="nav-btn"><FaSignInAlt /> Iniciar Sesión</Link>
              <Link to="/register" className="nav-btn register-btn"><FaUserPlus /> Registrarse</Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
