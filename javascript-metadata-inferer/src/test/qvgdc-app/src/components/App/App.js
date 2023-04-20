import QvgdcTheme from 'components/QvgdcTheme/QvgdcTheme';
import Router from 'components/Router/Router';
import React, { useReducer } from 'react';
import { authReducer, initialAuthState, initialPlayerState, playerReducer } from 'reducers/authReducer';
import { AuthProvider, PlayerProvider } from 'store';

function App() {
  const useAuthState = useReducer(authReducer, initialAuthState);
  const usePlayerState = useReducer(playerReducer, initialPlayerState);

  return (
    <AuthProvider value={useAuthState}>
      <PlayerProvider value={usePlayerState}>
        <QvgdcTheme>
          <Router />
        </QvgdcTheme>
      </PlayerProvider>
    </AuthProvider>
  );
}

export default App;
