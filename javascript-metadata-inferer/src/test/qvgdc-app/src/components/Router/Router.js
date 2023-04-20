import Admin from 'components/Admin/Admin';
import Game from 'components/Game/Game';
import Home from 'components/Home/Home';
import Login from 'components/Login/Login';
import RouteNotFound from 'components/RouteNotFound/RouteNotFound';
import React, { useContext } from 'react';
import { BrowserRouter, Redirect, Route, Switch } from 'react-router-dom';
import authContext, { playerContext } from 'store';

function AuthenticateRoute({ component, ...options }) {
  const [{ isLoggedIn }] = useContext(authContext);
  const componentToRender = isLoggedIn ? component : Login;

  return <Route {...options} component={componentToRender} />;
}

function GameRoute({ component, ...options }) {
  const [{ id }] = useContext(playerContext);

  if (id) return <Route {...options} component={component} />;

  return <Redirect to="/" />;
}

function Router() {
  return (
    <BrowserRouter>
      <Switch>
        <Route exact path="/" component={Home} />
        <GameRoute exact path="/game/:gameId" component={Game} />
        <Route exact path="/login" component={Login} />
        <AuthenticateRoute path="/admin" component={Admin} />
        <Route component={RouteNotFound} />
      </Switch>
    </BrowserRouter>
  );
}

export default Router;
