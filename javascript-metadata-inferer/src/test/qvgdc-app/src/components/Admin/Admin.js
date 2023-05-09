import React from 'react';
import AdminLayout from './AdminLayout/AdminLayout';
import AdminRouter from './AdminRouter';

function Admin() {
  return (
    <AdminLayout>
      <AdminRouter />
    </AdminLayout>
  );
}

export default Admin;
