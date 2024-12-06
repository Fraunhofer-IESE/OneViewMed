import useAccount from "hooks/account";
import { FC, useCallback, useEffect } from "react";
import { useNavigate } from "react-router-dom";

const LogoutPage: FC = () => {
  const { onLogOut } = useAccount();
  const navigate = useNavigate();

  const goToHomePage = useCallback(() => {
    navigate("/");
  }, [navigate]);

  useEffect(() => {
    onLogOut();
    goToHomePage();
  }, [goToHomePage, onLogOut]);

  return null;
};

export default LogoutPage;
