import React, { useContext } from 'react';
import { playerContext } from 'store';

import styles from './Choices.module.scss';

const Choices = (props) => {
  const [player] = useContext(playerContext);

  const handleClick = (choice) => {
    if (props.canChoose) {
      props.setChoice(choice);
    }
  };

  return (
    <>
      {props.choices.map((choice, index) => {
        const isActive = props.answerChoosen
          ? props.answerChoosen.id === choice.id
          : player.answers.some((answer) => answer.choice.id === choice.id);

        return (
          <div
            onClick={() => handleClick(choice)}
            className={`${styles.choice} ${isActive && props.showResults ? styles.errorchoice : isActive ? styles.pendingchoice : ''} ${
              choice.id === props.goodChoiceId && props.showResults ? styles.goodchoice : ''
            }`}
            key={choice.id}
          >
            <strong className={styles.questionIndex}>{(index + 10).toString(36)}.</strong>
            {choice.title}
          </div>
        );
      })}
    </>
  );
};

export default Choices;
