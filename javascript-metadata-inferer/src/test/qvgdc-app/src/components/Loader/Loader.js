import { ReactComponent as Canard } from 'assets/img/canard.svg';
import { ReactComponent as Vague } from 'assets/img/vague.svg';
import React from 'react';

import styles from './Loader.module.scss';

function Loader() {
  return (
    <div className={styles.duckloader}>
      <Vague className={`${styles.vague} ${styles.vagueBack}`} />
      <Canard className={styles.canard} />
      <Vague className={`${styles.vague} ${styles.vagueFront}`} />
    </div>
  );
}

export default Loader;
