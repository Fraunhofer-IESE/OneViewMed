import { FC } from "react";
import DrawerLayout, { DrawerItem } from "styles/DrawerLayout";
import LoginPage from "./LoginPage";
import useAccount from "hooks/account";

type RootPageProps = {
  drawerItems: Array<DrawerItem>;
};

const RootPage: FC<RootPageProps> = ({ drawerItems }) => {
  const { isLoggedIn } = useAccount();

  if (!isLoggedIn) {
    return <LoginPage />;
  }

  return <DrawerLayout drawerItems={drawerItems} />;
};

export default RootPage;
