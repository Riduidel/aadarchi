import Footer from 'components/Footer/Footer';
import Header from 'components/Header/Header';
import Logo from 'components/Logo/Logo';
import NetworkStatus from 'components/NetworkStatus/NetworkStatus';
import Score from 'components/Score/Score';
import React from 'react';
import { User } from 'react-feather';

import styles from './GameLayout.module.scss';

function GameLayout(props) {
  return (
    <div className={styles.question}>
      <Header>
        <div className={styles.logo}>
          <Logo />
        </div>
        <div className={styles.title}>
          <h1>{props.game.title}</h1>
          <p>{props.game.players.length} joueurs connectés</p>
        </div>
        <NetworkStatus online={true} />
      </Header>
      <div className={styles.main}>
        <div className="u-wrapper u-wrapper--no-padding">{props.children}</div>
      </div>
      <Footer>
        <p className={styles.player}>
          <User />
          {props.player.name}
        </p>
        <Score score={props.player.score} />
      </Footer>
    </div>
  );
}

export default GameLayout;
