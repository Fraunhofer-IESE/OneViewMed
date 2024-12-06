import { FC, PropsWithChildren } from "react";
import messagesDe from "./de.json";
import { IntlProvider } from "react-intl";

const LOCALE = "de";

const LangWrapper: FC<PropsWithChildren<unknown>> = (props) => {
  const { children } = props;
  return (
    <IntlProvider locale={LOCALE} messages={messagesDe}>
      {children}
    </IntlProvider>
  );
};

export default LangWrapper;
