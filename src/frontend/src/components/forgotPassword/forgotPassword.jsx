import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import hu_logo from '../../assets/hu_logo.png';
import styles from "../courses.module.css";



function isLoginIDValid(loginID) {
  let forbiddenChars = "'^+%&/()[]\\|<>;";
  for (let i = 0; i < forbiddenChars.length; i++) {
    if (loginID.includes(forbiddenChars.charAt(i))) return false;
  }

  return true;

};




function ForgotPassword() {

  let navigate = useNavigate();
  const [password, setPassword] = useState("");
  const [isPopupOpen, setIsPopupOpen] = useState(false);
  const [code, setCode] = useState("");
  const [loginId, setLoginId] = useState("");




  const openPopup = () => {
    sendCode();
    document.getElementById("popup").style.display = "block";
  }

  const closePopup = () => {
    document.getElementById("popup").style.display = "none";
  }


 const handleSendNewPassword = () => {
    handleNewPassword();
    navigate("/");
  };


  const handleNewPassword = () => {

    fetch("/api/auth/reset_password/request_new_password?loginId=" + loginId + "&code=" + code , { 
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: {

      }
    })
      .then((res) => {
        if (res.ok)
          {
          return res.json();}
        else {
          throw new Error("AN ISSUE CONNECTING TO BACKEND");
          }
      })
      .then((result) => {
        alert("Success");
        console.log(result);
        })
      .catch((err) => {
        console.log("Error:", err);
      });
  };


  const sendCode = () => {
    console.log(localStorage.getItem("loginID"));
    fetch("/api/auth/reset_password/request_code?loginId=" + loginId, { 
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: {

      }
    })
      .then((res) => {
        if (res.ok)
          {
          return res.json();}
        else {
          throw new Error("AN ISSUE CONNECTING TO BACKEND");
          }
      })
      .then((result) => {
        console.log(result);
        })
      .catch((err) => {
        console.log("Error:", err);
      });
  };



  return (
    <div className="full-screen-container">

      <div className="login-container">
        <div>
          <h2>Forgot Password</h2>

        </div>


        <form className="loginForm">
          <div className="fields-container">


          <input
              type="loginId"
              value={loginId}
              onChange={(e) => setLoginId(e.target.value)}
              placeholder="Username"
          />

          <div style={{marginTop: 15 + 'px'}}>
            <div className="reset-password-contaniner">
              <a href="#" className="reset-password" onClick={() => navigate('/')}>
                Back to the login page
              </a>
            </div>
          </div>

          </div>

          <button type="button" className="forgotPass-button" onClick={(event) => {event.preventDefault(); openPopup();}} >Password Change Request</button>

          <div id="popup" className="popup">
            <div className="popup-content">
              <span className="close-popup" onClick={closePopup}>&#10005;</span>
              <h2>Enter the code</h2>
              <label>Enter the verification code sent to your e-mail.</label>

                <form>
                  <input
                    type="text"
                    value={code}
                    onChange={(e) => setCode(e.target.value)}
                    placeholder="Enter code"
                    required
                  />
                  <button type="submit" className="action-button" onClick={handleSendNewPassword}>
                    Send New Password
                  </button>
                </form>
            </div>
          </div>

        </form>
      </div>
    </div>
  );
};

export default ForgotPassword;