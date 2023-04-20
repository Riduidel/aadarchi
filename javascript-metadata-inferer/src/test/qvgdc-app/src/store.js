import React from "react";

const authContext = React.createContext({});
export const AuthProvider = authContext.Provider;
export const AuthConsumer = authContext.Consumer;

export default authContext;

export const playerContext = React.createContext({});
export const PlayerProvider = playerContext.Provider;
export const PlayerConsumer = playerContext.Consumer;
