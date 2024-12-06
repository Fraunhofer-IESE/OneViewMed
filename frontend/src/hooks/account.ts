import { useContext } from "react";
import AccountContext from "../context/AccountContext";

const useAccount = () => {
  return useContext(AccountContext);
};

export default useAccount;
