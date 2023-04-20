import { useMutation, useQuery } from '@apollo/client';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import TextField from '@mui/material/TextField';
import React, { useState } from 'react';
import { Edit, Trash2 } from 'react-feather';
import { useHistory } from 'react-router-dom';
import CrudList from '../CrudList/CrudList';
import { ADD_QUESTION, DELETE_QUESTION, QUESTION_QUERY } from './AdminQuestions.actions';

const AdminQuestions = (props) => {
  const [input, setInput] = useState('');
  const [duration, setDuration] = useState('');

  const history = useHistory();
  const { loading, error, data, refetch } = useQuery(QUESTION_QUERY, {
    variables: {
      gameId: props.gameId,
    },
  });
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteQuestionMutation] = useMutation(DELETE_QUESTION, {
    onCompleted() {
      refetch();
    },
  });

  const [addQuestionMutation] = useMutation(ADD_QUESTION, {
    onCompleted() {
      refetch();
    },
  });

  const addQuestion = () => {
    setDialogOpen(false);
    addQuestionMutation({
      variables: { title: input, duration: parseInt(duration), gameId: props.gameId },
    });
    setInput('');
    setDuration('');
  };

  const deleteQuestion = (questions, questionId) => {
    data.questions.filter((g) => g.id !== questionId);

    deleteQuestionMutation({
      variables: { questionId },
    });
  };

  if (loading) return <div>Chargement des questions...</div>;
  if (error) return <div>Erreur lors du chargement des questions, veuillez réessayer.</div>;

  const questionsToRender = data.questions;
  const dataTable = {
    columns: [
      { title: 'Titre', slug: 'title' },
      { title: 'Durée', slug: 'duration', content: (question) => `${question.duration}s` },
      {
        title: '',
        slug: 'actions',
        align: 'right',
        content: (question) => (
          <>
            <IconButton onClick={() => history.push(`/admin/${props.gameId}/questions/${question.id}`)} size="small">
              <Edit size="16" />
            </IconButton>
            <IconButton size="small" onClick={() => deleteQuestion(questionsToRender, question.id)}>
              <Trash2 size="16" />
            </IconButton>
          </>
        ),
      },
    ],
    data: questionsToRender,
    title: 'Liste des questions',
    actions: (
      <Button size="small" onClick={() => setDialogOpen(true)} variant="contained" color="primary">
        Ajouter une question
      </Button>
    ),
  };

  const dataDialog = {
    title: 'Ajouter une question',
    description: 'Quelle question voulez-vous ajouter ?',
    open: dialogOpen,
    closeDialog: setDialogOpen,
    completeDialog: addQuestion,
    fields: [
      <TextField
        key="name"
        onChange={(e) => setInput(e.target.value)}
        autoFocus
        margin="dense"
        id="name"
        label="Question"
        fullWidth
        variant="outlined"
      />,
      <TextField
        type="number"
        key="duration"
        onChange={(e) => setDuration(e.target.value)}
        margin="dense"
        id="duration"
        label="Durée de la question"
        fullWidth
        variant="outlined"
      />,
    ],
  };

  return (
    <>
      <CrudList table={dataTable} dialog={dataDialog} />
    </>
  );
};

export default AdminQuestions;
