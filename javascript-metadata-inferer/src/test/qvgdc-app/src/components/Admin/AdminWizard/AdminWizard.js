import React from 'react';
import { Edit, PlayCircle } from 'react-feather';
import styles from './AdminWizard.module.scss';

const AdminWizard = () => {
  return (
    <div className={styles.content}>
      <h2>Bienvenue</h2>
      <p>
        Dans l'administration de <strong>Qui veut gagner des canards</strong>. Vous pouvez ajouter des parties puis gérer leurs contenus.
        Pour cela il faut :
      </p>
      <ol>
        <li>Ajouter une partie ou importer une partie via un <strong>JSON</strong>;</li>
        <li>
          L'éditer en cliquant sur le{' '}
          <strong>
            crayon <Edit size="14" />
          </strong>{' '}
          pour y ajouter des questions;
        </li>
        <li>
          Pour chaque question, après avoir cliqué sur le{' '}
          <strong>
            crayon <Edit size="14" />
          </strong>
          , y ajouter des réponses puis sélectionner la <strong>bonne réponse</strong>;
        </li>
      </ol>
      <p>
        Pour rendre une partie disponible et visible aux joueurs, cliquez sur la pastille rouge de la colonne <strong>Statut</strong>.
      </p>
      <p>
        Pour jouer une partie en tant que <strong>Game Master</strong>, cliquez sur le bouton{' '}
        <strong>
          <PlayCircle size="14" /> Play
        </strong>
      </p>
    </div>
  );
};

export default AdminWizard;
