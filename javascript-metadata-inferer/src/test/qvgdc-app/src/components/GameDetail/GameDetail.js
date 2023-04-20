import React from 'react';
import { Gift } from 'react-feather';

import styles from './GameDetail.module.scss';

const GameDetail = (props) => {
  return (
    <>
      <h2 className={`h3 ${styles.title}`} style={{ color: 'inherit' }}>
        <Gift />
        {props.game.title}
      </h2>
      <p className={styles.description}>
        {props.game.players && props.game.players.length ? `${props.game.players.length} joueur(s)` : 'Aucun joueur'}
      </p>
    </>
  );
};

export default GameDetail;
