import { gql } from '@apollo/client';

export const QUESTION_QUERY = gql`
  query questions($gameId: ID!) {
    questions(gameId: $gameId) {
      id
      duration
      title
      order
    }
  }
`;

export const ADD_QUESTION = gql`
  mutation newQuestion($title: String!, $gameId: ID!, $duration: Int!) {
    newQuestion(title: $title, gameId: $gameId, duration: $duration) {
      id
      title
    }
  }
`;

export const DELETE_QUESTION = gql`
  mutation deleteQuestion($questionId: ID!) {
    deleteQuestion(questionId: $questionId) {
      id
    }
  }
`;
