export const initialAuthState = {
  isLoggedIn: localStorage.getItem('token') ? true : false,
  user: JSON.parse(localStorage.getItem('user')),
  token: localStorage.getItem('token'),
};

export const initialPlayerState = {
  name: localStorage.getItem('player') ? JSON.parse(localStorage.getItem('player'))['name'] : null,
  id: localStorage.getItem('player') ? JSON.parse(localStorage.getItem('player'))['id'] : null,
  score: localStorage.getItem('player') ? JSON.parse(localStorage.getItem('player'))['score'] : null,
  answers: localStorage.getItem('player') ? JSON.parse(localStorage.getItem('player'))['answers'] : null,
};

export const authReducer = (state, action) => {
  switch (action.type) {
    case 'LOGIN':
      return {
        isLoggedIn: true,
        user: action.payload.user,
        token: action.payload.token,
      };
    default:
      return state;
  }
};

export const playerReducer = (state, action) => {
  switch (action.type) {
    case 'JOIN':
      return {
        name: action.payload.name,
        id: action.payload.id,
        score: null,
        answers: [],
      };
    case 'UPDATE':
      return {
        ...state,
        ...action.payload,
      };
    default:
      return state;
  }
};
