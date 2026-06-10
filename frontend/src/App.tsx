import React from 'react';
import { BrowserRouter, Routes, Route, NavLink } from 'react-router-dom';
import { Package, ShoppingCart, CreditCard, Truck } from 'lucide-react';
import Products from './pages/Products';
import Orders from './pages/Orders';
import Payments from './pages/Payments';
import Shipments from './pages/Shipments';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <div className="app-container">
        <aside className="sidebar">
          <h1>MicroStore</h1>
          <nav className="nav-links">
            <NavLink to="/" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
              <Package size={20} /> Productos
            </NavLink>
            <NavLink to="/orders" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
              <ShoppingCart size={20} /> Órdenes
            </NavLink>
            <NavLink to="/payments" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
              <CreditCard size={20} /> Pagos
            </NavLink>
            <NavLink to="/shipments" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
              <Truck size={20} /> Envíos
            </NavLink>
          </nav>
        </aside>

        <main className="main-content">
          <Routes>
            <Route path="/" element={<Products />} />
            <Route path="/orders" element={<Orders />} />
            <Route path="/payments" element={<Payments />} />
            <Route path="/shipments" element={<Shipments />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default App;
