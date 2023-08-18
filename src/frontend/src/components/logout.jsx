export const logout = () => {
    localStorage.removeItem("loginID");
    localStorage.removeItem("authToken");
  };