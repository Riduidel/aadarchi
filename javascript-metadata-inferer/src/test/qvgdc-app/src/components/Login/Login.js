import { gql } from '@apollo/client';
import { Mutation } from '@apollo/client/react/components';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import DefaultLayout from 'components/DefaultLayout/DefaultLayout';
import Logo from 'components/Logo/Logo';
import React, { useContext, useState } from 'react';
import { Redirect } from 'react-router-dom';
import authContext from 'store';
import styles from './Login.module.scss';

const LOGIN_MUTATION = gql`
  mutation login($email: String!, $password: String!) {
    login(email: $email, password: $password) {
      token
      user {
        id
        email
      }
    }
  }
`;

function Login(props) {
  const [login, setLogin] = useState('');
  const [password, setPassword] = useState('');
  const [state, dispatch] = useContext(authContext);

  const storeTokenAndUserAndRefresh = (token, user) => {
    dispatch({
      type: 'LOGIN',
      payload: {
        token,
        user,
      },
    });

    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
  };

  const handleComplete = (e) => {
    if (e.login) {
      storeTokenAndUserAndRefresh(e.login.token, e.login.user);
    }
  };

  return (
    <>
      {state.isLoggedIn ? (
        <Redirect to="/admin" />
      ) : (
        <Mutation mutation={LOGIN_MUTATION} variables={{ email: login, password }} onCompleted={handleComplete}>
          {(loginMutation) => (
            <DefaultLayout>
              <div className={styles.card}>
                <Logo />
                <h1 className={`h3 text-center ${styles.title}`}>Game master</h1>
                <form className={styles.form} noValidate autoComplete="off">
                  <TextField
                    onChange={(e) => setLogin(e.target.value)}
                    type="email"
                    required
                    label="Email"
                    margin="normal"
                    variant="outlined"
                  />
                  <TextField
                    onChange={(e) => setPassword(e.target.value)}
                    type="password"
                    required
                    label="Mot de passe"
                    margin="normal"
                    variant="outlined"
                  />
                  <br />
                  <br />
                  <Button variant="contained" color="primary" onClick={loginMutation}>
                    Se connecter
                  </Button>
                </form>
              </div>
            </DefaultLayout>
          )}
        </Mutation>
      )}
    </>
  );
}

export default Login;
