import AdminUser from 'components/Admin/AdminUser/AdminUser';
import Header from 'components/Header/Header';
import Logo from 'components/Logo/Logo';
import React, { useContext } from 'react';
import authContext from 'store';
import styles from './AdminLayout.module.scss';

const AdminLayout = (props) => {
  const [{ user }] = useContext(authContext);

  return (
    <>
      <div className={styles.header}>
        <Header>
          <div className={styles.logo}>
            <Logo />
          </div>
          <div className={styles.title}>
            <h1>Game Master</h1>
          </div>
          <div className={styles.user}>
            <AdminUser user={user} />
          </div>
        </Header>
      </div>
      <div className={styles.page}>
        <div className="u-wrapper">{props.children}</div>
      </div>
    </>
  );
};

export default AdminLayout;
