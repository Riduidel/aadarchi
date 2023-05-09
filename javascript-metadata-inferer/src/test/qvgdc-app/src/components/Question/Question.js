import { useMutation } from '@apollo/client';
import { Button } from '@mui/material';
import Choices from 'components/Choices/Choices';
import Counter from 'components/Counter/Counter';
import React, { useContext, useState } from 'react';
import { playerContext } from 'store';
import { NEW_ANSWER } from './Question.actions';
import styles from './Question.module.scss';

const Question = (props) => {
  const [addNewAnswer] = useMutation(NEW_ANSWER);
  const [player, dispatch] = useContext(playerContext);
  const endingQuestion =
    props.question && props.question.launched ? new Date(props.question.launched).getTime() + props.question.duration * 1000 : null;
  const [isQuestionFinished, setIsQuestionFinished] = useState(endingQuestion < Date.now());
  const [answerChoosen, setAnswer] = useState(null);
  const [hasChoose, setHasChoose] = useState(isQuestionFinished);

  const nbAnswers = props.question.choices.reduce((acc, val) => (acc += val.answers.length), 0);
  let content;

  // if (!hasChoose && player.answers.some((answer) => answer.choice.id === choice.id) {
  if (!hasChoose) {
    player.answers.forEach((answer) => {
      let foundChoice = props.question.choices.find((c) => c.id === answer.choice.id);

      if (foundChoice) {
        setHasChoose(true);
        setAnswer(answer.choice);
      }
    });
  }

  const endCounter = () => {
    if (!isQuestionFinished) {
      if (!hasChoose) {
        dispatchOnClick();
      }

      props.onCompleteCounter();

      setIsQuestionFinished(true);
      setAnswer(null);
    }
  };

  const dispatchOnClick = () => {
    setHasChoose(true);
    const recupPlayer = JSON.parse(localStorage.getItem('player'));

    recupPlayer['answers'] = [
      ...player.answers,
      {
        id: 'temp',
        choice: { ...answerChoosen },
      },
    ];

    localStorage.setItem('player', JSON.stringify(recupPlayer));

    dispatch({
      type: 'UPDATE',
      payload: {
        answers: recupPlayer['answers'],
      },
    });

    addNewAnswer({
      variables: {
        choiceId: answerChoosen.id,
        questionId: props.question.id,
        playerId: player.id,
      },
    });
  };

  if (isQuestionFinished) {
    content = <p className="text-center">Stats</p>;
  } else {
    if (!hasChoose) {
      content = (
        <div className="text-center">
          <Button variant="contained" color="primary" onClick={dispatchOnClick}>
            Valider ma réponse
          </Button>
          <p>{nbAnswers} joueurs ont déjà répondus</p>
        </div>
      );
    } else {
      content = <p className="text-center">Réponse envoyée</p>;
    }
  }

  return (
    <>
      <div className={styles.head}>
        <Counter duration={props.question.duration} endAt={endingQuestion} onComplete={() => endCounter()} />
        <div className={styles.infos}>
          <p className={styles.suptitle}>Question n°{props.question.order}</p>
          <h1 className={styles.title}>{props.question.title}</h1>
        </div>
      </div>

      <div className={styles.choices}>
        <Choices
          canChoose={!hasChoose}
          setChoice={setAnswer}
          answerChoosen={answerChoosen}
          choices={props.question.choices}
          showResults={isQuestionFinished}
          goodChoiceId={props.question.goodChoice.id}
        />
        <div className={styles.actions}>{content}</div>
      </div>
    </>
  );
};

export default Question;
