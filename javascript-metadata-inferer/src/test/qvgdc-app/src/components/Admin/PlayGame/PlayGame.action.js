import { gql } from '@apollo/client';

export const GAMEDETAIL_QUERY = gql`
  query game($gameId: ID!) {
    game(gameId: $gameId) {
      id
      title
      state
      finish
      currentQuestion {
        id
        title
        launched
        duration
        order
      }
      players {
        id
        name
      }
      questions {
        id
        title
        launched
        duration
        order
        choices {
          id
          title
        }
        goodChoice {
          id
          title
        }
      }
    }
  }
`;

export const GAMEDETAIL_SUBSCRIPTION = gql`
  subscription updatedGame($gameId: ID!) {
    updatedGame(gameId: $gameId) {
      id
      title
      state
      finish
      currentQuestion {
        id
        title
        launched
        duration
        order
      }
      players {
        id
        name
      }
      questions {
        id
        title
        launched
        duration
        order
        choices {
          id
          title
        }
        goodChoice {
          id
          title
        }
      }
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
