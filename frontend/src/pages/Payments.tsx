import React, { useState } from 'react';
import { processPayment } from '../services/api';

const Payments = () => {
  const [payment, setPayment] = useState({ ordenId: '', monto: 0, metodo: 'tarjeta' });
  const [message, setMessage] = useState('');
  const [isError, setIsError] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (payment.monto <= 0) {
      setMessage('El monto debe ser mayor a cero.');
      setIsError(true);
      return;
    }
    try {
      await processPayment(payment);
      setMessage('Pago procesado correctamente.');
      setIsError(false);
      setPayment({ ordenId: '', monto: 0, metodo: 'tarjeta' });
    } catch (err: any) {
      setMessage(err.response?.data || 'Error al procesar pago');
      setIsError(true);
    }
  };

  return (
    <div style={{ maxWidth: '600px', margin: '0 auto' }}>
      <h2 className="card-title">Procesar Pago</h2>
      
      <div className="card">
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>ID de la Orden</label>
            <input 
              type="text" 
              value={payment.ordenId} 
              onChange={e => setPayment({...payment, ordenId: e.target.value})} 
              required 
            />
          </div>
          <div className="form-group">
            <label>Monto</label>
            <input 
              type="number" 
              step="0.01" 
              value={payment.monto} 
              onChange={e => setPayment({...payment, monto: parseFloat(e.target.value)})} 
              required 
            />
          </div>
          <div className="form-group">
            <label>Método de Pago</label>
            <select value={payment.metodo} onChange={e => setPayment({...payment, metodo: e.target.value})}>
              <option value="tarjeta">Tarjeta de Crédito</option>
              <option value="transferencia">Transferencia</option>
              <option value="efectivo">Efectivo</option>
            </select>
          </div>
          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '1rem' }}>
            Pagar Ahora
          </button>
        </form>

        {message && (
          <p style={{ 
            marginTop: '1rem', 
            padding: '0.75rem', 
            borderRadius: '0.375rem',
            backgroundColor: isError ? '#fee2e2' : '#dcfce7',
            color: isError ? '#991b1b' : '#166534',
            fontSize: '0.875rem'
          }}>
            {message}
          </p>
        )}
      </div>
    </div>
  );
};

export default Payments;
