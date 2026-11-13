import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';
import Login from './components/Login';
import Register from './components/Register';
import ProductList from './components/ProductList';
import Cart from './components/Cart';
import Navbar from './components/Navbar';
import AdminPanel from './components/AdminPanel';
import Orders from './components/Orders';
import { clearSession, getSession, isAdmin } from './session';
import { catalogoApi, contarItems } from './api/catalogoApi';
import { SESSION_EXPIRED_EVENT } from './api/client';

function inicioPara(user) {
  return isAdmin(user) ? '/admin' : '/productos';
}

function RutaPrivada({ user, children }) {
  return user ? children : <Navigate to="/login" replace />;
}

function RutaAdmin({ user, children }) {
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  return isAdmin(user) ? children : <Navigate to="/productos" replace />;
}

function App() {
  const [user, setUser] = useState(null);
  const [inicializando, setInicializando] = useState(true);
  const [cartCount, setCartCount] = useState(0);

  useEffect(() => {
    const session = getSession();
    setUser(session?.user || null);
    setInicializando(false);

    const handleSessionExpired = () => {
      setUser(null);
      setCartCount(0);
    };

    window.addEventListener(SESSION_EXPIRED_EVENT, handleSessionExpired);
    return () =>
      window.removeEventListener(SESSION_EXPIRED_EVENT, handleSessionExpired);
  }, []);

  useEffect(() => {
    if (!user || isAdmin(user)) {
      setCartCount(0);
      return undefined;
    }

    let activo = true;
    catalogoApi.carrito
      .obtener()
      .then((carrito) => {
        if (activo) {
          setCartCount(contarItems(carrito));
        }
      })
      .catch(() => {
        if (activo) {
          setCartCount(0);
        }
      });

    return () => {
      activo = false;
    };
  }, [user]);

  const handleLogin = (loggedUser) => {
    setUser(loggedUser);
  };

  const handleLogout = () => {
    clearSession();
    setUser(null);
    setCartCount(0);
  };

  if (inicializando) {
    return (
      <div className="loading">
        <div className="spinner"></div>
        Cargando...
      </div>
    );
  }

  return (
    <Router>
      <div className="App">
        <Navbar user={user} onLogout={handleLogout} cartCount={cartCount} />

        <Routes>
          <Route
            path="/login"
            element={
              user ? (
                <Navigate to={inicioPara(user)} replace />
              ) : (
                <Login onLogin={handleLogin} />
              )
            }
          />

          <Route
            path="/register"
            element={
              user ? (
                <Navigate to={inicioPara(user)} replace />
              ) : (
                <Register />
              )
            }
          />

          <Route
            path="/admin"
            element={
              <RutaAdmin user={user}>
                <AdminPanel />
              </RutaAdmin>
            }
          />

          <Route
            path="/productos"
            element={
              <RutaPrivada user={user}>
                <ProductList setCartCount={setCartCount} />
              </RutaPrivada>
            }
          />

          <Route
            path="/carrito"
            element={
              <RutaPrivada user={user}>
                <Cart setCartCount={setCartCount} />
              </RutaPrivada>
            }
          />

          <Route
            path="/compras"
            element={
              <RutaPrivada user={user}>
                <Orders user={user} />
              </RutaPrivada>
            }
          />

          <Route
            path="/"
            element={
              <Navigate to={user ? inicioPara(user) : '/login'} replace />
            }
          />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
