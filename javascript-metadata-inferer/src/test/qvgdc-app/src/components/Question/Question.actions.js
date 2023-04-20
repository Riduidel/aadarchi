import { gql } from '@apollo/client';

export const NEW_ANSWER = gql`
  mutation newAnswer($playerId: ID!, $choiceId: ID!, $questionId: ID!) {
    newAnswer(playerId: $playerId, choiceId: $choiceId, questionId: $questionId) {
      id
    }
  }
`;
