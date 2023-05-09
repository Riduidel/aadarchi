import { gql } from '@apollo/client';

export const QUESTIONDETAIL_QUERY = gql`
  query question($questionId: ID!) {
    question(questionId: $questionId) {
      goodChoice {
        id
      }
      id
      title
      game {
        title
      }
    }
  }
`;

export const UPDATE_QUESTION = gql`
  mutation updateQuestion($questionId: ID!, $data: QuestionInput!) {
    updateQuestion(questionId: $questionId, data: $data) {
      id
    }
  }
`;
