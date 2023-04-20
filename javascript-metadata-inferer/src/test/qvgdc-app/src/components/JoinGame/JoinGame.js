import { gql, useMutation } from '@apollo/client';
import { Button, TextField } from '@mui/material';
import GameDetail from 'components/GameDetail/GameDetail';
import React, { useContext, useState } from 'react';
import { useHistory } from 'react-router-dom';
import { playerContext } from 'store';

export const NEW_PLAYER = gql`
  mutation newPlayer($gameId: ID!, $name: String!) {
    newPlayer(gameId: $gameId, name: $name) {
      id
      name
    }
  }
`;

const JoinGame = ({ game }) => {
  const dispatch = useContext(playerContext)[1];
  const history = useHistory();
  const [playerName, setPlayerName] = useState('');
  const [newPlayerMutation, { loading, error }] = useMutation(NEW_PLAYER, {
    onCompleted(completeData) {
      localStorage.setItem('player', JSON.stringify({ id: completeData.newPlayer.id, name: completeData.newPlayer.name }));

      dispatch({
        type: 'JOIN',
        payload: {
          id: completeData.newPlayer.id,
          name: completeData.newPlayer.name,
        },
      });

      history.push(`/game/${game.id}`);
    },
  });

  const handleClick = () => {
    if (playerName !== '') {
      newPlayerMutation({
        variables: {
          gameId: game.id,
          name: playerName,
        },
      });
    }
  };

  return (
    <>
      <GameDetail game={game} />

      <TextField
        type="text"
        required
        label="Nom de joueur"
        margin="normal"
        variant="outlined"
        onChange={(e) => setPlayerName(e.target.value)}
      />
      <br />
      {loading ? (
        <p>Inscription à la partie...</p>
      ) : (
        <>
          <Button variant="contained" color="primary" onClick={handleClick}>
            Rejoindre la partie
          </Button>
          {error ? <p>{error.message}</p> : null}
        </>
      )}
    </>
  );
};
export default JoinGame;
