import { gql } from '@apollo/client';

export const USER_QUERY = gql`
  {
    me {
      games {
        id
        title
        players {
          id
        }
        open
        finish
      }
    }
  }
`;

export const ADD_GAME = gql`
  mutation newGame($title: String!) {
    newGame(title: $title) {
      id
      title
    }
  }
`;

export const ADD_JSON_GAME = gql`
  mutation newJsonGame($json: String!) {
    newJsonGame(json: $json) {
      id
      title
    }
  }
`;

export const DELETE_GAME = gql`
  mutation deleteGame($gameId: ID!) {
    deleteGame(gameId: $gameId) {
      id
    }
  }
`;

export const UPDATE_GAME = gql`
  mutation updateGame($gameId: ID!, $data: GameInput!) {
    updateGame(gameId: $gameId, data: $data) {
      id
    }
  }
`;
