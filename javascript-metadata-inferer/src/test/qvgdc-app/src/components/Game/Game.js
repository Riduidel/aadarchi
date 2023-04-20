import { useQuery, useSubscription } from '@apollo/client';
import GameLayout from 'components/GameLayout/GameLayout';
import LeaderBoard from 'components/LeaderBoard/LeaderBoard';
import Loader from 'components/Loader/Loader';
import Question from 'components/Question/Question';
import React, { useContext, useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { playerContext } from 'store';
import { GAMEDETAIL_QUERY, GAMEDETAIL_SUBSCRIPTION, PLAYER_QUERY } from './Game.actions';
import styles from './Game.module.scss';

export const Game = () => {
  const [player, dispatch] = useContext(playerContext);

  let { gameId } = useParams();
  const { loading, error, data } = useQuery(GAMEDETAIL_QUERY, {
    variables: {
      gameId,
    },
  });

  const { data: dataPlayer, refetch: refetchPlayer } = useQuery(PLAYER_QUERY, {
    variables: {
      playerId: player.id,
    },
    onCompleted(_) {
      localStorage.setItem('player', JSON.stringify(_.player));

      dispatch({
        type: 'UPDATE',
        payload: { ..._.player },
      });
    },
  });

  const { data: data$ } = useSubscription(GAMEDETAIL_SUBSCRIPTION, {
    variables: {
      gameId,
    },
  });

  useEffect(() => {
    if (dataPlayer && dataPlayer.player) {
      localStorage.setItem('player', JSON.stringify(dataPlayer.player));

      dispatch({
        type: 'UPDATE',
        payload: { ...dataPlayer.player },
      });
    }
  }, [dataPlayer, dispatch]);

  if (loading) return <div>Chargement de la partie...</div>;
  if (error) return <div>Problème lors du chargement de la partie</div>;

  const currentGame = data$ ? data$.updatedGame : data.game;

  if (currentGame.finish)
    return (
      <GameLayout player={dataPlayer ? dataPlayer.player : player} game={currentGame}>
        <h3 className="text-center">Partie terminée</h3>
        <p className="text-center"><Link to={'/'}>Retour à la page d'accueil</Link></p>
        <br />
        <LeaderBoard players={currentGame.players} />
      </GameLayout>
    );

  const currentQuestion = currentGame.currentQuestion;

  return (
    <GameLayout player={player} game={currentGame}>
      {!currentQuestion ? (
        <>
          <p className={styles.waiting}>En attente de la question...</p>
          <Loader />
        </>
      ) : (
        <Question question={currentQuestion} onCompleteCounter={refetchPlayer} />
      )}
    </GameLayout>
  );
};

export default Game;
