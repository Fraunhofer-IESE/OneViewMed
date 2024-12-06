import { createContext } from "react";

export const DEFAULT_BACKEND_URL = import.meta.env.VITE_BACKEND_URL;

export type ConfigurationContextType = {
  isLoaded: boolean;
  backendUrl: string;
};

const ConfigurationContext = createContext<ConfigurationContextType>({
  isLoaded: false,
  backendUrl: DEFAULT_BACKEND_URL,
});

export default ConfigurationContext;
