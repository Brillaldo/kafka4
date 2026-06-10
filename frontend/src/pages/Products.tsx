import React, { useState, useEffect } from 'react';
import { getProducts, createProduct, deleteProduct } from '../services/api';
import { Trash2, Plus } from 'lucide-react';

interface Product {
  id: string;
  name: string;
  price: number;
  stock: number;
}

const Products = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [newProduct, setNewProduct] = useState({ name: '', price: 0, stock: 0 });
  const [error, setError] = useState('');

  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    try {
      const res = await getProducts();
      setProducts(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (newProduct.stock <= 0) {
      setError('El stock debe ser mayor a cero.');
      return;
    }
    try {
      await createProduct(newProduct);
      setNewProduct({ name: '', price: 0, stock: 0 });
      setError('');
      loadProducts();
    } catch (err: any) {
      setError(err.response?.data || 'Error al crear producto');
    }
  };

  const handleDelete = async (id: string) => {
    try {
      await deleteProduct(id);
      loadProducts();
    } catch (err: any) {
      alert(err.response?.data || 'Error al eliminar producto');
    }
  };

  return (
    <div>
      <h2 className="card-title">Gestión de Productos</h2>

      <div className="card">
        <h3 className="card-title">Añadir Nuevo Producto</h3>
        <form onSubmit={handleCreate} className="grid">
          <div className="form-group">
            <label>Nombre</label>
            <input 
              type="text" 
              value={newProduct.name} 
              onChange={e => setNewProduct({...newProduct, name: e.target.value})} 
              required 
            />
          </div>
          <div className="form-group">
            <label>Precio</label>
            <input 
              type="number" 
              step="0.01" 
              value={newProduct.price} 
              onChange={e => setNewProduct({...newProduct, price: parseFloat(e.target.value)})} 
              required 
            />
          </div>
          <div className="form-group">
            <label>Stock</label>
            <input 
              type="number" 
              value={newProduct.stock} 
              onChange={e => setNewProduct({...newProduct, stock: parseInt(e.target.value)})} 
              required 
            />
          </div>
          <div className="form-group" style={{ display: 'flex', alignItems: 'flex-end' }}>
            <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>
              <Plus size={18} /> Añadir
            </button>
          </div>
        </form>
        {error && <p style={{ color: 'var(--danger)', fontSize: '0.875rem' }}>{error}</p>}
      </div>

      <div className="card">
        <table>
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Precio</th>
              <th>Stock</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {products.map(p => (
              <tr key={p.id}>
                <td>{p.name}</td>
                <td>${p.price.toFixed(2)}</td>
                <td>{p.stock}</td>
                <td>
                  <button onClick={() => handleDelete(p.id)} className="btn btn-danger">
                    <Trash2 size={16} />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Products;
