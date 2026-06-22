import { useState } from 'react';
import { getToken } from './api';
import LoginPage from './LoginPage';
import ShippingPage from './ShippingPage';

export default function App() {
  const [authenticated, setAuthenticated] = useState(() => !!getToken());

  return (
    <div className="app-shell">
      {authenticated ? (
        <ShippingPage onLogout={() => setAuthenticated(false)} />
      ) : (
        <LoginPage onLoggedIn={() => setAuthenticated(true)} />
      )}
    </div>
  );
}
