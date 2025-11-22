import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom';
import './App.css';
import Login from './components/Login';
import Register from './components/Register';
import ProductList from './components/ProductList';
import Cart from './components/Cart';
import Navbar from './components/Navbar';
import AdminPanel from './components/AdminPanel';
import Orders from './components/Orders';

function App() {
  const [user, setUser] = useState(null);
  const [cartCount, setCartCount] = useState(0);

  useEffect(() => {
    // Verificar si hay un usuario en localStorage
    try {
      const storedUser = localStorage.getItem('user');
      if (storedUser && storedUser !== 'undefined' && storedUser !== 'null') {
        setUser(JSON.parse(storedUser));
      } else {
        // Limpiar localStorage si tiene valores inválidos
        localStorage.removeItem('user');
        localStorage.removeItem('token');
      }
    } catch (error) {
      console.error('Error al cargar usuario:', error);
      localStorage.removeItem('user');
      localStorage.removeItem('token');
    }
  }, []);

  const handleLogin = (userData) => {
    setUser(userData);
    localStorage.setItem('user', JSON.stringify(userData));
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  };

  return (
    <Router>
      <div className="App">
        <Navbar user={user} onLogout={handleLogout} cartCount={cartCount} />
        
        <Routes>
          <Route path="/login" element={
            user ? <Navigate to={user.role === 'ADMIN' ? '/admin' : '/productos'} /> : <Login onLogin={handleLogin} />
          } />
          
          <Route path="/register" element={
            user ? <Navigate to={user.role === 'ADMIN' ? '/admin' : '/productos'} /> : <Register />
          } />
          
          <Route path="/admin" element={
            user && user.role === 'ADMIN' ? <AdminPanel user={user} /> : <Navigate to="/login" />
          } />
          
          <Route path="/productos" element={
            user ? <ProductList user={user} setCartCount={setCartCount} /> : <Navigate to="/login" />
          } />
          
          <Route path="/carrito" element={
            user ? <Cart user={user} cartCount={cartCount} setCartCount={setCartCount} /> : <Navigate to="/login" />
          } />
          
          <Route path="/compras" element={
            user ? <Orders user={user} /> : <Navigate to="/login" />
          } />
          
          <Route path="/" element={<Navigate to={user ? (user.role === 'ADMIN' ? '/admin' : '/productos') : '/login'} />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
