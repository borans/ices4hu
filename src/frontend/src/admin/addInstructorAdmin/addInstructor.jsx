import React, { useState, useEffect } from "react";
import "./addInstructor.css";
import TopBar from "../topbarAdmin/topbarAdmin";
import { useNavigate} from "react-router-dom";
import {logout} from "../../components/logout";
import SideBar from "../sidebarAdmin/sidebarAdmin";


function AddAndViewInstructor() {
  let navigate = useNavigate();

  const openPopup = () => {
    document.getElementById("popup").style.display = "block";
  }

  const closePopup = () => {
    document.getElementById("popup").style.display = "none";
  }

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


  const [name, setName] = useState("");
  const [surname, setSurname] = useState("");
  const [email, setEmail] = useState("");
  const [loginId, setLoginId] = useState("");
  const [selectedOption, setSelectedOption] = useState(0);
  const [departments, setDepartments] = useState([]);
  const [users, setUsers] = useState([]);

  useEffect(() => {
    getDepartments()
    getUsers()
}, [])

const getUsers = () => {

  fetch("/api/users/admin", {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      "Authorization": localStorage.getItem("authToken")
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
      setUsers(result);
    })
    .catch((err) => {
      console.log("Error:", err);
    });



}

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


const removeUser = (e) => {

  fetch("/api/users/admin?user=" + String(e.target.value), {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
      "Authorization": localStorage.getItem("authToken")
    },
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
      result.json.then((json) => {
        alert(json.message);
      })


      console.log(e.target.value);
      setUsers((current) => 
                    current.filter((user) => user.id !== Number(e.target.value)));
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


const handleBan = (e) => {

  fetch("/api/users/admin/ban?user=" + String(e.target.value), {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": localStorage.getItem("authToken")
    },
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
      result.json.then((json) => {
        alert(json.message);
      })

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

const handleRemoveBan = (e) => {

  fetch("/api/users/admin/remove_ban?user=" + String(e.target.value), {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": localStorage.getItem("authToken")
    },
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
      result.json.then((json) => {
        alert(json.message);
      })

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


const handleSubmitButton = (e) => {
  console.log(JSON.stringify({
      name: name,
      surname: surname,
      loginId: loginId,
      email: email,
      departmentId: selectedOption,
    }));
  e.preventDefault();

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
  window.location.reload();
};


const sendSignRequest = () => {
  fetch("/api/users/admin/create_instructor", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": localStorage.getItem("authToken")
    },
    body: JSON.stringify({
      name: name,
      surname: surname,
      loginId: loginId,
      email: email,
      departmentId: Number(selectedOption),
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
          
          result.json.then((json) => {
            alert(json.message);
          })

          closePopup();
      } else {
          result.json.then((json) => {
              alert(json.message);
          })
      }
    })
    .catch((err) => {
      console.log("Error:", err);
    });

    setEmail("");
    setName("");
    setSurname("");
    setLoginId("");
    setSelectedOption(0);
}



const handleDepartmentChange = (event) => {
  setSelectedOption(event.target.value);
};




  let HACKINGTRY = localStorage.getItem("loginID") == null ? true : false;
  if (HACKINGTRY) {
    return <div> ACCESS DENIED </div>;
  } else {
    // TEMP DEL ACCOUNT BUTTON AFTER MYSURVEY'S BUTTON!

  return (

     <div>
            <div>
              <SideBar />
            </div>
            <div>
              <TopBar />
            </div>
            <div className="container">
              <h1 className="page-title">Accounts</h1>
              <div className="instructor-btn">
                <div className="ins-btn">
                  <button className="add-instructor-button" onClick={(event) => {event.preventDefault(); openPopup();}}>Add Instructor</button>
                  <div id="popup" className="popup">
                    <div className="popup-content">
                      <span className="close-popup" onClick={closePopup}>&#10005;</span>
                      <h2>Add Instructor</h2>
                      <form>
                        <label htmlFor="name">Name:</label>
                        <input type="text" id="name" name="name" value={name} onChange={(e) => setName(e.target.value)}/>
                        <label htmlFor="surname">Surname:</label>
                        <input type="text" id="surname" name="surname" value={surname} onChange={(e) => setSurname(e.target.value)}/>
                        <label htmlFor="loginId">Login ID:</label>
                        <input type="text" id="loginId" name="loginId" value={loginId}  onChange={(e) => setLoginId(e.target.value)} />
                        <label htmlFor="email">Email Address:</label>
                        <input type="email" id="email" name="email"  value={email} onChange={(e) => setEmail(e.target.value)} />
                        <label htmlFor="department">Department:</label>
                        <div className="department-container">
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
                        <button type="submit" className="action-button" onClick={handleSubmitButton}>Create Account and Send Email</button>
                      </form>
                    </div>
                  </div>
                </div>


              <table className="instructor-table">
                <thead>
                  <tr>
                    <th>User Type</th>
                               
                    <th>Name</th>
                    <th>Surname</th>
                    <th>Login ID</th>
                    <th>E-mail Address</th>
                    <th>Remove</th>
                    <th>Ban</th>
                    <th>Remove Ban</th>
                  </tr>
                </thead>
                <tbody>
                  {console.log(users)}
                {users.map(user => (
                <tr>
                  <td>{user.userType}</td>
                  <td>{user.name}</td>
                  <td>{user.surname}</td>
                  <td>{user.loginId}</td>
                  <td>{user.email}</td>
                  <td><button className="action-button" value={user.id} onClick={removeUser}>Remove</button></td>
                  <td><button className="action-button" value={user.id} onClick={handleBan}>Ban</button></td>
                  <td><button className="action-button" value={user.id} onClick={handleRemoveBan}>Remove Ban</button></td>
                </tr>
              ))}
                </tbody>
              </table>

            <div>  
            <button type="submit" className="enrol-button" 
            onClick={(event) => {event.preventDefault(); navigate(`/userEnrolmentRequests`)}}
            >See User Enrolment Requests</button>

            <button type="submit" className="enrol-button" 
            onClick={(event) => {event.preventDefault(); navigate(`/courseEnrolmentRequest`)}}>See Course Enrolment Requests</button>

            </div>

            </div>
         </div>
      </div>
  );
}
 }
export default AddAndViewInstructor;