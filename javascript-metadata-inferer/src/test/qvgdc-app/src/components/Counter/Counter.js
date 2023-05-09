import React, { useState } from 'react';

import styles from './Counter.module.scss';

const Counter = (props) => {
  const [remaining, setRemaining] = useState(
    new Date(props.endAt).getTime() - Date.now() > 0 ? new Date(props.endAt).getTime() - Date.now() : 0
  );

  let interval;

  if (interval) clearTimeout(interval);

  if (remaining > 0) {
    interval = setTimeout(() => {
      const newTiming = new Date(props.endAt).getTime() - Date.now();

      if (newTiming > 0) {
        setRemaining(newTiming);
      } else {
        setRemaining(0);
        props.onComplete();
      }
    }, 1000);
  }

  const progress = ((remaining / 1000) * 100) / props.duration;
  const dashOffset = (progress * 157.079) / 100;

  return (
    <div className={`${styles.counter} ${remaining === 0 ? styles.finish : null}`}>
      <svg width="70" height="70" viewBox="0 0 70 70">
        <defs>
          <filter id="f2" x="0" y="0" width="300%" height="300%">
            <feOffset result="offOut" in="SourceGraphic" dx="-2" dy="0" />
            <feGaussianBlur result="blurOut" in="offOut" stdDeviation="4" />
            <feComponentTransfer>
              <feFuncA type="linear" slope="0" />
            </feComponentTransfer>
            <feBlend in="SourceGraphic" in2="blurOut" mode="normal" />
          </filter>
        </defs>
        <circle cx="35" cy="35" r="25" fill="none" stroke="#707070" opacity=".2" strokeWidth="2" />
        <circle
          cx="35"
          cy="35"
          r="25"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeDasharray="157.079"
          strokeDashoffset={dashOffset}
          transform="rotate(-90 35 35)"
          strokeLinecap="round"
          opacity=".32"
          filter="url(#f2)"
        />
        <circle
          cx="35"
          cy="35"
          r="25"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeDasharray="157.079"
          strokeDashoffset={dashOffset}
          transform="rotate(-90 35 35)"
          strokeLinecap="round"
        />
      </svg>
      <p className={styles.remaining}>{Math.floor(remaining / 1000)}s</p>
    </div>
  );
};

export default Counter;
