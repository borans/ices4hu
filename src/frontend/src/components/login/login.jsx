import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./login.css";
import hu_logo from '../../assets/hu_logo.png';
import styles from "../courses.module.css";



function isLoginIDValid(loginID) {
  let forbiddenChars = "'^+%&/()[]\\|<>;";
  for (let i = 0; i < forbiddenChars.length; i++) {
    if (loginID.includes(forbiddenChars.charAt(i))) return false;
  }

  return true;

};

function Login() {

  let navigate = useNavigate();
  const [logID, setlogID] = useState("");
  const [password, setPassword] = useState("");


  function handleLogInButton() {

    setlogID("");
    setPassword("");

    if (logID.length == 0)
    {
      alert("Please enter your login ID!");
      return;
    }

    if (password.length == 0)
    {
      alert("Please enter your password!")
      return;
    }
    
    if (!isLoginIDValid(logID))
    {
      alert("Invalid login id!");
      return;
    }
    
    sendRequest();
    
  }

  const sendRequest = () => {
    fetch("/api/auth/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        loginID: logID,
        password: password
      }),
    })
      .then((res) => {
        if (res.ok)
          {
          return res.json();}
        else {
          if (res.status === 401)
          {
            alert("Incorrect login ID/password!")
            throw new Error("USER NOT FOUND");
            
          }
          else
            throw new Error("AN ISSUE CONNECTING TO BACKEND");
        }
      })
      .then((result) => {
        localStorage.setItem("authToken", result.token)
        localStorage.setItem("userID", result.userId);
        localStorage.setItem("loginID", logID);

        if(result.userType == 0){
          navigate("/mainPageAdmin");
        }else if(result.userType == 1){
          navigate("/mainPageStudent");
        }else if(result.userType == 2){
          navigate("/mainPageManager");
        }else{
          navigate("/mainPageInstructor");
        }
        
        
      })
      .catch((err) => {
        console.log("Error:", err);
      });
  }

//check if local storage is cleared existed. Add user id to remove 
//  useEffect(() => {
//    localStorage.removeItem("authToken");
//    localStorage.removeItem("logID");
//  }, [])

  const handleSignUpButton = () => {
    navigate('/signup');
  }


  return (
    <div className="full-screen-container">

      <div className="login-container">

        <div className="hu_logo">
          <img src={hu_logo} alt="" />
        </div>
        <div>
          <h1>ICES4HU LOGIN</h1>

        </div>


        <form className="loginForm">
          <div className="fields-container">


              <input
                  type="email"
                  value={logID}
                  onChange={(e) => setlogID(e.target.value)}
                  placeholder="Login id"
              />




              <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Password"
              />




          </div>

          <div className="reset-password-contaniner">
            <a href="#" className="reset-password" onClick={() => navigate('/forgotPassword')}>
              Reset Password
            </a>
          </div>


          <button type="button" className="login-button" onClick={(event) => {event.preventDefault(); handleLogInButton();}}>Log In</button>
        </form>
      </div>

      <button type="button" className="sign-button" onClick={(event) => {event.preventDefault(); handleSignUpButton();}}>Sign Up</button>
    </div>
  );
};

export default Login;