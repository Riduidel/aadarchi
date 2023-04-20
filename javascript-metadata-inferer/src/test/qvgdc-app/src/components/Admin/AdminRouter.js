import React from 'react';
import { Route, Switch, useRouteMatch } from 'react-router-dom';

import AdminGameDetail from './AdminGameDetail/AdminGameDetail';
import AdminGames from './AdminGames/AdminGames';
import PlayGame from './PlayGame/PlayGame';
import QuestionDetail from './QuestionDetail/QuestionDetail';

const AdminRouter = () => {
  let match = useRouteMatch();

  return (
    <Switch>
      <Route exact path={match.path} component={AdminGames} />
      <Route exact path={`${match.path}/:gameId`} component={AdminGameDetail} />
      <Route exact path={`${match.path}/:gameId/play`} component={PlayGame} />
      <Route exact path={`${match.path}/:gameId/questions/:questionId`} component={QuestionDetail} />
    </Switch>
  );
};

export default AdminRouter;
