import { useState, useEffect } from 'react';
import styles from "../../components/topbar.module.css";
import ices4hu_logo from '../../assets/ices4hu_logo.png';
import notifications_logo from '../../assets/notifications_logo.png';
import pp_placeholder from '../../assets/pp_placeholder.png'
import { useNavigate } from 'react-router-dom';

function TopBarManager ()  {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [userInfo, setUserInfo] = useState([]);
  const [pp, setPP] = useState(null);
  const [isLoaded, setIsLoaded] = useState(false);
  const [error, setError] = useState(null);
  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  }
  const navigate = useNavigate();
  const handleClick = () => {
    navigate('/mainPageManager');
  };

  
  useEffect(() => {
    
    setPP(userInfo.base64);

  }, [userInfo])

  useEffect(() => {
    fetchUserInformation();
}, [])





  const handleLogout = () => {
    alert('You are logging out.');
    localStorage.removeItem("loginID");
    localStorage.removeItem("authToken");
    navigate('/');
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



  return (
    <div className={styles.topbar}>
      <div className={styles.topbarflex}>
        <div></div>
        <div><img onClick={handleClick} src={ices4hu_logo} alt="" style={{width: "100%",cursor: "pointer" }} /></div>
        <div>
          <div className={styles.notification_logo}>
            <img width={30} src={notifications_logo} alt="" />
          </div>

          <div className={styles.dropdown}>
          <img onClick={toggleMenu} width={50} src={pp === null || pp === undefined ? pp_placeholder : pp}
              alt=""
              style={{ cursor: "pointer" }} />
          {isMenuOpen && (
            <div className={styles.dropdownMenu}>
              <button onClick={(event) => {event.preventDefault(); navigate('/accountSettingsDM');}} className={styles.deleteUserBtn}>Settings</button>
              <button onClick={(event) => {event.preventDefault(); navigate('/deleteAccountDM');}} className={styles.deleteUserBtn}>Delete Account</button>
              <button onClick={(event) => {event.preventDefault(); handleLogout();}} className={styles.logoutBtn}>Logout</button>
            </div>
          )}
      </div>
        </div>
      </div>
      <hr className={styles.linebar}/>
    </div>


    //<div style={{textAlign: 'center', backgroundColor: 'red', color: 'white'}}>
    //  <h1>This is the top bar</h1>
    //</div>
  );
};

export default TopBarManager;