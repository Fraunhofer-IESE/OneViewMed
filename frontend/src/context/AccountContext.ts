import { createContext } from "react";

export type AccountContextType = {
  isLoggedIn: boolean;
  username: string;
  onLogIn: (username: string) => void;
  onLogOut: () => void;
};

const AccountContext = createContext<AccountContextType>({
  isLoggedIn: false,
  username: "",
  onLogIn: () => null,
  onLogOut: () => null,
});

export default AccountContext;
