import React from "react";
import "./deleteuser.css";
import {logout} from "../logout";
import { useNavigate } from "react-router-dom";
export default DeleteUser;
function DeleteUser() {



  const navigate = useNavigate();

    const deleteRequest = () => {
        fetch("/api/users/" + localStorage.getItem("loginID"), {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            "Authorization": localStorage.getItem("authToken")
        },
          body: "",
        })
          .then((res) => {
            if (res.ok)
              {
              return res.json();}
            else {
              if (res.status === 401)
                throw new Error("USER NOT FOUND");
              else
                throw new Error("AN ISSUE CONNECTING TO BACKEND");
            }
          })
          .then((result) => {
            logout();
            localStorage.removeItem("userID");
            navigate('/');  // restart
              alert("Account has been deleted successfully!");


          })
          .catch((err) => {
            console.log("Error:", err);
          });
      }
    return(<div>
        <div className="delQuestion">
        <h1>Do you want to delete account?</h1>
    <h2>(Note: This action deletes the account permanently from the system)</h2>
        </div>
        <div className="delButton">
        <button onClick={(event) => {event.preventDefault(); deleteRequest();}}>YES</button>
        <button onClick={(event) => {event.preventDefault(); navigate('/mainPage');}}>NO</button>
        </div>
        
    </div>
    
    );
    
}