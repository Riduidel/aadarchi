import React from 'react';

import styles from './Footer.module.scss';

function Footer(props) {
  return (
    <footer className={styles.footer}>
      <div className={`${styles.footerWrapper} u-wrapper`}>{props.children}</div>
    </footer>
  );
}

export default Footer;
