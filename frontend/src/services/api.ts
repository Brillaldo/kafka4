import axios from 'axios';

const API_GATEWAY = 'http://localhost:8080';
// Note: As per instructions, we use specific ports if they are different, 
// but gateway is better. However, instructions say:
// Products: 3001, Orders: 3002, Pagos: 3003. 
// I will define constants for each.

const PRODUCTS_URL = 'http://localhost:3001';
const ORDERS_URL = 'http://localhost:3002';
const PAYMENTS_URL = 'http://localhost:3003';

export const productsApi = axios.create({ baseURL: PRODUCTS_URL });
export const ordersApi = axios.create({ baseURL: ORDERS_URL });
export const paymentsApi = axios.create({ baseURL: PAYMENTS_URL });

export const getProducts = () => productsApi.get('/productos');
export const createProduct = (data: any) => productsApi.post('/productos', data);
export const deleteProduct = (id: string) => productsApi.delete(`/productos/${id}`);

export const getOrders = () => ordersApi.get('/ordenes');
export const getOrderById = (id: string) => ordersApi.get(`/ordenes/${id}`);
export const createOrder = (data: any) => ordersApi.post('/ordenes', data);
export const getShipments = () => paymentsApi.get('/ordenes/envios');

export const processPayment = (data: any) => paymentsApi.post('/pagos/procesar', data);
export const getPaymentsByOrder = (orderId: string) => paymentsApi.get(`/pagos/orden/${orderId}`);
