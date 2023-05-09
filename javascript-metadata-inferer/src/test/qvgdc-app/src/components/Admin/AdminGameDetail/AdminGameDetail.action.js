import { gql } from '@apollo/client';

export const GAMEDETAIL_QUERY = gql`
  query game($gameId: ID!) {
    game(gameId: $gameId) {
      id
      title
      questions {
        id
      }
    }
  }
`;
