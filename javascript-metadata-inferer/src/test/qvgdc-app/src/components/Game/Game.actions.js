import { gql } from '@apollo/client';

export const GAMEDETAIL_QUERY = gql`
  query getGame($gameId: ID!) {
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
        goodChoice {
          id
          title
        }
        choices {
          id
          title
          answers {
            id
          }
        }
      }
      players {
        id
        name
        score
        responseTime
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
        goodChoice {
          id
          title
        }
        choices {
          id
          title
          answers {
            id
          }
        }
      }
      players {
        id
        name
        score
        responseTime
      }
    }
  }
`;

export const PLAYER_QUERY = gql`
  query getPlayer($playerId: ID!) {
    player(playerId: $playerId) {
      id
      name
      score
      answers {
        id
        choice {
          id
        }
      }
    }
  }
`;

export const PLAYER_SUBSCRIPTION = gql`
  subscription updatedPlayer($playerId: ID!) {
    updatedPlayer(playerId: $playerId) {
      id
      name
      score
      answers {
        id
        choice {
          id
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
