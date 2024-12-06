import react from "@vitejs/plugin-react";
import path from "path";
import { defineConfig } from "vite";
import svgr from "vite-plugin-svgr";

// Define your directories
const directories = [
  "assets",
  "components",
  "hooks",
  "lang",
  "pages",
  "styles",
  "utils"
];

// Define the aliases
const aliases = directories.map((directory) => ({
  find: directory,
  replacement: path.resolve(__dirname, `src/${directory}`),
}));

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react(), svgr()],
  resolve: {
    preserveSymlinks: true,
    alias: [...aliases],
  },
  base: "/dashboard/",
  server: {
    port: 3000,
    open: true,
  },
  preview: {
    port: 3000,
    open: true,
  },
});
