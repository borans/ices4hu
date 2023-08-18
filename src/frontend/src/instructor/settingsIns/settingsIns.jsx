import React, { useEffect, useState } from "react";
import styles from "./settings.module.css";
import SideBar from "../sidebarInstructor/sidebarInstructor";
import TopBar from "../topbarInstructor/topbarInstructor";
import defaultProfilePicture from "../../images/user.png";
import { useNavigate} from "react-router-dom";

function Settings() {

  

  let navigate = useNavigate();
  const [name, setName] = useState("");
  const [surname, setSurname] = useState("");
  const [departmentName, setDepartmentName] = useState("");
  const [loginId, setLoginId] = useState("");
  const [email, setEmail] = useState("");
  const [profilePicture, setProfilePicture] = useState(defaultProfilePicture);
  const [userInfo, setUserInfo] = useState([]);
  const [error, setError] = useState(null);
  const [isLoaded, setIsLoaded] = useState(false);






  useEffect(() => {

    setName(userInfo.name);
    setSurname(userInfo.surname);
    setDepartmentName(userInfo.departmentName);
    setLoginId(userInfo.loginId);
    setEmail(userInfo.email);
    setProfilePicture(userInfo.base64);

  }, [userInfo])

  useEffect(() => {
    fetchUserInformation()
  }, [])


  



  let HACKINGTRY = localStorage.getItem("loginID") == null ? true : false;

  const handleNameChange = (event) => {
    setName(event.target.value);
    //fetchUserInformation();
    
  };


  const handleSurnameChange = (event) => {
    setSurname(event.target.value);
  };


  const handleLoginIdChange = (event) => {
    setLoginId(event.target.value);

  };



  const handleProfilePictureChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        setProfilePicture(reader.result);
      };
      reader.readAsDataURL(file);
    }

  };
  

  const handleSave = () => {

    fetch("/api/users?loginId=" + localStorage.getItem("loginID"), {
      method: "PUT",
      headers: {
          "Content-Type": "application/json",
          "Authorization": localStorage.getItem("authToken")
      },
      body: JSON.stringify({
        name: name,
        surname: surname,
        base64: profilePicture
      }),
  })
      .then((res) => {
          if (res.ok)
              return res.json();
          else
              throw new Error("AN ISSUE CONNECTING TO BACKEND");
      }
      )
      .then(result => {
        alert("Changes are saved.");
        navigate("/mainPageInstructor");
   
      })
      .catch((err) => {
          setIsLoaded(true);
          console.log("Error:", err);
      }); 

  };



  const fetchUserInformation = () => {

    fetch("/api/users?loginId=" + localStorage.getItem("loginID"), {
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
        setUserInfo(result);
        setIsLoaded(true);
      })
      .catch((err) => {
          setIsLoaded(true);
          console.log("Error:", err);
      });



  };

  if (HACKINGTRY) {
    return <div> ACCESS DENIED </div>;
}  else if (error) {
  return <div> Error !!!</div>;
} else if (!isLoaded) {
  return <div> Loading... </div>; 
}else {

  return (
    <div>
      <TopBar />
      <div>
        <div>
          <SideBar />
        </div>
        <div className={styles.containerSettings}>
          <h2>Manage Account</h2>
          <div className={styles.inputBox}>
            <label className={styles.label}>Profile Picture:</label>
            <div className={styles.profilePictureContainer}>
                <div className={styles.profilePicture}>
                                <img src={profilePicture === null || profilePicture === undefined ? defaultProfilePicture : profilePicture} alt="Profile" className={styles.profileImage} />
                </div>
              <input
                type="file"
                accept="image/png, image/jpeg"
                className={styles.fileInput}
                id="profilePictureInput"
                onChange={handleProfilePictureChange}
              />
            </div>
          </div>
          <div className={styles.inputBox}>
            <label className={styles.label}>Name:</label>
            <div className={styles.inputContainer}>
              <input
                type="text"
                value={name}
                onChange={handleNameChange}
                className={styles.input}
              />
            </div>
          </div>
          <div className={styles.inputBox}>
            <label className={styles.label}>Surname:</label>
            <div className={styles.inputContainer}>
              <input
                type="text"
                value={surname}
                onChange={handleSurnameChange}
                className={styles.input}
              />
            </div>
          </div>
          <div className={styles.inputBox}>
            <label className={styles.label}>Department:</label>
            <div className={styles.inputContainer}>
              <input
                type="text"
                value={departmentName}
                readOnly
                className={styles.input}
              />
            </div>
          </div>
          <div className={styles.inputBox}>
            <label className={styles.label}>Login ID:</label>
            <div className={styles.inputContainer}>
              <input
                type="text"
                value={loginId}
                readOnly
                className={styles.input}
              />
            </div>
          </div>
          <div className={styles.inputBox}>
            <label className={styles.label}>Email:</label>
            <div className={styles.inputContainer}>
              <input
                type="text"
                value={email}
                readOnly
                className={styles.inputReadOnly}
              />
            </div>
          </div>
          <button className={styles.actionButton} onClick={(event) => {event.preventDefault(); handleSave();}}>
            Save
          </button>
        </div>
      </div>
    </div>
  );




}

 

  
}

export default Settings;