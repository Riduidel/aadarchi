import { gql } from '@apollo/client';

export const CHOICE_QUERY = gql`
  query choices($questionId: ID!) {
    choices(questionId: $questionId) {
      id
      title
    }
  }
`;

export const ADD_CHOICE = gql`
  mutation newChoice($title: String!, $questionId: ID!) {
    newChoice(title: $title, questionId: $questionId) {
      id
      title
    }
  }
`;

export const DELETE_CHOICE = gql`
  mutation deleteChoice($choiceId: ID!) {
    deleteChoice(choiceId: $choiceId) {
      id
    }
  }
`;
