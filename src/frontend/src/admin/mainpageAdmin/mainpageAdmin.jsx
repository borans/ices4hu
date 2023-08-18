import React from "react";
import TopBar from "../topbarAdmin/topbarAdmin";
import { useNavigate } from "react-router-dom";
import {logout} from "../../components/logout";
import SideBar from "../sidebarAdmin/sidebarAdmin";
import styles from "../mainpageAdmin/mainpageAdmin.module.css"
import myCourses_logo from '../../assets/myCourses_logo.jpg';
import accounts_logo from '../../assets/accounts_logo.png'; 
import survey_logo from '../../assets/survey_logo.png'; 
import schedule_logo from '../../assets/schedule_logo.jpg';

function MainPageAdmin() {
  let navigate = useNavigate();


  let HACKINGTRY = localStorage.getItem("loginID") == null ? true : false;
  if (HACKINGTRY) {
    return <div className={styles.label}> ACCESS DENIED </div>;
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
        
  
        <div>
          <div className={styles.ul}>
            <div className={styles.li}>
              <div className={styles.coursesDiv}>
                <img src={myCourses_logo} alt="" width="20%" height="20%"  border-top-left-radius= "10px" border-top-right-radius="10px" />
                <div onClick={() => navigate('/adminCourses')} className={styles.course}>Courses</div>
              </div>

              <div className={styles.surveysDiv}>
                <img src={accounts_logo} alt="" width="20%" height="20%"  />
                <div onClick={() => navigate('/viewUserAndAddInstructor')} className={styles.reg}>Accounts</div>
              </div>
            </div>
            <div className={styles.li}>
              <div className={styles.regDiv}>
                <img src={survey_logo} alt="" width="20%" height="20%"  />
                <div onClick={() => navigate('/adminSurveys')} className={styles.surveys}>Surveys</div>
              </div>

              <div className={styles.scheduleDiv}>
                <img src={schedule_logo} alt="" width="20%" height="20%"  />
                <div onClick={() => navigate('/adminSchedule')} className={styles.schedule}>Schedule</div>
              </div>
              
            </div>
          </div>
        </div>
        



      </div>
    );
  }
}

export default MainPageAdmin;