import { Table, TableBody, TableCell, TableHead, TableRow } from '@mui/material';
import React from 'react';
import { Clock } from 'react-feather';

const LeaderBoard = (props) => {
  const sortedArrays = [...props.players].sort((a, b) => {
    if (a.score === b.score) return a.responseTime - b.responseTime;
    return b.score - a.score;
  });

  return (
    <Table>
      <TableHead>
        <TableRow>
          <TableCell>Joueur</TableCell>
          <TableCell>Score</TableCell>
          <TableCell align="right">
            <Clock size="14" />
          </TableCell>
        </TableRow>
      </TableHead>

      <TableBody>
        {sortedArrays.map((player) => (
          <TableRow key={player.id}>
            <TableCell>{player.name}</TableCell>
            <TableCell>{player.score}</TableCell>
            <TableCell align="right">{player.responseTime / 1000}s</TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
};

export default LeaderBoard;
