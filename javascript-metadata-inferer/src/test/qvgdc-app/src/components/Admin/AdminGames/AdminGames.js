import { useMutation, useQuery } from '@apollo/client';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import TextField from '@mui/material/TextField';
import AdminWizard from 'components/Admin/AdminWizard/AdminWizard';
import NetworkStatus from 'components/NetworkStatus/NetworkStatus';
import React, { useState } from 'react';
import { Edit, PlayCircle, Trash2 } from 'react-feather';
import { useHistory } from 'react-router-dom';
import CrudList from '../CrudList/CrudList';
import { ADD_GAME, ADD_JSON_GAME, DELETE_GAME, UPDATE_GAME, USER_QUERY } from './AdminGames.actions';
import styles from './AdminGames.module.scss';

const AdminGames = () => {
  const [input, setInput] = useState('');
  const history = useHistory();
  const { loading, error, data, refetch } = useQuery(USER_QUERY);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [show, setShow] = useState('')
  const [deleteGameMutation] = useMutation(DELETE_GAME, {
    onCompleted() {
      refetch();
    },
  });

  const [updateGameMutation, { loading: updatingGame }] = useMutation(UPDATE_GAME, {
    onCompleted() {
      refetch();
    },
  });

  const [addGameMutation] = useMutation(ADD_GAME, {
    onCompleted() {
      refetch();
    },
  });

  const [addJsonGameMutation] = useMutation(ADD_JSON_GAME, {
    onCompleted() {
      refetch();
    },
  });

  const addGame = () => {
    setDialogOpen(false);
    addGameMutation({
      variables: { title: input },
    });
    setInput('');
  };

  const addJsonGame = () => {
    setDialogOpen(false);
    addJsonGameMutation({
      variables: { json: input },
    });
    setInput('');
  };

  const toggleOpenGame = (gameId, state) => {
    updateGameMutation({ variables: { gameId, data: { open: state } } });
  };

  const deleteGame = (games, gameId) => {
    data.me.games.filter((g) => g.id !== gameId);

    deleteGameMutation({
      variables: { gameId },
    });
  };


  if (loading) return <div>Chargement des parties...</div>;
  if (error) return <div>Erreur lors du chargement des parties, veuillez réessayer.</div>;

  const gamesToRender = data.me.games;
  const dataTable = {
    columns: [
      { title: 'Titre', slug: 'title' },
      { title: 'Joueurs', slug: 'players', content: (game) => game.players.length },
      {
        title: 'Statut',
        slug: 'open',
        content: (game) => (
          <>
            <IconButton disabled={updatingGame} size="small" onClick={() => toggleOpenGame(game.id, !game.open)}>
              <NetworkStatus online={game.open} />
            </IconButton>
          </>
        ),
      },
      {
        title: 'Terminée',
        slug: 'finish',
        content: (game) => (
          <>
            <IconButton size="small">
              <NetworkStatus online={game.finish} />
            </IconButton>
          </>
        ),
      },
      {
        title: '',
        slug: 'actions',
        align: 'right',
        content: (game) => (
          <>
            <IconButton onClick={() => history.push(`/admin/${game.id}`)} size="small">
              <Edit size="16" />
            </IconButton>
            <IconButton size="small" onClick={() => deleteGame(gamesToRender, game.id)}>
              <Trash2 size="16" />
            </IconButton>
            <IconButton onClick={() => history.push(`/admin/${game.id}/play`)} size="small">
              <PlayCircle size="16" />
            </IconButton>
          </>
        ),
      },
    ],
    data: gamesToRender,
    title: 'Liste des parties',
    actions: (
      <div className={styles.buttons}>
        <Button size="small" onClick={() => { setShow("add"); setDialogOpen(true) }} variant="contained" color="primary">
          Ajouter une partie
        </Button>
        <Button size="small" onClick={() => { setShow("json"); setDialogOpen(true) }} variant="contained" color="primary">
          Importer un JSON
        </Button>
      </div>
    ),
  };

  const dataDialog = {
    title: 'Ajouter une partie',
    description: 'Pour ajouter une partie, veuillez renseigner son nom.',
    open: dialogOpen,
    closeDialog: setDialogOpen,
    completeDialog: addGame,
    fields: [
      <TextField
        key="name"
        onChange={(e) => setInput(e.target.value)}
        autoFocus
        margin="dense"
        id="name"
        label="Nom de la partie"
        fullWidth
        variant="outlined"
      />,
    ],
  };

  const dataJsonDialog = {
    title: 'Importer un JSON',
    description: ['Créer une partie à partir d\'un fichier JSON.', <br />, 'Pour le format requis, voir sur ', <a href="https://github.com/Zenika/qvgdc-app/blob/main/example-party.json" rel="noreferrer noopener" target="_blank">GitHub</a>, '.'],
    open: dialogOpen,
    closeDialog: setDialogOpen,
    completeDialog: addJsonGame,
    fields: [
      <TextField
        key="jsonGame"
        multiline
        onChange={(e) => setInput(e.target.value)}
        autoFocus
        fullWidth
        minRows="8"
        margin="dense"
        id="jsonGame"
        placeholder="{ }"
        variant="outlined"
        className={styles.jsontextarea}
      />,
    ],
  };

  return (
    <>
      <AdminWizard />
      <br />
      <CrudList table={dataTable} dialog={show === "json" ? dataJsonDialog : dataDialog} />
    </>
  );
};

export default AdminGames;
