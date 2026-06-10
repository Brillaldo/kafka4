import React, { useState, useEffect } from 'react';
import { getOrders, createOrder, getProducts } from '../services/api';
import { Plus, Eye } from 'lucide-react';

const Orders = () => {
  const [orders, setOrders] = useState<any[]>([]);
  const [products, setProducts] = useState<any[]>([]);
  const [selectedProducts, setSelectedProducts] = useState<string[]>([]);
  const [error, setError] = useState('');

  useEffect(() => {
    loadOrders();
    loadProducts();
  }, []);

  const loadOrders = async () => {
    try {
      const res = await getOrders();
      setOrders(res.data);
    } catch (err) {}
  };

  const loadProducts = async () => {
    try {
      const res = await getProducts();
      setProducts(res.data);
    } catch (err) {}
  };

  const handleCreateOrder = async () => {
    if (selectedProducts.length === 0) {
      setError('Selecciona al menos un producto.');
      return;
    }
    try {
      await createOrder({
        usuarioId: 'User1', // Mock
        productosIds: selectedProducts,
        total: 100 // Mock, backend could calculate this
      });
      setSelectedProducts([]);
      setError('');
      loadOrders();
    } catch (err: any) {
      setError(err.response?.data || 'Error al crear orden');
    }
  };

  const toggleProduct = (id: string) => {
    if (selectedProducts.includes(id)) {
      setSelectedProducts(selectedProducts.filter(pid => pid !== id));
    } else {
      setSelectedProducts([...selectedProducts, id]);
    }
  };

  return (
    <div>
      <h2 className="card-title">Órdenes de Compra</h2>

      <div className="card">
        <h3 className="card-title">Crear Nueva Orden</h3>
        <div className="grid">
          <div>
            <label style={{ fontSize: '0.875rem', fontWeight: 600 }}>Seleccionar Productos</label>
            <div style={{ maxHeight: '200px', overflowY: 'auto', border: '1px solid var(--border)', borderRadius: '0.375rem', padding: '0.5rem', marginTop: '0.5rem' }}>
              {products.map(p => (
                <div key={p.id} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', padding: '0.25rem' }}>
                  <input 
                    type="checkbox" 
                    checked={selectedProducts.includes(p.id)} 
                    onChange={() => toggleProduct(p.id)} 
                    style={{ width: 'auto' }}
                  />
                  <span>{p.name} - ${p.price} (Stock: {p.stock})</span>
                </div>
              ))}
            </div>
          </div>
          <div style={{ display: 'flex', alignItems: 'flex-end' }}>
            <button onClick={handleCreateOrder} className="btn btn-primary" style={{ width: '100%' }}>
              Crear Orden
            </button>
          </div>
        </div>
        {error && <p style={{ color: 'var(--danger)', fontSize: '0.875rem' }}>{error}</p>}
      </div>

      <div className="card">
        <table>
          <thead>
            <tr>
              <th>ID Orden</th>
              <th>Status</th>
              <th>Total</th>
              <th>Productos</th>
            </tr>
          </thead>
          <tbody>
            {orders.map(o => (
              <tr key={o.id}>
                <td style={{ fontSize: '0.75rem' }}>{o.id}</td>
                <td>
                  <span className={`badge badge-${o.status?.toLowerCase() === 'paid' ? 'success' : 'pending'}`}>
                    {o.status}
                  </span>
                </td>
                <td>${o.total}</td>
                <td>{o.productosIds?.length} items</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Orders;
