import { useMutation } from '@apollo/client';
import { Menu, MenuItem, Snackbar, TextField } from '@mui/material';
import React, { useState } from 'react';
import AdminAddDialog from '../AdminAddDialog/AdminAddDialog';
import { ADD_USER } from './AdminUser.actions';
import styles from './AdminUser.module.scss';

const AdminUser = (props) => {
  const [anchorEl, setAnchorEl] = useState(null);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [dialogOpen, setDialogOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [snackbar, setSnackbar] = useState(false);

  const initial = props.user.email.match(/(\w){1}[a-z-_]*\.?(\w){0,1}[a-z]*@/im).reduce((acc, val, i) => {
    if (i === 0 || !val) {
      return acc;
    }

    acc += val.toUpperCase();
    return acc;
  }, '');

  const handleClick = (ev) => {
    setAnchorEl(ev.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const addUser = async () => {
    setLoading(true);
    await addUserMutation({
      variables: { email, password },
    });

    setLoading(false);
    setSnackbar(true);
    setDialogOpen(false);
    setEmail('');
    setPassword('');
  };

  const clickOnMenuAddItem = () => {
    setDialogOpen(true);
    handleClose();
  };

  const [addUserMutation] = useMutation(ADD_USER);

  const addUserDialog = {
    title: 'Ajouter un utilisateur',
    description: 'Pour ajouter un utilisateur, veuillez renseigner son email et un mot de passe.',
    open: dialogOpen,
    closeDialog: setDialogOpen,
    completeDialog: addUser,
    fields: [
      <TextField
        key="email"
        onChange={(e) => setEmail(e.target.value)}
        autoFocus
        margin="dense"
        id="email"
        label="Email de l'utilisateur"
        fullWidth
        variant="outlined"
      />,
      <TextField
        key="password"
        onChange={(e) => setPassword(e.target.value)}
        margin="dense"
        type="password"
        id="password"
        label="Mot de passe"
        fullWidth
        variant="outlined"
      />,
    ],
  };

  return (
    <div className={styles.container}>
      <button className={styles.button} type="button" onClick={handleClick}>
        <span className={styles.avatar}>{initial}</span>
      </button>

      <Snackbar
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
        open={snackbar}
        message="Utilisateur ajouté"
        key="addusersnackbar"
      />
      <AdminAddDialog
        title={addUserDialog.title}
        description={addUserDialog.description}
        open={addUserDialog.open}
        close={addUserDialog.closeDialog}
        complete={addUserDialog.completeDialog}
        fields={addUserDialog.fields}
        loading={loading}
      />
      <Menu
        anchorEl={anchorEl}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'right',
        }}
        transformOrigin={{
          vertical: 'top',
          horizontal: 'right',
        }}
        keepMounted
        open={Boolean(anchorEl)}
        onClose={handleClose}
      >
        <MenuItem disabled>{props.user.email}</MenuItem>
        <MenuItem onClick={clickOnMenuAddItem}>Ajouter un utilisateur</MenuItem>
      </Menu>
    </div>
  );
};

export default AdminUser;
