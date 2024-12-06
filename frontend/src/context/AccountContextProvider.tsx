import { FC, PropsWithChildren, useCallback, useState } from "react";
import AccountContext from "./AccountContext";

const AccountProvider: FC<PropsWithChildren<unknown>> = (props) => {
  const { children } = props;
  const [{ isLoggedIn, username }, setAccount] = useState({
    isLoggedIn: false,
    username: "",
  });

  const onLogIn = useCallback(
    (username: string) => setAccount({ isLoggedIn: true, username }),
    [],
  );

  const onLogOut = useCallback(
    () => setAccount({ isLoggedIn: false, username: "" }),
    [],
  );

  return (
    <AccountContext.Provider
      value={{ isLoggedIn, username, onLogIn, onLogOut }}
    >
      {children}
    </AccountContext.Provider>
  );
};

export default AccountProvider;
