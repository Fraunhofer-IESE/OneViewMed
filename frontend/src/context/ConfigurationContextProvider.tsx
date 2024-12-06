import { FC, PropsWithChildren, useEffect, useState } from "react";
import ConfigurationContext, {
  ConfigurationContextType,
  DEFAULT_BACKEND_URL,
} from "./ConfigurationContext";

const APPLICATION_JSON = "application/json";
const CONTENT_TYPE_HEADER = "content-type";

const ConfigurationProvider: FC<PropsWithChildren<unknown>> = (props) => {
  const { children } = props;
  const [config, setConfig] = useState<ConfigurationContextType>({
    isLoaded: false,
    backendUrl: DEFAULT_BACKEND_URL,
  });

  useEffect(() => {
    const loadContext = async () => {
      try {
        const response = await fetch(import.meta.env.BASE_URL + "config.json");
        if (!response.ok) {
          throw new Error("Configuration could not be loaded!");
        }
        const contentType = response.headers.get(CONTENT_TYPE_HEADER);
        if (!contentType || !contentType.includes(APPLICATION_JSON)) {
          throw new Error("Configuration could not be loaded!");
        }
        const configObj = await response.json();
        setConfig({
          isLoaded: true,
          backendUrl: configObj["backendUrl"] ?? DEFAULT_BACKEND_URL,
        });
      } catch (e) {
        setConfig({
          isLoaded: true,
          backendUrl: DEFAULT_BACKEND_URL,
        });
      }
    };
    loadContext();
  }, []);

  return (
    <ConfigurationContext.Provider value={config}>
      {children}
    </ConfigurationContext.Provider>
  );
};

export default ConfigurationProvider;
