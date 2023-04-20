import { ReactComponent as LogoQVGDC } from 'assets/img/logo.svg';
import React from 'react';

import styles from './Logo.module.scss';

function Logo() {
  return <LogoQVGDC className={`Logo ${styles.logo}`} />;
}

export default Logo;
