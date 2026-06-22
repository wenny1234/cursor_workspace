import { useCallback, useEffect, useState } from 'react';
import {
  Order,
  clearToken,
  fetchOrders,
  formatYen,
  getToken,
  shipOrder,
  statusLabel,
} from './api';

interface ShippingPageProps {
  onLogout: () => void;
}

type TabStatus = 'PAID' | 'SHIPPING';

export default function ShippingPage({ onLogout }: ShippingPageProps) {
  const [tab, setTab] = useState<TabStatus>('PAID');
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
  const [shippingAddress, setShippingAddress] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const loadOrders = useCallback(async () => {
    if (!getToken()) {
      onLogout();
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const res = await fetchOrders(tab);
      setOrders(res.orders ?? []);
    } catch (err) {
      setError(err instanceof Error ? err.message : '注文の取得に失敗しました');
      setOrders([]);
    } finally {
      setLoading(false);
    }
  }, [tab, onLogout]);

  useEffect(() => {
    loadOrders();
  }, [loadOrders]);

  function openShipModal(order: Order) {
    setSelectedOrder(order);
    setShippingAddress('');
    setError(null);
  }

  function closeShipModal() {
    setSelectedOrder(null);
    setShippingAddress('');
  }

  async function handleShip() {
    if (!selectedOrder) return;

    setSubmitting(true);
    setError(null);

    try {
      await shipOrder(selectedOrder.id, shippingAddress);
      closeShipModal();
      await loadOrders();
    } catch (err) {
      setError(err instanceof Error ? err.message : '出荷処理に失敗しました');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div>
      <header className="page-header">
        <div>
          <h1>出荷処理</h1>
          <p className="subtitle">支払済み注文の送り場所を登録して出荷します</p>
        </div>
        <button className="secondary-btn" type="button" onClick={() => { clearToken(); onLogout(); }}>
          ログアウト
        </button>
      </header>

      <div className="tabs">
        <button
          type="button"
          className={`tab-btn ${tab === 'PAID' ? 'active' : ''}`}
          onClick={() => setTab('PAID')}
        >
          出荷待ち（支払済）
        </button>
        <button
          type="button"
          className={`tab-btn ${tab === 'SHIPPING' ? 'active' : ''}`}
          onClick={() => setTab('SHIPPING')}
        >
          出荷済み（配送中）
        </button>
      </div>

      {loading && <div className="card">読み込み中...</div>}
      {!loading && error && !selectedOrder && <div className="card error-msg">{error}</div>}
      {!loading && !error && orders.length === 0 && (
        <div className="card">
          {tab === 'PAID' ? '出荷待ちの注文はありません。' : '出荷済みの注文はありません。'}
        </div>
      )}

      {!loading &&
        orders.map((order) => (
          <div className="card order-card" key={order.id}>
            <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12 }}>
              <strong>{order.orderNumber}</strong>
              <span className={`status-badge ${tab === 'PAID' ? 'paid' : 'shipping'}`}>
                {statusLabel(order.status)}
              </span>
            </div>
            <div className="order-meta">合計: {formatYen(order.totalAmount)}</div>
            <div className="order-meta">ユーザー ID: {order.userId}</div>
            {order.shippingAddress && (
              <div className="order-meta">送り場所: {order.shippingAddress}</div>
            )}
            {order.items && order.items.length > 0 && (
              <div className="order-items">
                {order.items.map((item) => (
                  <div key={item.id}>
                    {item.productName} × {item.quantity}
                  </div>
                ))}
              </div>
            )}
            {tab === 'PAID' && (
              <button
                className="primary-btn"
                type="button"
                style={{ marginTop: 12, width: 'auto' }}
                onClick={() => openShipModal(order)}
              >
                出荷する
              </button>
            )}
          </div>
        ))}

      {selectedOrder && (
        <div className="modal-backdrop" role="dialog" aria-modal="true">
          <div className="modal">
            <h2 style={{ marginTop: 0 }}>出荷登録</h2>
            <p className="subtitle">{selectedOrder.orderNumber}</p>

            {error && <p className="error-msg">{error}</p>}

            <div className="form-group">
              <label htmlFor="shippingAddress">送り場所</label>
              <textarea
                id="shippingAddress"
                value={shippingAddress}
                onChange={(e) => setShippingAddress(e.target.value)}
                placeholder="例: 東京都千代田区 1-2-3"
                required
              />
            </div>

            <div className="modal-actions">
              <button className="secondary-btn" type="button" onClick={closeShipModal} disabled={submitting}>
                キャンセル
              </button>
              <button
                className="primary-btn"
                type="button"
                onClick={handleShip}
                disabled={submitting || !shippingAddress.trim()}
              >
                {submitting ? '処理中...' : '出荷済みにする'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
