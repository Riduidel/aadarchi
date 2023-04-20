import { CircularProgress } from '@mui/material';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import React from 'react';
import styles from './AdminAddDialog.module.scss';

const AdminAddDialog = (props) => {
  return (
    <Dialog open={props.open} onClose={() => props.close(false)} aria-labelledby="form-dialog-title">
      <DialogTitle id="form-dialog-title">{props.title}</DialogTitle>
      <DialogContent>
        <DialogContentText>{props.description}</DialogContentText>
        <br />
        {props.fields}
        {props.children}
        {props.loading ? (
          <div className={styles.loader}>
            <CircularProgress />
          </div>
        ) : (
          ''
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={() => props.close(false)} color="primary">
          Annuler
        </Button>
        <Button onClick={() => props.complete()} color="primary">
          {props.validateLabel || 'Ajouter'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AdminAddDialog;
