import { useState, useEffect, useRef } from 'react';
import styles from "../../components/topbar.module.css";
import ices4hu_logo from '../../assets/ices4hu_logo.png';
import notifications_logo from '../../assets/notifications_logo.png';
import pp_placeholder from '../../assets/pp_placeholder.png'
import { useNavigate } from 'react-router-dom';
import Notifications_Table from "./NotificationsTable.jsx";
import Settings from "../settingsStudent/settingsStudent";

function TopBarStudent (props)  {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isPopupOpen, setIsPopupOpen] = useState(false);
  const [notificationList, setNotificationList] = useState([]);
  const [isLoaded, setIsLoaded] = useState(false);
  const [error, setError] = useState(null);
  const [userInfo, setUserInfo] = useState([]);
  const [pp, setPP] = useState(null);

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  }

    const togglePopup = () => {
      setIsPopupOpen(!isPopupOpen);
    };

  const navigate = useNavigate();
  const handleClick = () => {
    navigate('/mainPageStudent');
  };


  useEffect(() => {
    
    setPP(userInfo.base64);

  }, [userInfo])


 useEffect(() => {
        fetchNotifications();
        fetchUserInformation();
    }, [])

      const openPopup = () => {
        document.getElementById("popup").style.display = "block";
      }

      const closePopup = () => {
        document.getElementById("popup").style.display = "none";
        setIsPopupOpen(!isPopupOpen);
      }


  const handleLogout = () => {
    alert('You are logging out.');
    localStorage.removeItem("loginID");
    localStorage.removeItem("authToken");
    navigate('/');
  }
const fetchNotifications = () => {
        fetch("/api/survey/student/upcoming?user=" + localStorage.getItem("loginID"), {
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
                setNotificationList(result);
                setIsLoaded(true);
            })
            .catch((err) => {
                setIsLoaded(true);
                console.log("Error:", err);
            });
    };


  const handleDelete = () => {
    
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
        //localStorage.removeItem("loginID");    // flush local storage
        localStorage.removeItem("userID");
        //localStorage.removeItem("authToken");
        // LOGOUT
        navigate('/');  // restart
      })
      .catch((err) => {
        console.log("Error:", err);
      });
      navigate('/');
      alert("Account has been deleted.")
  }

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
        console.log(userInfo.id);
      })
      .catch((err) => {
          setIsLoaded(true);
          console.log("Error:", err);
      });



  };



  return (
    <div className={styles.topbar}>
      <div className={styles.topbarflex}>
        <div></div>
        <div>
          <img
            onClick={handleClick}
            src={ices4hu_logo}
            alt=""
            style={{ width: "100%", cursor: "pointer" }}
          />
        </div>
        <div>
          <div className={styles.notification_logo}>
            <img
              width={30}
              src={notifications_logo}
              alt=""
              onClick={togglePopup}
              style={{ cursor: "pointer" }}
            />
          </div>

          <div>
          
          </div>
          <div className={styles.dropdown}>
            <img
              onClick={toggleMenu}
              width={50}
              src={pp === null || pp === undefined ? pp_placeholder : pp}
              alt=""
              style={{ cursor: "pointer" }}
            />
            {isMenuOpen && (
              <div className={styles.dropdownMenu}>
                <button
                  onClick={() => navigate('/accountSettingsStudent')}
                  className={styles.deleteUserBtn}
                >
                  Settings
                </button>
                <button
                    onClick={() => navigate('/deleteAccountStudent')}
                  className={styles.deleteUserBtn}
                >
                  Delete Account
                </button>
                <button
                  onClick={handleLogout}
                  className={styles.logoutBtn}
                >
                  Logout
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
      <hr className={styles.linebar} />

     {isPopupOpen && (
       <div className={styles.popup} id="popup">
         <div className={styles.popupcontent} id="popup-content">
           <span className="close-popup" onClick={closePopup}>&#10005;</span>
           <h2>Surveys Due Soon</h2>
           <table className={styles.notificationTable}>
             <thead>
               <tr>
                 <th>Course</th>
                 <th>Instructor</th>
                 <th>Date</th>
               </tr>
             </thead>
                 <tbody className={styles.td}>

                     {notificationList.map(notification => (
                     <Notifications_Table key={notification.id}
                         courseName = {notification.courseName}
                         instructorName = {notification.instructorName}
                         deadline = {notification.deadline}
                          />
                 ))}

                 </tbody>
           </table>
         </div>
       </div>
     )}
    </div>
  );
};

export default TopBarStudent;