import React from "react";
import "../../components/deleteuser/deleteuser.css";
import {logout} from "../../components/logout";
import { useNavigate } from "react-router-dom";
export default DeleteSelfStudent;
function DeleteSelfStudent() {



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
                    else if (res.status === 400)
                        throw new Error("USER HAS ATTRIBUTED TO SYSTEM")
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
                if(err.toString().localeCompare("USER HAS ATTRIBUTED TO SYSTEM")){
                    navigate('/mainPageStudent');
                    alert("Cannot delete a user being used in the system!")
                }else
                    console.log("Error:", err);
            });
    }
    return(<div>
            <div className="delQuestion">
                <h1>Do you want to delete account?</h1>
                <h2>(Note: This action deletes the account permanently from the system)</h2>
            </div>
            <div className="delButton">
                <button onClick={() => deleteRequest()}>YES</button>
                <button onClick={() => navigate('/mainPageStudent')}>NO</button>
            </div>

        </div>

    );

}