import React from 'react';

import styles from './Score.module.scss';

function Score(props) {
  return <p className={styles.score}>Score : {props.score ? props.score : 0}</p>;
}

export default Score;
