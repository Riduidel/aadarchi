import React from 'react';

import styles from './DefaultLayout.module.scss';

function DefaultLayout(props) {
  return (
    <div className={styles.layout}>
      <div className={styles.main}>
        <div className="u-wrapper">{props.children}</div>
      </div>
    </div>
  );
}

export default DefaultLayout;
