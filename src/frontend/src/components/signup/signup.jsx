import React, { useState, useEffect } from "react";
import "./signup.css"; // Import the CSS file for styling
import { useNavigate } from "react-router-dom";
import hu_logo from '../../assets/hu_logo.png';

const AFTER_AT = "hacettepe.edu.tr";

function isNameValid(name) {

    let validChars = "abcçdefgğhıijklmnoöprsştuüvyzwxq ";
    validChars += "ABCÇDEFGĞHIİJKLMNOÖPRSŞTUÜVYZWXQ";

    for (let i = 0; i < name.length; i++) {
        if (!validChars.includes(name.charAt(i))) 
            return false;
    }

    return true;
};

function isEmailValid(email) {
    const sp = email.split("@");

    let numberOfAts = sp.length - 1;
    if (numberOfAts != 1) {
        return false;
    }

    let afterAt = sp[1];
    if (afterAt != AFTER_AT) {
        return false;
    }
    
    return true;
}

function SignUp () {
    let navigate = useNavigate();
    const [name, setName] = useState("");
    const [surname, setSurname] = useState("");
    const [email, setEmail] = useState("");
    const [isGraduate, setIsGraduate] = useState(false);
    const [isDM, setIsDM] = useState(false);  /*is department manager?*/
    const [selectedOption, setSelectedOption] = useState(0);
    const [departments, setDepartments] = useState([]);

    useEffect(() => {
        getDepartments()
    }, [])

    const getDepartments = () => {

      fetch("/api/auth/departments", {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      })
        .then((res) => {
          if (res.ok)
            return res.json();
          else
            throw new Error("AN ISSUE CONNECTING TO BACKEND");
        }
        )
        .then(result => {
          setDepartments(result);
        })
        .catch((err) => {
          console.log("Error:", err);
        });

    } 

    function handleSubmitButton (){
        console.log(JSON.stringify({
            name: name,
            surname: surname,
            email: email,
            departmentId: selectedOption,
            degree: isGraduate ? 1 : 0,
            userType: isDM ? 2 : 1
          }));

        if (email.length == 0) {
            alert("Please enter your email address")
            return;
        }

        if (!isEmailValid(email)) {
            alert("Invalid email address");
            return;
        }

        if (name.trim().length == 0) {
            alert("Please enter your name");
            return;
        }

        if (surname.trim().length == 0) {
            alert("Please enter your surname");
            return;
        }

        if (!isNameValid(name) || !isNameValid(surname)) {
            alert("The name/surname entered is invalid");
            return;
        }

        if (selectedOption == 0) {
            alert("Please select a department");
            return;
        }

        sendSignRequest();
        setEmail("");
        setName("");
        setSurname("");
        setIsDM(false);
        setSelectedOption(0);
    };

    const sendSignRequest = () => {
        fetch("/api/auth/signup", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            name: name,
            surname: surname,
            email: email,
            departmentId: selectedOption,
            degree: isGraduate ? 1 : 0,
            userType: isDM ? 2 : 1
          }),
        })
          .then((res) => {
            if (res.ok) {
                return {json: res.json(),
                    success: true};
            }

            else {
                return {json: res.json(),
                    success: false};
            }

          })
          .then((result) => {

            if (result.success) {
                alert("Operation successful!");
                navigate('/');
            } else {
                result.json.then((json) => {
                    alert(json.message);
                })
            }
          })
          .catch((err) => {
            console.log("Error:", err);
          });
      }

    const handleDepartmentChange = (event) => {
        setSelectedOption(event.target.value);
    };
    const handleIsGraduateChange = (statuss) => {
        setIsGraduate(statuss);
    };

    const handleIsDMChange = (statuss) => {
        setIsDM(statuss);
    };

    return (
        <div className="full-screen-container">
            <div className="singup-container">
                <div className="hu_logo">
                    <img src={hu_logo} alt="" />
                </div>
                <div className="signup-form">
                <div className="role-selection">
                    <label className="radioStudent"><input
                        type="radio"
                        value="Student"
                        checked={!isDM}
                        onChange={() => handleIsDMChange(false)}
                    />Student
                    </label>

                    <label className="radioDM"><input
                        type="radio"
                        value="Department Manager"
                        checked={isDM}
                        onChange={() => handleIsDMChange(true)}
                    />Department Manager 
                    </label>

                </div>
                <div className="input-group">
                    <div className="name-fields">
                        <input
                            type="text"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            placeholder="Name" />
                        <input
                            type="text"
                            value={surname}
                            onChange={(e) => setSurname(e.target.value)}
                            placeholder="Surname" />
                    </div>
                    <input
                        type="email"
                        className="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="E-mail Address"
                    />

                    <div className="department-container">
                        <label>Department:</label>
                        <form action="#" className="department-dropdown" method="POST" style={{ display: 'inline-block' }}>
                            <div className="select-box">
                                <div className="options-container">
                                    <select className="option" id="NONE" value={selectedOption} onChange={handleDepartmentChange}>
                                        <option value={0}>{"Choose a department"}</option>
                                        { departments.map((department) => (
                                            <option key={department.id} value={department.id}>{department.name}</option>
                                        ))}
                                    </select>
                                </div>
                            </div>
                        </form>
                    </div>

                    <div className="educationSelection">
                        <label>Degree:</label>
                        <label><input
                            type="radio"
                            className="radioButton"
                            value="Undergraduate"
                            checked={!isGraduate}
                            onChange={() => handleIsGraduateChange(false)}
                        />Undergraduate
                        </label>

                        <label><input
                            type="radio"
                            className="radioButton"
                            value="Graduate"
                            checked={isGraduate}
                            onChange={() => handleIsGraduateChange(true)}
                        />Graduate
                        </label>

                    </div>
                    </div>
                </div>
                <div className="login-contaniner">
                    <label>If you already have an account <a href="#" className="login" onClick={() => navigate('/')}>Login</a></label>
                </div>

                <button type="button" className="sign-button" onClick={(event) => {event.preventDefault(); handleSubmitButton();}}>Sign Up</button>
            </div>
        </div>
    );
};

export default SignUp;
