import React from 'react';

import styles from './NetworkStatus.module.scss';

function NetworkStatus(props) {
  return <div className={props.online ? styles.online : styles.offline}></div>;
}

export default NetworkStatus;
