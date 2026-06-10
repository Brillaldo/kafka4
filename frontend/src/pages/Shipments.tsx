import React, { useState, useEffect } from 'react';
import { getShipments } from '../services/api';

const Shipments = () => {
  const [shipments, setShipments] = useState<any[]>([]);

  useEffect(() => {
    const interval = setInterval(loadShipments, 3000);
    loadShipments();
    return () => clearInterval(interval);
  }, []);

  const loadShipments = async () => {
    try {
      const res = await getShipments();
      setShipments(res.data);
    } catch (err) {}
  };

  return (
    <div>
      <h2 className="card-title">Envíos Programados (PostgreSQL)</h2>

      <div className="card">
        <table>
          <thead>
            <tr>
              <th>ID Envío</th>
              <th>ID Orden</th>
              <th>Status</th>
              <th>Fecha Registro</th>
            </tr>
          </thead>
          <tbody>
            {shipments.length === 0 ? (
              <tr><td colSpan={4} style={{ textAlign: 'center', padding: '2rem' }}>No hay envíos registrados.</td></tr>
            ) : (
              shipments.map(s => (
                <tr key={s.id}>
                  <td>{s.id}</td>
                  <td style={{ fontSize: '0.75rem' }}>{s.ordenId}</td>
                  <td>
                    <span className="badge badge-success">
                      {s.status}
                    </span>
                  </td>
                  <td>{new Date(s.sentAt).toLocaleString()}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Shipments;
