import React from 'react';

import styles from './Header.module.scss';

function Header(props) {
  return (
    <header className={styles.header}>
      <div className={`u-wrapper ${styles.wrapper}`}>{props.children}</div>
    </header>
  );
}

export default Header;
