import { useContext } from "react";
import ConfigurationContext from "../context/ConfigurationContext";

const useConfiguration = () => {
  return useContext(ConfigurationContext);
};

export default useConfiguration;
