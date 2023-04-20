import { useQuery } from '@apollo/client';
import React from 'react';
import { ChevronRight, Home } from 'react-feather';
import { Link, useParams } from 'react-router-dom';
import AdminQuestions from '../AdminQuestions/AdminQuestions';
import { GAMEDETAIL_QUERY } from './AdminGameDetail.action';

const AdminGameDetail = () => {
  let { gameId } = useParams();
  const { loading, error, data } = useQuery(GAMEDETAIL_QUERY, {
    variables: {
      gameId,
    },
  });

  if (loading) return <div>Chargement de la partie...</div>;
  if (error) return <div>Problème lors du chargement de la partie</div>;

  return (
    <>
      <p>
        <Link to="/admin">
          <Home size="16" />
        </Link>
        <ChevronRight size="12" />
        {data.game.title}
      </p>

      <h2>{data.game.title}</h2>
      <AdminQuestions gameId={gameId} />
    </>
  );
};

export default AdminGameDetail;
