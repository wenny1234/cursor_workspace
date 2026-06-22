import { useState, FormEvent } from 'react';
import { clearToken, login, setToken } from './api';

interface LoginPageProps {
  onLoggedIn: () => void;
}

export default function LoginPage({ onLoggedIn }: LoginPageProps) {
  const [username, setUsername] = useState('staff');
  const [password, setPassword] = useState('staff123');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const res = await login(username, password);
      if (res.role !== 'ADMIN' && res.role !== 'STAFF') {
        throw new Error('ADMIN または STAFF のみログインできます');
      }
      setToken(res.token);
      onLoggedIn();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'ログインに失敗しました');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="login-card card">
      <h1>WMS 出荷管理</h1>
      <p className="subtitle">倉庫出荷処理システム</p>

      <form onSubmit={handleSubmit}>
        {error && <p className="error-msg">{error}</p>}

        <div className="form-group">
          <label htmlFor="username">ユーザー名</label>
          <input
            id="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="password">パスワード</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
            required
          />
        </div>

        <button className="primary-btn" type="submit" disabled={loading}>
          {loading ? 'ログイン中...' : 'ログイン'}
        </button>
      </form>

      <p className="subtitle" style={{ marginTop: 16 }}>
        サンプル: staff / staff123
      </p>
      <button
        className="secondary-btn"
        type="button"
        style={{ marginTop: 8 }}
        onClick={() => {
          clearToken();
        }}
      >
        トークンをクリア
      </button>
    </div>
  );
}
