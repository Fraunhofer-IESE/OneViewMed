import {
  Backdrop,
  Box,
  IconButton,
  Modal,
  Typography,
  useTheme,
} from "@mui/material";
import { css, styled } from "@mui/system";
import { CloseIcon } from "assets";
import { FC, PropsWithChildren } from "react";

const BaseModal: FC<
  {
    open: boolean;
    onClose: () => void;
    title: string;
  } & PropsWithChildren
> = ({ open, title, children, onClose }) => {
  const theme = useTheme();

  return (
    <Modal
      aria-labelledby="unstyled-modal-title"
      aria-describedby="unstyled-modal-description"
      sx={{
        position: "fixed",
        zIndex: 1300,
        inset: 0,
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
      }}
      open={open}
      onClose={onClose}
      slots={{ backdrop: Backdrop }}
    >
      <ModalContent sx={{ width: "60rem", height: "42.5rem" }}>
        <Box
          display="flex"
          justifyContent="space-between"
          padding={theme.spacing(3)}
          borderBottom={`1px solid ${theme.palette.divider}`}
        >
          <Typography
            fontSize="1rem"
            textAlign="center"
            textTransform="capitalize"
          >
            {title}
          </Typography>
          <IconButton
            aria-label="close-modal"
            sx={{ padding: "unset" }}
            type="button"
            onClick={onClose}
          >
            <CloseIcon
              fill={theme.palette.secondary.main}
              width={20}
              height={20}
            />
          </IconButton>
        </Box>
        <Box padding={theme.spacing(3)} height="100%">
          {children}
        </Box>
      </ModalContent>
    </Modal>
  );
};

const ModalContent = styled("div")(
  ({ theme }) => css`
    text-align: start;
    position: relative;
    display: flex;
    flex-direction: column;
    gap: 8px;
    overflow: hidden;
    background-color: ${theme.palette.background.paper};
    border-radius: 8px;
    border: 1px solid ${theme.palette.divider};
    box-shadow: 0 4px 12px rgb(0 0 0 / 0.2);
  `,
);

export default BaseModal;
