import { useMutation, useQuery } from '@apollo/client';
import React from 'react';
import { ChevronRight, Home } from 'react-feather';
import { Link, useParams } from 'react-router-dom';
import Choices from '../Choices/Choices';
import { QUESTIONDETAIL_QUERY, UPDATE_QUESTION } from './QuestionDetail.actions';

const AdminGameDetail = () => {
  let { gameId, questionId } = useParams();
  const { loading, error, data, refetch } = useQuery(QUESTIONDETAIL_QUERY, {
    variables: {
      questionId,
    },
  });

  const [updateQuestionMutation, { loading: updatingQuestion }] = useMutation(UPDATE_QUESTION, {
    onCompleted() {
      refetch();
    },
  });

  const updateGoodChoiceQuestion = (questionId, goodChoiceId) => {
    updateQuestionMutation({ variables: { questionId, data: { goodChoiceId: goodChoiceId } } });
  };

  if (loading) return <div>Chargement de la question...</div>;
  if (error) return <div>Problème lors du chargement de la question</div>;

  return (
    <>
      <p>
        <Link to="/admin">
          <Home size="16" />
        </Link>
        <ChevronRight size="12" />
        <Link to={`/admin/${gameId}`}>{data.question.game.title}</Link>
        <ChevronRight size="12" />
        {data.question.title}
      </p>

      <h2>{data.question.title}</h2>
      <Choices
        updatingQuestion={updatingQuestion}
        questionId={questionId}
        updateGoodChoiceQuestion={updateGoodChoiceQuestion}
        goodChoiceId={data.question.goodChoice ? data.question.goodChoice.id : null}
      />
    </>
  );
};

export default AdminGameDetail;
